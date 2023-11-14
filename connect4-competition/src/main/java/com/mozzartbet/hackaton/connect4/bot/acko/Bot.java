package com.mozzartbet.hackaton.connect4.bot.acko;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;

import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.Direction;
import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Move;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

	private Object lock;

	private static final int IMPOSSIBLE_MOVE = -1;

	private static final int PLAYER = 1;
	private static final int OPPONENT = 2;

	private GameBoard gameBoard, advancedGameBoard;
	private int advancedPlayerWins;
	private int advancedOpponentWins;
	private int availableMoves;

	private boolean firstMove;
	private int currentMove;
	private boolean zerg;
	private int firstMoves[];

	@Override
	public void configure(long timeoutMillis) {
		gameBoard = new GameBoard();
		availableMoves = GameConsts.COLUMNS * GameConsts.ROWS;
		// TODO(acko): Synchronization?
		lock = new Object();
		zerg = false;
		currentMove = 0;
		firstMove = true;
		firstMoves = new int[] { 4, 5, 6, 3, 5, 5, 5 };
	}

	public int getKillerMove(int player) {
		int killerMove = IMPOSSIBLE_MOVE;
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			boolean possible = gameBoard.placeCounter(i, player);
			boolean finished = gameBoard.isGameOver();
			if (possible && finished) {
				killerMove = i;
			}
			gameBoard.undoMove();
		}
		return killerMove;
	}

	public int getSimpleMove(int player) {
		int opponent = 3 - player;
		int simpleMove = IMPOSSIBLE_MOVE;
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			boolean possiblePlayer = gameBoard.placeCounter(i, player);
			if (possiblePlayer) {
				int killerMoveOpponent = getKillerMove(opponent);
				// System.out.println("killerMoveOpponent: " +
				// killerMoveOpponent);
				if (killerMoveOpponent != IMPOSSIBLE_MOVE) {
					// System.out.println("killerMoveOpponent found!");
				} else {
					simpleMove = i;
				}
			}
			gameBoard.undoMove();
		}
		return simpleMove;
	}

	public int getPossibleMove(int player) {
		int possibleMove = IMPOSSIBLE_MOVE;
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			boolean possible = gameBoard.placeCounter(i, player);
			if (possible) {
				possibleMove = i;
			}
			gameBoard.undoMove();
		}
		return possibleMove;
	}

	public boolean canWin(int player, int count) {
		if (count == 0) {
			return false;
		}
		int opponent = 3 - player;
		boolean allFalse = false;
		for (int i = 0; i < COLUMNS; i++) {
			boolean possible = advancedGameBoard.placeCounter(i, player);
			if (possible) {
				boolean finished = advancedGameBoard.isGameOver();
				if (finished) {
					return true;
				}
				boolean opponentResult = canWin(opponent, count - 1);
				allFalse |= opponentResult;
			}
		}
		return !allFalse;
	}

	public int getAdvancedMove(int player) {
		int opponent = 3 - player;
		advancedGameBoard = gameBoard.deepCopy();
		int advancedMove = IMPOSSIBLE_MOVE;
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			boolean possible = advancedGameBoard.placeCounter(i, player);
			if (!possible) {
				continue;
			}
			boolean can = canWin(opponent, 3);
			if (!can) {
				advancedMove = i;
			}
			System.out.println("i = " + i + " can = " + can);
			// System.out.println("advanced: " + i + " cntPlayer: " + cntPlayer
			// + " cntOpponent: " + cntOpponent);
			advancedGameBoard.undoMove();
		}
		return advancedMove;
	}

	public int getExpertMove(int player) {
		int[][] board = gameBoard.getBoard();
		GameBoard gmBrd = gameBoard.deepCopy();
		int expertMove = IMPOSSIBLE_MOVE;

		int idxMaximum = IMPOSSIBLE_MOVE;
		int maximumScore = 0;
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			boolean possible = gmBrd.placeCounter(i, player);
			if (!possible) {
				gmBrd.undoMove();
				continue;
			}
			Move move = gmBrd.getLastMove();
			int n = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.N, player);
			int s = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.S, player);
			int w = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.W, player);
			int e = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.E, player);
			int nw = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.NW, player);
			int ne = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.NE, player);
			int sw = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.SW, player);
			int se = gmBrd.countConnected(move.getRow(), move.getCol(), Direction.SE, player);

			int score = n + s + w + e + nw + ne + sw + se;

			if ((idxMaximum == IMPOSSIBLE_MOVE) || (score > maximumScore)) {
				maximumScore = score;
				idxMaximum = i;
			}
		}
		expertMove = idxMaximum;
		return expertMove;
	}

	@Override
	public void move() {
		if (firstMove) {
			firstMove = false;
			zerg = true;
		}
		System.out.println("move");

		int killerMove = getKillerMove(PLAYER);
		System.out.println("killerMove: " + killerMove);
		if (killerMove != IMPOSSIBLE_MOVE) {
			move = killerMove;
			gameBoard.placeCounter(move, PLAYER);
			System.out.println("killerMove found!");
			availableMoves--;
			return;
		}

		if (zerg) {
			int zergMove = firstMoves[currentMove % firstMoves.length];
			currentMove = currentMove + 1;
			System.out.println("zergMove: " + zergMove);
			move = zergMove;
			gameBoard.placeCounter(move, PLAYER);
			availableMoves--;
			return;
		}

		int advancedMove = getAdvancedMove(PLAYER);
		System.out.println("advancedMove: " + advancedMove);
		if (advancedMove != IMPOSSIBLE_MOVE) {
			move = advancedMove;
			gameBoard.placeCounter(move, PLAYER);
			System.out.println("advancedMove found!");
			availableMoves--;
			return;
		}

		int simpleMove = getSimpleMove(PLAYER);
		System.out.println("simpleMove: " + simpleMove);
		if (simpleMove != IMPOSSIBLE_MOVE) {
			move = simpleMove;
			gameBoard.placeCounter(move, PLAYER);
			System.out.println("simpleMove found!");
			availableMoves--;
			return;
		}

		int expertMove = getExpertMove(PLAYER);
		System.out.println("expertMove: " + expertMove);
		if (expertMove != IMPOSSIBLE_MOVE) {
			move = expertMove;
			gameBoard.placeCounter(move, PLAYER);
			System.out.println("expertMove found!");
			availableMoves--;
			return;
		}

		int possibleMove = getPossibleMove(PLAYER);
		System.out.println("possibleMove: " + possibleMove);
		if (possibleMove != IMPOSSIBLE_MOVE) {
			move = possibleMove;
			gameBoard.placeCounter(move, PLAYER);
			System.out.println("possibleMove found!");
			availableMoves--;
			return;
		}

		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		move = threadLocalRandom.nextInt(COLUMNS);
		System.out.println("randomMove: " + move);
		gameBoard.placeCounter(move, PLAYER);
		availableMoves--;
	}

	@Override
	public void stop() {
	}

	@Override
	public void opponentMove(int move) {
		if (firstMove) {
			firstMove = false;
			zerg = false;
		}
		System.out.println("opponentMove: " + move);
		gameBoard.placeCounter(move, OPPONENT);
		availableMoves--;
	}

	@Override
	public void finished(int winner) {
		System.out.println("finished: " + winner);
	}

}
