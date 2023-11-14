package com.mozzartbet.hackaton.connect4.bot.Vesna;

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

	protected final int maxRows = GameConsts.ROWS;
	protected final int maxCols = GameConsts.COLUMNS;

	private boolean playFirst = false;
	// long timeoutMillis = 100;
	// int opponentMoves = 0;

	private int myMove;
	GameBoard board;

	@Override
	public void configure(long timeoutMillis) {

		// this.timeoutMillis = timeoutMillis;

		this.board = new GameBoard();

	}

	@Override
	public void move() {

		if (board.getCountersPlaced() == 0) {
			// ja igram prva
			playFirst = true;
		}
		if (!playFirst) {

			// igram ako ne znam bolje

			boolean b010 = find010();
			boolean def = false;
			if (!canWin()) {
				if (!b010) {

					def = mustDef(board.getLastMove());
					if (!def) {
						if (!findxx()) {
							if (!checkWin(2)) {
								if (!findL()) {
									move = board.getLastMove().getCol();
									board.placeCounter(move, 2);
								}
							}
						}
					}
				}
			} else {
				System.out.println("naucio");
			}

		} else {
			ThreadLocalRandom rnd = ThreadLocalRandom.current();
			move = rnd.nextInt(COLUMNS);
			playFirst = false;
			board.placeCounter(move, 2);
		}
		myMove = move;

	}

	private boolean findxx() {

		for (int j = 0; j < 6; j++) {
			for (int i = 0; i < 5; i++) {
				if (board.getBoard()[j][i] == 2 && board.getBoard()[j][i + 1] == 0 && board.getBoard()[j][i + 2] == 2) {
					move = i + 1;
					board.placeCounter(move, 2);
					return true;
				}

			}
		}
		return false;

	}

	private boolean checkWin(int counter) {
		int maxCount = IN_A_ROW - 1;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				int row = i;
				int col = j;

				int count = countConnected(row + 1, col, S, counter);
				if (count >= maxCount) {
					move =j;
					board.placeCounter(move, 2);
					
					return true;
				}

				count = countConnected(row, col + 1, E, counter) + countConnected(row, col - 1, W, counter);
				if (count >= maxCount) {
					move =j;
					board.placeCounter(move, 2);
					return true;
				}

				count = countConnected(row - 1, col + 1, NE, counter) + countConnected(row + 1, col - 1, SW, counter);
				if (count >= maxCount) {
					move =j;
					board.placeCounter(move, 2);
					return true;
				}

				count = countConnected(row - 1, col - 1, NW, counter) + countConnected(row + 1, col + 1, SE, counter);
				if (count >= maxCount) {
					move =j;
					board.placeCounter(move, 2);
					return true;
				}
			}
		}

		return false;
	}

	public int countConnected(int row, int col, Direction dir, int counter) {
		if (row < maxRows && row > -1 && col < maxCols && col > -1 && board.getBoard()[row][col] == counter) {
			switch (dir) {
			case N:
				return 1 + countConnected(row - 1, col, dir, counter);
			case S:
				return 1 + countConnected(row + 1, col, dir, counter);
			case E:
				return 1 + countConnected(row, col + 1, dir, counter);
			case W:
				return 1 + countConnected(row, col - 1, dir, counter);
			case NE:
				return 1 + countConnected(row - 1, col + 1, dir, counter);
			case NW:
				return 1 + countConnected(row - 1, col - 1, dir, counter);
			case SE:
				return 1 + countConnected(row + 1, col + 1, dir, counter);
			case SW:
				return 1 + countConnected(row + 1, col - 1, dir, counter);
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	private boolean canWin() {
		int col = myMove;
		int row = board.findDepth(myMove);
		;
		// EST
		if (col > 0) {

			int e = board.countConnected(row, col, Direction.E, 2);

			if (e == 3) {
				if (board.findDepth(col - 1) == row) {
					move = col - 1;
					board.placeCounter(move, 2);
					return true;
				} else {
					if (col + e < 7 && board.findDepth(col + e - 1) == row) {
						move = col + e - 1;
						board.placeCounter(move, 2);
						return true;
					}
				}
			}

			// nw
			if (row > 0) {

				int nw = board.countConnected(row, col, Direction.NW, 2);

				if ((nw == 2 || nw == 3) && board.findDepth(col - 1) == row + 1) {

					move = col - 1;
					board.placeCounter(move, 2);
					return true;
				}
			}

		}

		// WEST
		if (col < 7) {
			int w = board.countConnected(row, col, Direction.W, 2);
			if (w == 3) {
				if (board.findDepth(col + 1) == row) {
					move = col + 1;
					board.placeCounter(move, 2);
					return true;
				} else {
					if (col - w + 1 >= 0 && board.findDepth(col - w + 1) == row) {
						move = col - w + 1;
						board.placeCounter(move, 2);
						return true;
					}
				}
			}

			// sw
			if (row > 0) {

				int sw = board.countConnected(row, col, Direction.SW, 2);
				if ((sw == 2 || sw == 3) && board.findDepth(col + 1) == row - 1) {
					move = col + 1;
					board.placeCounter(move, 2);
					return true;
				}
			}
		}

		// n
		if (row > 0) {

			int n = board.countConnected(row, col, Direction.N, 2);
			if (n == 3) {
				move = col;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// s
		if (row < 5) {

			int s = board.countConnected(row, col, Direction.S, 2);
			if (s == 3) {
				move = col;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// sw
		if (col < 7 && row > 0) {

			int sw = board.countConnected(row, col, Direction.SW, 2);
			if (sw == 3 && board.findDepth(col + 1) == row - 1) {
				move = col + 1;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// nw
		if (col > 0 && row > 0) {

			int nw = board.countConnected(row, col, Direction.NW, 2);

			if (nw == 3 && board.findDepth(col - 1) == row + 1) {
				move = col - 1;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// se
		if (col < 7 && row > 0 && col > 0) {

			int se = board.countConnected(row, col, Direction.SE, 2);
			if (se == 3 && board.findDepth(col - 1) == row - 1) {
				move = col - 1;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// ne
		if (col < 7 && row > 0) {

			int ne = board.countConnected(row, col, Direction.NE, 2);
			if (ne == 3 && board.findDepth(col + 1) == row + 1) {
				move = col + 1;
				board.placeCounter(move, 2);
				return true;
			}
		}

		return false;
	}

	private boolean find010() {
		for (int i = 0; i < 5; i++) {
			if (board.getBoard()[5][i] == 1 && board.getBoard()[5][i + 1] == 0 && board.getBoard()[5][i + 2] == 1) {
				move = i + 1;
				board.placeCounter(move, 2);
				return true;
			}
		}
		return false;

	}

	private boolean findL() {
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 5; i++) {
				if (board.getBoard()[j][i] == 1 && board.getBoard()[j][i + 1] == 0 && board.getBoard()[j][i + 2] == 1) {
					move = i + 1;
					board.placeCounter(move, 2);
					return true;
				}
			}
		}
		return false;

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		move = board.getLastMove().getCol();
		board.placeCounter(move, 2);
	}

	@Override
	public void opponentMove(int move) {
		// TODO Auto-generated method stub
		board.placeCounter(move, 1);

	}

	@Override
	public void finished(int winner) {
		board = new GameBoard();
	}

	private boolean mustDef(Move m) {
		int col = m.getCol();
		int row = m.getRow();

		// EST
		if (col > 0) {

			int e = board.countConnected(row, col, Direction.E, 1);
			if (e == 3) {
				if (board.findDepth(col - 1) == row) {
					move = col - 1;
					board.placeCounter(move, 2);
					return true;
				} else {
					if (col + e < 7 && board.findDepth(col + e - 1) == row) {
						move = col + e - 1;
						board.placeCounter(move, 2);
						return true;
					}
				}
			}

			// nw
			if (row > 0) {

				int nw = board.countConnected(row, col, Direction.NW, 1);

				if ((nw == 2 || nw == 3) && board.findDepth(col - 1) == row + 1) {
					move = col - 1;
					board.placeCounter(move, 2);
					return true;
				}
			}
			if (e == 2) {
				if (board.findDepth(col - 1) == row) {
					move = col - 1;
					board.placeCounter(move, 2);
					return true;
				} else {
					if (col + e < 7 && board.findDepth(col + e - 1) == row) {
						move = col + e - 1;
						board.placeCounter(move, 2);
						return true;
					}
				}
			}

		}

		// WEST
		if (col < 7) {
			int w = board.countConnected(row, col, Direction.W, 1);
			if (w == 3) {
				if (board.findDepth(col + 1) == row) {
					move = col + 1;
					board.placeCounter(move, 2);
					return true;
				} else {
					if (col - w + 1 >= 0 && board.findDepth(col - w + 1) == row) {
						move = col - w + 1;
						board.placeCounter(move, 2);
						return true;
					}
				}
			}

			// sw
			if (row > 0) {

				int sw = board.countConnected(row, col, Direction.SW, 1);
				if ((sw == 2 || sw == 3) && board.findDepth(col + 1) == row - 1) {
					move = col + 1;
					board.placeCounter(move, 2);
					return true;
				}
			}
			if (w == 2) {
				if (board.findDepth(col + 1) == row) {
					move = col + 1;
					board.placeCounter(move, 2);
					return true;
				} else {
					if (col - w + 1 >= 0 && board.findDepth(col - w + 1) == row) {
						move = col - w + 1;
						board.placeCounter(move, 2);
						return true;
					}
				}
			}
		}

		// n
		if (row > 0) {

			int n = board.countConnected(row, col, Direction.N, 1);
			if (n == 3) {
				move = col;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// s
		if (row < 5) {

			int s = board.countConnected(row, col, Direction.S, 1);
			if (s == 3) {
				move = col;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// se
		if (col < 7 && row > 0 && col > 0) {

			int se = board.countConnected(row, col, Direction.SE, 1);
			if ((se == 2 || se == 3) && board.findDepth(col - 1) == row - 1) {
				move = col - 1;
				board.placeCounter(move, 2);
				return true;
			}
		}

		// ne
		if (col < 7 && row > 0) {

			int ne = board.countConnected(row, col, Direction.NE, 1);
			if ((ne == 2 || ne == 3) && board.findDepth(col + 1) == row + 1) {
				move = col + 1;
				board.placeCounter(move, 2);
				return true;
			}
		}

		return false;
	}

}
