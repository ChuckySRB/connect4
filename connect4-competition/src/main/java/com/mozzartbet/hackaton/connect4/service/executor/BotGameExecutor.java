package com.mozzartbet.hackaton.connect4.service.executor;

import static com.google.common.base.Preconditions.*;
import static com.mozzartbet.hackaton.connect4.competition.Game.Outcome.*;
import static java.lang.System.*;
import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozzartbet.hackaton.connect4.competition.Game;
import com.mozzartbet.hackaton.connect4.competition.Game.Outcome;
import com.mozzartbet.hackaton.connect4.competition.Move;
import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

public class BotGameExecutor implements GameExecutor {

	static final Logger logger = LoggerFactory.getLogger(BotGameExecutor.class);

	final long timeoutMillis;

	final GameBoard board = new GameBoard();
	final BotLoader loader = new BotLoader();

	Player playerOne;
	Player playerTwo;

	public BotGameExecutor(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	@Override
	public Game runGame(Game game, Runnable onMove) {
		preparePlayers(game);

		game.start();

		do {
			if (!makeMove(game, 1, onMove)) {
				game.end(WINNER_TWO);
				return game;
			}

			if (board.isGameOver()) {
				break;
			}

			if (!makeMove(game, 2, onMove)) {
				game.end(WINNER_ONE);
				return game;
			}
		} while (!board.isGameOver());

		int winner = board.getWinner();
		playerOne.finished(winner);
		playerTwo.finished(winner);

		game.end(outcome(winner));
		onMove.run();
		
		return game;
	}

	private boolean makeMove(Game game, int counter, Runnable onMove) {
		Player player = (counter == 1) ? playerOne : playerTwo;
		Player opponent = (counter == 1) ? playerTwo : playerOne;

		player.move();
		int move = player.getMove();

		boolean valid = board.placeCounter(move, counter);
		saveMove(game, counter, move, valid);
		onMove.run();

		opponent.opponentMove(move);

		return valid;
	}

	private Move saveMove(Game game, int counter, int move, boolean valid) {
		Move m = new Move(counter, move, valid);
		if (valid) {
			com.mozzartbet.hackaton.connect4.model.Move mm = board.getLastMove();
			checkState(mm.getCounter() == m.getCounter());
			m.setRow(mm.getRow());
			m.setCol(mm.getCol());
		}
		game.getMoves().add(m);

		logger.debug("---- {}", m);
		return m;
	}

	private Outcome outcome(int winner) {
		switch (winner) {
		case 0:
			return DRAW;
		case 1:
			return WINNER_ONE;
		case 2:
			return WINNER_TWO;
		default:
			return UNKNOWN;
		}

	}

	private void preparePlayers(Game game) {
		playerOne = preparePlayer(game.getOne());
		playerTwo = preparePlayer(game.getTwo());
	}

	private Player preparePlayer(String name) {
		Player bot = loader.loadPlayer(name);
		Player adapter = new PlayerAdapter(bot, timeoutMillis);
		adapter.configure(timeoutMillis);
		return adapter;
	}

	//

	static class PlayerAdapter extends Player {
		private static final int paddingMillis = 100;

		final Player delegate;
		final long timeoutMillis;

		ExecutorService executor;

		PlayerAdapter(Player delegate, long timeoutMillis) {
			this.delegate = delegate;
			this.timeoutMillis = timeoutMillis;
			this.executor = newExecutor();
		}

		private ExecutorService newExecutor() {
			return Executors.newFixedThreadPool(1);
		}

		@Override
		public void configure(long timeoutMillis) {
			withTimeout(() -> delegate.configure(timeoutMillis));
		}

		@Override
		public void move() {
			withTimeout(() -> delegate.move());
			move = delegate.getMove();
		}

		volatile boolean stopping = false;

		@Override
		public void stop() {
			logger.error("========== Stopping: " + delegate);
			stopping = true;
			withTimeout(() -> {
				delegate.stop();
				stopping = false;
			});
		}

		@Override
		public void opponentMove(int move) {
			withTimeout(() -> delegate.opponentMove(move));
		}

		@Override
		public void finished(int winner) {
			withTimeout(() -> delegate.finished(winner));
		}

		private void withTimeout(Runnable r) {
			long t1 = currentTimeMillis();

			try {
				executor.submit(r).get(timeoutMillis + paddingMillis, MILLISECONDS);
			} catch (InterruptedException | ExecutionException e) {
			} catch (TimeoutException e) {
				long t2 = currentTimeMillis();
				logger.error("========== Timeout [{}/{}] {}", t2 - t1, timeoutMillis, delegate);

				executor.shutdownNow();
				executor = newExecutor();

				if (!stopping) {
					stop();
				}
			}
		}
	}

	//

	public GameBoard getBoard() {
		return board;
	}
}
