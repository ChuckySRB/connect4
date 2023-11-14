package com.mozzartbet.hackaton.connect4.bot.vs;

import static com.mozzartbet.hackaton.connect4.model.Direction.E;
import static com.mozzartbet.hackaton.connect4.model.Direction.NE;
import static com.mozzartbet.hackaton.connect4.model.Direction.NW;
import static com.mozzartbet.hackaton.connect4.model.Direction.S;
import static com.mozzartbet.hackaton.connect4.model.Direction.SE;
import static com.mozzartbet.hackaton.connect4.model.Direction.SW;
import static com.mozzartbet.hackaton.connect4.model.Direction.W;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.IN_A_ROW;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

	long timeoutMillis;
	int opponentMoves = 0;
	boolean amFirst;
	int lastOpMove = -1;
	GameBoard board;

	int myCounter = 1;// I am 1, opponent 2
	int opCounter = 2;

	@Override
	public void configure(long timeoutMillis) {
		// TODO Auto-generated method stub
		this.timeoutMillis = timeoutMillis;
		this.opponentMoves = 0;
		this.lastOpMove = -1;
		board = new GameBoard();
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		/*
		 * if(lastOpMove==-1) { //kopiranje protivnika amFirst=true;
		 * ThreadLocalRandom rnd = ThreadLocalRandom.current(); move =
		 * rnd.nextInt(COLUMNS); } else{ move=(COLUMNS-1)-lastOpMove; }
		 */

		boolean canWin = false;
		boolean canLose = false;
		int t = canWin();
		if (t > -1)
			canWin = true;

		if (!canWin) {
			t = canLose();
			if (t > -1)
				canLose = true;
		}

		if (canWin) {
			move = t;
		} else if (canLose) {
			move = t;
		}

		else {
			boolean ICanWinNext = false;
			boolean HeCanWinNext = false;
			ArrayList a = safeMoves();
			int u = CanWinNext(myCounter);
			if (u > -1 && a.contains(u)){
				ICanWinNext = true;
			}
			if (!ICanWinNext) {// da li odigravanjem nekog poteza mogu da
								// pobedim
								// u sledecem potezu

				u = CanWinNext(opCounter);
				if (u > -1)
					HeCanWinNext = true;

			}

			if (ICanWinNext) {
				move = u;
			} else if (HeCanWinNext) {
				move = u;
			}

			else {// ne random, vec potez

				//ArrayList a = safeMoves();

				if (a.isEmpty()) {
					// poraz
					move = 0;
				} else {
					if (opponentMoves%2==0){
					int max_count = 0;
					int max_i = 0;
					for (int i = 0; i < a.size(); i++) {
						int col = i;
						int row = board.findDepth(col);
						int count = 1 + board.countConnected(row + 1, col, S, 1)
								+ board.countConnected(row, col + 1, E, 1) + board.countConnected(row, col - 1, W, 1)
								+ board.countConnected(row - 1, col + 1, NE, 1)
								+ board.countConnected(row + 1, col - 1, SW, 1)
								+ board.countConnected(row - 1, col - 1, NW, 1)
								+ board.countConnected(row + 1, col + 1, SE, 1);

						if (count > max_count) {
							max_count = count;
							max_i = i;
						}

					}

					if (max_count >= 5)
						move = max_i;
					else {
						ThreadLocalRandom rnd = ThreadLocalRandom.current();
						int ind = rnd.nextInt(a.size());
						move = (int) a.get(ind);
					}
					// System.out.println(a);
					// System.out.println(move);
					}
					else{
						ThreadLocalRandom rnd = ThreadLocalRandom.current();
						int ind = rnd.nextInt(a.size());
						move = (int) a.get(ind);
					}
				}
			}

		}
		board.placeCounter(move, 1);

	}

	private ArrayList safeMoves() {
		ArrayList list = new ArrayList();

		for (int i = 0; i < COLUMNS; i++) {
			int col = i;

			board.placeCounter(col, 1);
			if (canPlayerWin(opCounter) == -1) {
				list.add(col);
			}
			board.undoMove();
		}

		return list;
	}

	private int CanWinNext(int myC) {// vraca potez koji treba da odigramo ako
		int[][] myBoard = board.getBoard(); // postoji, ako ne -1
		for (int i = 0; i < COLUMNS; i++) {
			int col = i;
			int row = board.findDepth(col);
			if (row == -1) {
				for (int j = 0; j < COLUMNS; j++) {
					row = board.findDepth(j);
					if (row != -1) {
						col = i;
						break;
					}
				}
			}

			int pravci_pobede = 0;// treba da bude 2
			// int pravci_sa_tri = 0;

			int vertikalno = 1 + board.countConnected(row + 1, col, S, myC);
			if (vertikalno >= 3 && row > 0) { // dole imam jos 2, a gore ima
												// mesta da se stavi jos jedan
				pravci_pobede++;
			}

			int horizontalno = 1 + board.countConnected(row, col + 1, E, myC)
					+ board.countConnected(row, col - 1, W, myC);

			if (horizontalno >= 3) {
				int hor_pravci = 0;

				int leva_gr = col;
				int desna_gr = col;

				// System.out.println("myBoard[row][col]:"+myBoard[row][col]);

				while (leva_gr >= 1 && myBoard[row][leva_gr - 1] == myC) {
					leva_gr--;
				}
				while (desna_gr <= 6 && myBoard[row][desna_gr + 1] == myC) {
					desna_gr++;
				}

				// System.out.println("leva: "+leva_gr+", desna: "+desna_gr+",
				// row: "+row+", col: "+col);

				if (leva_gr >= 1) {
					if (board.findDepth(leva_gr - 1) == row)
						hor_pravci++;
				}
				if (desna_gr <= 6) {
					if (board.findDepth(desna_gr + 1) == row)
						hor_pravci++;
				}
				if (hor_pravci >= 2)
					return leva_gr - 1;

				else if (hor_pravci >= 1)
					pravci_pobede++;
			}

			int dij1 = 1 + board.countConnected(row - 1, col + 1, NE, myC)
					+ board.countConnected(row + 1, col - 1, SW, myC);
			if (dij1 >= 3) {
				int dij1_pravci = 0;
				if (col >= 1) {
					if (board.findDepth(col - 1) == row + 1 && row < 6)
						dij1_pravci++;
				}
				if (col <= 6) {
					if (board.findDepth(col + 1) == row - 1 && row >= 0)
						dij1_pravci++;
				}

				if (dij1_pravci >= 2)
					return col;
				else if (dij1_pravci >= 1)
					pravci_pobede++;
			}

			int dij2 = 1 + board.countConnected(row - 1, col - 1, NW, myC)
					+ board.countConnected(row + 1, col + 1, SE, myC);

			if (dij2 >= 3) {
				int dij2_pravci = 0;
				if (col >= 1) {
					if (board.findDepth(col - 1) == row - 1 && row >= 6)
						dij2_pravci++;
				}
				if (col <= 6) {
					if (board.findDepth(col + 1) == row + 1 && row < 6)
						dij2_pravci++;
				}

				if (dij2_pravci >= 2)
					return col;
				else if (dij2_pravci >= 1)
					pravci_pobede++;
			}

			if (pravci_pobede >= 2)
				return col;

		} // for

		return -1;
	}

	private int canLose() {
		// TODO Auto-generated method stub
		return canPlayerWin(opCounter);
	}

	private int canWin() {
		return canPlayerWin(myCounter);
	}

	private int canPlayerWin(int myC) {// vraca kolonu, tj move koji donosi
										// pobedu, inace -1(ako nema pobede)
		// TODO Auto-generated method stub

		for (int i = 0; i < COLUMNS; i++) {
			int col = i;
			int row = board.findDepth(col);
			if (row == -1) {
				for (int j = 0; j < COLUMNS; j++) {
					row = board.findDepth(j);
					if (row != -1) {
						col = i;
						break;
					}
				}
			}

			int maxCount = IN_A_ROW - 1;
			int count = board.countConnected(row + 1, col, S, myC);
			if (count >= maxCount) {
				return col;
			}

			count = board.countConnected(row, col + 1, E, myC) + board.countConnected(row, col - 1, W, myC);
			if (count >= maxCount) {
				return col;
			}

			count = board.countConnected(row - 1, col + 1, NE, myC) + board.countConnected(row + 1, col - 1, SW, myC);
			if (count >= maxCount) {
				return col;
			}

			count = board.countConnected(row - 1, col - 1, NW, myC) + board.countConnected(row + 1, col + 1, SE, myC);
			if (count >= maxCount) {
				return col;
			}

		} // for

		return -1;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void opponentMove(int move) {
		// TODO Auto-generated method stub
		opponentMoves++;
		board.placeCounter(move, 2);
	}

	@Override
	public void finished(int winner) {
		// TODO Auto-generated method stub

	}

}
