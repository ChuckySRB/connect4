package com.mozzartbet.hackaton.connect4.bot.abii;

import static com.mozzartbet.hackaton.connect4.model.Direction.E;
import static com.mozzartbet.hackaton.connect4.model.Direction.NE;
import static com.mozzartbet.hackaton.connect4.model.Direction.NW;
import static com.mozzartbet.hackaton.connect4.model.Direction.S;
import static com.mozzartbet.hackaton.connect4.model.Direction.SE;
import static com.mozzartbet.hackaton.connect4.model.Direction.SW;
import static com.mozzartbet.hackaton.connect4.model.Direction.W;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.IN_A_ROW;

import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.*;

public class Bot extends Player {
	GameBoard myBoard = new GameBoard();
	int opponent = 1;
	int me;
	int minDepth = 5;
	long timeout;

	@Override
	public void configure(long timeoutMillis) {
		myBoard = new GameBoard();
		me = 0;
		opponent = 1;
		timeout = timeoutMillis;
	}

	@Override
	public void move() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		move = rnd.nextInt(COLUMNS);
		firstMove();
		tryAndSee(me);
		tryAndSee(opponent);
		myBoard.placeCounter(move, me);

	}

	@Override
	public void stop() {
		myBoard.placeCounter(move, me);
	}

	@Override
	public void opponentMove(int move) {
		myBoard.placeCounter(move, opponent);
	}

	@Override
	public void finished(int winner) {
		// TODO Auto-generated method stub
		myBoard.reset();
		me = 0;
		opponent = 1;

	}

	public void firstMove() {
		if (myBoard.getCountersPlaced() == 0) {
			me = 1;
			opponent = 2;
			move = 3;
		} else if (myBoard.getCountersPlaced() == 1) {
			me = 2;
			opponent = 1;
			move = 3;
			if (myBoard.getLastMove().getCol() == 3)
				move = 4;
		}
	}

	public void tryAndSee(int id) {

		GameBoard local = myBoard.deepCopy();
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			local.placeCounter(i, opponent);
			boolean tmp = checkWin(local, id);
			if (tmp) {
				move = i;
				return;
			}
			local.undoMove();
		}

	}

	public boolean checkWin(GameBoard local, int counter) {
		int maxCount = IN_A_ROW - 1;

		int row = local.getMoves().peek().getRow();
		int col = local.getMoves().peek().getCol();

		int count = local.countConnected(row + 1, col, S, counter);
		if (count >= maxCount) {
			return true;
		}

		count = local.countConnected(row, col + 1, E, counter) + local.countConnected(row, col - 1, W, counter);
		if (count >= maxCount) {
			return true;
		}

		count = local.countConnected(row - 1, col + 1, NE, counter)
				+ local.countConnected(row + 1, col - 1, SW, counter);
		if (count >= maxCount) {
			return true;
		}

		count = local.countConnected(row - 1, col - 1, NW, counter)
				+ local.countConnected(row + 1, col + 1, SE, counter);
		if (count >= maxCount) {
			return true;
		}

		return false;
	}

	public boolean blockOpponent() {
		boolean amBlocking = false;
		int maxBlocked = 0;
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				int tmp = myBoard.countConnected(i, j, Direction.E, opponent);
				if (tmp >= 2 && maxBlocked < tmp) {
					if (j > 0 && myBoard.getBoard()[i][j - 1] == 0) {
						maxBlocked = tmp;
						move = j - 1;
						amBlocking = true;
					} else if (j < 6 && myBoard.getBoard()[i][j + tmp] == 0) {
						maxBlocked = tmp;
						move = j + tmp;
						amBlocking = true;
					}

				}
			}
		}
		if (amBlocking)
			return amBlocking;
		for (int i = 5; i >= 3; i--) {
			for (int j = 0; j < 8; j++) {
				int tmp = myBoard.countConnected(i, j, Direction.N, opponent);
				if (tmp >= 2 && maxBlocked < tmp) {
					maxBlocked = tmp;
					move = j;
					amBlocking = true;

				}
			}
		}
		return amBlocking;

	}

	public void connectMe() {
		int maxConnected = 0;
		for (int i = 5; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				int tmp = myBoard.countConnected(i, j, Direction.E, me);
				if (maxConnected < tmp) {
					maxConnected = tmp;
					if (j > 0 && myBoard.getBoard()[i][j - 1] == 0)
						move = j - 1;
					else if (j < 6 && myBoard.getBoard()[i][j + tmp] == 0)
						move = j + tmp;
				}
			}
		}
		for (int j = 0; j < 7; j++) {
			int tmp = myBoard.countConnected(5, j, Direction.N, me);
			if (maxConnected < tmp) {
				maxConnected = tmp;
				move = j;
			}
		}

	}

}
