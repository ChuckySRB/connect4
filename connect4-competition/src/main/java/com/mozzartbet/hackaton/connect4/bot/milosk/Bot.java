package com.mozzartbet.hackaton.connect4.bot.milosk;

import static com.mozzartbet.hackaton.connect4.model.Direction.*;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.IN_A_ROW;

import java.util.Stack;

import com.mozzartbet.hackaton.connect4.model.Direction;
import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Move;
import com.mozzartbet.hackaton.connect4.model.Player;
import com.mozzartbet.hackaton.connect4.model.GameBoard;

public class Bot extends Player {
	
	private int[] arrMyCount;
	private int[] arrOpCount;
	
	private int myCount = -1;
	private int opCount = -1;
	
	private int maxRows = 6;
	private int maxCols = 8;

	private int[][] board;

	private int moveCnt = 0;
	
	//GameBoard GV; // treba da se inicijalizuje. kako????????????????
	
	
	//protected Stack<Move> moves;
	//protected int countersPlaced;
	private int lastOpMove = -1;

	//protected boolean gameOver;
	//protected int winner;
	
	void placeOp(int col) {
		int row = findDepth(col);
		if (row >= 0)
			board[row][col] = opCount;
	}
	
	private void initializeBoard() {
		board = new int[6][8];
		for (int i = 0; i < maxRows; i++)
			for (int j = 0; j < maxCols; j++)
				board[i][j] = 0;
	}
	
	private int findDepth(int col) { // broj praznih mesta u koloni odozgo
		int depth = 0;
		while (depth < maxRows && board[depth][col] == 0) {
			depth++;
		}
		return depth - 1;
	}

	private boolean checkWin(int counter, int row, int col) { // proverava da li je dobijo // counter je 1 ili 2
		int maxCount = IN_A_ROW - 1; // in a row je 4, kao povezi 4

		int count = countConnected(row + 1, col, S, counter);
		if (count >= maxCount) {
			return true;
		}

		count = countConnected(row, col + 1, E, counter)
				+ countConnected(row, col - 1, W, counter);
		if (count >= maxCount) {
			return true;
		}

		count = countConnected(row - 1, col + 1, NE, counter)
				+ countConnected(row + 1, col - 1, SW, counter);
		if (count >= maxCount) {
			return true;
		}

		count = countConnected(row - 1, col - 1, NW, counter)
				+ countConnected(row + 1, col + 1, SE, counter);
		if (count >= maxCount) {
			return true;
		}

		return false;
	}

	private int countConnected(int row, int col, Direction dir, int counter) { // broji koliko ima povezanih u zadatom pravcu i smeru
		if (row < maxRows && row > -1 && col < maxCols && col > -1
				&& board[row][col] == counter) {
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
	
	private void doRand() { /////////////////////////// proveri!!!!!!!!!!!!!!!!!!!!
		int rez = -1;
		int rnd = -1;
		int row = -1;
		
		if (moveCnt == 3) // za debug !!!
			moveCnt = 3;
		
		int sum = 0;
		for (int i = 0; i < 5; i++)
			sum += arrMyCount[i];
		
		loop: while (true) {
			rnd = ((int)(Math.random() * sum)) + 1; // ????????????????????od 1 do sum + 1 ??????
			
			if (rnd == sum + 1) // ako se desi da padne sum na randu, sto je vrlo malo verovatno
				rnd = sum;
			
			rez = -1;
			for (int i = 0; i < 5; i++) {
				if (rnd <= arrMyCount[i]) {
					rez = i;
					break; ///// ??????
				}
				else
					rnd -= arrMyCount[i];
			}
			
			
			row = -1;
			
			for (int i = 0; i < 3; i++) {
				rnd = (int)(Math.random() * 4);
			
				if (rnd == 4) // ako se desi da padne 4 na randu, sto je vrlo malo verovatno
					rnd = 3;
				
				row = findDepth(rez + rnd);
				if (row >= 0)
					break loop;
			}
		}
		
		int col = rez + rnd;
		
		//++arrMyCount[rez]; // jer je rez deo od 4 kolone u koji stavljamo ///// trebaju svi nizovi da se azuriraju
		
		if (moveCnt == 1) // za debug !!!
			moveCnt = 1;
		
		int cnt = 3; // proveri
		for (int j = col; j >= 0; j--) {
			if (cnt == 0)
				break;
			--cnt;
			
			if (j <= 3)
				++arrMyCount[j];
		}
		
		board[row][col] = myCount; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		//GV.placeCounter(rez + rnd, myCount);
		move = col;
	}
	
	@Override
	public void configure(long timeoutMillis) { // govori da pocinje nova igra, i parametar na je vreme na koji eng ceka da zavrsimo potez // kad istekne vreme poziva se get move ili kad se zavrsi move pozvace se 
		// TODO Auto-generated method stub
		
		initializeBoard(); ///////////
		
		arrMyCount = new int[6];
		arrOpCount = new int[6];
		
		for (int i = 0; i < 6; i++) {
			arrMyCount[i] = 0;
			arrOpCount[i] = 0;
		}
		
		moveCnt = 0;
		
		myCount = -1;
		opCount = -1;
	}
	
	@Override
	public void move() {
		// TODO Auto-generated method stub
		if (myCount == -1) { ////
			myCount = 1;
			opCount = 2;
		}
		
		
		if (moveCnt == 3) // za debug !!!
			moveCnt = 3;
		
		boolean end = false;
		
		if (moveCnt == 0) {
			move = 3 + ((int)Math.random()) * 2;
			board[findDepth(move)][move] = myCount;
			end = true;
			
			int cnt = 3; // proveri
			for (int j = move; j >= 0; j--) {
				if (cnt == 0)
					break;
				--cnt;
				
				if (j <= 3)
					++arrMyCount[j];
			}
		}
		
		if (!end) {
			// proverava da li mozemo da dobijemo u tekucem potezu // trebda neki go to !!!!!!!!!!!!!!!!!
			for (int i = 0; i < 8; i++) {
				int k;
				if ((k = findDepth(i)) >= 0) // treba >= verovatno
					 board[k][i] = myCount;
				else
					continue;
				if (checkWin(myCount, k, i)) { // moze da se optimizuje samo za unetu kolonu ///// PITAJ STA RADI CHECKWIN JER JE... /////
					//GV.placeCounter(i, myCount);
					move = i;
					
					end = true; ////////////////////////////////////////////////////////////////////////			
					
					break;
				}
				else
					board[k][i] = 0;
			}
		}
		
		if (!end) {
			// proverava da li protivnik dobija u sledecem potezu i blokira ga
			for (int i = 0; i < 8; i++) {
				int k;
				if ((k = findDepth(i)) >= 0) // treba >= verovatno
					 board[k][i] = opCount;
				else
					continue;
				if (checkWin(opCount, k ,i)) {
					//GV.placeCounter(i, myCount);
					move = i;
					board[k][i] = myCount; // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					
					int cnt = 3; // proveri
					for (int j = i; j >= 0; j--) {
						if (cnt == 0)
							break;
						--cnt;
						
						if (j <= 3)
							++arrMyCount[j];
					}
					
					end = true;
					
					break;
				}
				board[k][i] = 0; ///////////////////
			}
		}
		
		if (!end)
			doRand(); ////
		
		++moveCnt;
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		// moze thread za vreme drugog bota kolko radi // move je br kolone //
		// ovo se izvrsava ako nas komp zaustavi
		
		// mozemo da probamo brute dp, pa ako ne uspe da uvde izvrsimo ostalo, jer verovatno ne zahteva mnogo
		//move = 8;
		move = (int)(Math.random() * 8);
		while (move == 8) // ako se pojavi slucajno 8 //////////////////
			stop();
	}

	@Override
	public void opponentMove(int xxx) { // koji je potez napravio protivnik, i to su sve info koje imamo
		// TODO Auto-generated method stub
		if (myCount == -1){ ////
			myCount = 2;
			opCount = 1;
		}
		//lastOpMove = xxx;
		placeOp(xxx);
		
		// ovo vidi moze li cemu da sluzi
		/*int cnt = 4; // proveri
		for (int j = move; j >= 0; j--) {
			if (cnt == 0)
				break;
			--cnt;
			
			if (j <= 4)
				++arrOpCount[j];
		}*/
	}

	@Override
	public void finished(int winner) { // kad se zavrsi game
		// TODO Auto-generated method stub

	}
}
