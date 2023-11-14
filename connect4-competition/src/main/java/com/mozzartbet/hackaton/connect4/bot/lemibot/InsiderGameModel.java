package com.mozzartbet.hackaton.connect4.bot.lemibot;

import java.util.Stack;

import com.mozzartbet.hackaton.connect4.model.GameConsts;

public class InsiderGameModel {
	private class Coordinates {
		int i;
		int j;

		Coordinates(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}

	private final static int ROWS = GameConsts.ROWS;
	private final static int COLS = GameConsts.COLUMNS;

	private int[][] table = new int[ROWS][COLS];
	private int currentPlayer = 1;

	Stack<Coordinates> stack = new Stack<>();

	public InsiderGameModel() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				table[i][j] = 0;
			}
		}
	}

	private Score calculateConsecutives(int limit) {
		Score score = new Score();
		for (int j = 0; j < COLS; j++) {
			for (int i = limit - 1; i < ROWS; i++) {
				if (table[i][j] == 0) {
					continue;
				}
				boolean same = true;
				for (int k = 1; k < limit; k++) {
					if (table[i - k + 1][j] != table[i - k][j]) {
						same = false;
					}
				}
				if (same) {
					score.increase(table[i][j]);

				}
			}
		}

		for (int i = 0; i < ROWS; i++) {
			for (int j = limit - 1; j < COLS; j++) {
				if (table[i][j] == 0) {
					continue;
				}
				boolean same = true;
				for (int k = 1; k < limit; k++) {
					if (table[i][j - k + 1] != table[i][j - k]) {
						same = false;
					}
				}
				if (same) {
					score.increase(table[i][j]);
				}
			}
		}

		for (int i = limit - 1; i < ROWS; i++) {
			for (int j = limit - 1; j < COLS; j++) {
				if (table[i][j] == 0) {
					continue;
				}
				boolean same = true;
				for (int k = 1; k < limit; k++) {
					if (table[i - k + 1][j - k + 1] != table[i - k][j - k]) {
						same = false;
					}
				}
				if (same) {
					score.increase(table[i][j]);
				}
			}
		}

		for (int i = ROWS - limit; i >= 0; i--) {
			for (int j = limit - 1; j < COLS; j++) {
				if (table[i][j] == 0) {
					continue;
				}
				boolean same = true;
				for (int k = 1; k < limit; k++) {
					if (table[i + k - 1][j - k + 1] != table[i + k][j - k]) {
						same = false;
					}
				}
				if (same) {
					score.increase(table[i][j]);
				}
			}
		}
		return score;
	}

	public int calculateScore(int me) {
		final int TWO_ME_PARAM = 10;
		final int TWO_OTHER_PARAM = -100;
		
		final int THREE_ME_PARAM = 100;
		final int THREE_OTHER_PARAM = -1000;
		
		Score twos = calculateConsecutives(2);
		Score threes = calculateConsecutives(3);
		Score fours = calculateConsecutives(4);
		
		int other = getOpposite(me);
		if (fours.get(me) > 1) {
			return Integer.MAX_VALUE;
		}
		if (fours.get(other) > 1) {
			return Integer.MIN_VALUE;
		}
		
		int score = TWO_ME_PARAM * twos.get(me) + 
				TWO_OTHER_PARAM * twos.get(other) + 
				THREE_ME_PARAM * threes.get(me) + 
				THREE_OTHER_PARAM * threes.get(other);		
		return score;
	}

	
	public void dumpTable() {
		for (int i = ROWS - 1; i >= 0; i--) {
			for (int j = 0; j < COLS; j++) {
				System.out.print(table[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	public int[][] getTable() {
		return table;
	}

	public boolean canMakeMove(int move) {
		return table[ROWS - 1][move] == 0;
	}

	public boolean placeIt(int move) {
		for (int i = 0; i < ROWS; i++) {
			if (table[i][move] == 0) {
				table[i][move] = currentPlayer;
				stack.push(new Coordinates(i, move));
				return true;
			}
		}
		for (int j = 0; j < COLS; j++) {
			for (int i = 0; i < ROWS; i++) {
				if (table[i][j] == 0) {
					table[i][j] = currentPlayer;
					stack.push(new Coordinates(i, j));
					return true;
				}
			}
		}
		return false;
	}

	public void undoMove() {
		Coordinates coordinates = stack.pop();
		table[coordinates.i][coordinates.j] = 0;
		switchPlayer();
	}

	public void makeMove(int move) {
		placeIt(move);
		switchPlayer();
	}

	public int getOpposite(int player) {
		return player == 1 ? 2 : 1;
	}
	
	private void switchPlayer() {
		currentPlayer = getOpposite(currentPlayer);
	}

	public void clearTable() {
		// TODO Auto-generated method stub

	}

}
