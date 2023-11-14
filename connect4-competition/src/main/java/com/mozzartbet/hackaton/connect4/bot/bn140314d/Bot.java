package com.mozzartbet.hackaton.connect4.bot.bn140314d;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;

import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.*;

import static com.mozzartbet.hackaton.connect4.util.ThreadHelper.sleep;

import java.lang.*;

import java.util.*;

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

class ListOfThreads {
	final static HashSet<Thread> set = new HashSet<>();
	
	final static int MAX_THREADS = 7;
	
	static void insert(Thread t) {
		new Thread(() -> {
			synchronized (set) {
				try {
					while (set.size() > MAX_THREADS) {
						set.wait();
					}
				} catch (InterruptedException e) {
				}
				set.add(t);
				t.start();
			}			
		}).start();
	}
	
	static void erase(Thread t) {
		synchronized (set) {
			set.remove(t);
			set.notifyAll();
		}
	}
}

class Helper {
	static double INF = 100000000;
	
	static boolean checkHorizontal(int[][] board, int i, int j) {
		if (j > 0) {
			if (board[i][j - 1] == 0 && (i == 0 || board[i - 1][j - 1] != 0)) return true;
		}
		if (j < 6) {
			if (board[i][j + 3] == 0 && (i == 0 || board[i - 1][j + 3] != 0)) return true;
		}
		return false;
	}
	
	static int countConnected(GameBoard board, Direction dir, int i, int j) {
		int ret1 = board.countConnected(i, j, dir, 1);
		int ret2 = board.countConnected(i, j, dir, 2);
		return ret1 > ret2 ? ret1 : -ret2;
	}
	
	static int is(int[][] brd, int i, int j, int num) {
		if (i < 0 || i > 5) return 0;
		if (j < 0 || j > 7) return 0;
		if (brd[i][j] == num) return 1;
		return 0;
	}
	
	static int countSame(int[][] brd, int i, int j) {
		int ret = 0;
		
		int hor = 0;
		int vert = 0;
		int dr = 0;
		int dl = 0;
		
		int num = brd[i][j];
		if (num == 0) return 0;
			
		for (int k = 0; k < 4; k++) {
			hor += is(brd, i + k, j, num);
			vert += is(brd, i, j + k, num);
			dr += is(brd, i+k, j+k, num);
			dl += is(brd, i-k, j+k, num);
		}
		
		if (hor > ret) ret = hor;
		if (vert > ret) ret = vert;
		if (dr > ret) ret = dr;
		if (dl > ret) ret = dl;
		
		return ret;
	}
	
	static double possibleFour(GameBoard board) {
		
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				if (countSame(board.getBoard(), i, j) == 3) {
					System.out.println("Counted" + i + " " + j);
					if (board.getBoard()[i][j] == 1) return INF - 100;
					else return -INF + 100;
				}
			}
		}
		
		return 0;
	}
	
	static int connected(GameBoard board, int[][] brd, int i, int j) {
		if (brd[i][j] != 0) return 0;
		int ret = 0;
		if (i == 0 || i == 5) ret += 1;
		if (j == 0 || j == 7) ret += 1;
		
		ret += countConnected(board, Direction.N, i + 1, j);
		ret += countConnected(board, Direction.S, i - 1, j);
		ret += countConnected(board, Direction.E, i, j - 1);
		ret += countConnected(board, Direction.W, i, j + 1);
		ret += countConnected(board, Direction.NE, i + 1, j - 1);
		ret += countConnected(board, Direction.NW, i + 1, j + 1);
		ret += countConnected(board, Direction.SE, i - 1, j - 1);
		ret += countConnected(board, Direction.SW, i - 1, j + 1);
		
		return ret;
	}
	
	static int connected(GameBoard board) {
		int ret = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				ret += connected(board, board.getBoard(), i, j);
			}
		}
		
		return ret;
	}
	
	static double distance(int i, int j) {
		double ii = 5 - i;
		double jj = 3.5 - j;
		return Math.sqrt(ii * ii + jj * jj);
	}
	
	static double distance(int[][] board) {
		double ret = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == 1) ret += distance(i, j);
				else if (board[i][j] == 2) ret -= distance(i, j);
			}
		}
		return ret;
	}
	
	static double evaluate(GameBoard board) {
		if (board.isGameOver()) {
			int winner = board.getWinner();
			if (winner == 1) return INF;
			else return -INF;
		}
		
		int [][] brd = board.getBoard();
		// Fourth in a row
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				if (brd[i][j] == 0 && board.countConnected(i,  j, Direction.S, 1) == 3) return INF - 100;
				if (brd[i][j] == 0 && board.countConnected(i, j,  Direction.S, 2) == 3) return -INF + 100;
			}
		}
		
		// Horizontal fourth in a row
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (brd[i][j] == 0 && 
						board.countConnected(i,  j, Direction.E, 1) == 3 &&
						Helper.checkHorizontal(brd, i, j)) 
					return INF - 100;
				if (brd[i][j] == 0 && 
						board.countConnected(i,  j, Direction.E, 2) == 3 &&
						Helper.checkHorizontal(brd, i, j)) 
					return -INF + 100;			
			}
		}
		
		//double possible = possibleFour(board);
		//if (possible > 100000 || possible < -100000) return possible;
		
		// Simple evaluation
		int connected = Helper.connected(board);
		
		double distance = Helper.distance(brd);

		return connected - distance;
	}
}

class BotThread extends Thread {
	
	final static double INF = 1000000000.0;
		
	int level;
	int myMove;
	int move = 0;
	double evaluation = 0;
	Bot bot;
	GameBoard board;
	
	BotThread[] children = new BotThread[8];
	BotThread parent;
	
	boolean first;
	
	BotThread(Bot bot, BotThread parent, boolean first, int myMove, GameBoard board, int level) {
		this.bot = bot;
		this.parent = parent;
		this.first = first;
		this.myMove = myMove;
		this.board = board;
		this.level = level;
	}
		
	int findChild(double evaluation) {
		for (int i = 0; i < 8; i++) if (children[i] != null && 
				children[i].evaluation > evaluation - 1 &&
				children[i].evaluation < evaluation + 1) {
			return i;
		}
		System.out.println("Not found");
		return 0;
	}
	
	static void propagate(BotThread t, double evaluation) {
		while (t.parent != null) {
			if ((t.first && t.evaluation < evaluation) || ((!t.first) && t.evaluation > evaluation))
				t.evaluation = evaluation;
			else return;
			t = t.parent;
		}
		t.bot.setMove(t.findChild(evaluation), evaluation);
	}
	
	synchronized void propagate(double evaluation) {
		if (bot.botThread == null) return;
		if (bot.botThread.first && bot.botThread.evaluation > evaluation) return;
		if (!bot.botThread.first && bot.botThread.evaluation < evaluation) return;
		/*System.out.println("Propagate " + evaluation);
		if (first) {
			if (evaluation > this.evaluation) {
				this.evaluation = evaluation;
				if (parent != null) parent.propagate(evaluation);
				else bot.setMove(findChild(evaluation), evaluation);					
			}
		}
		else {
			if (evaluation < this.evaluation) {
				this.evaluation = evaluation;
				if (parent != null) parent.propagate(evaluation);
				else bot.setMove(findChild(evaluation), evaluation);			
			}
		}*/
		propagate(this, evaluation);
	}
	
	double evaluate(GameBoard board) {
		if (board.isGameOver()) {
			int winner = board.getWinner();
			if (winner == 1) return INF;
			else return -INF;
		}
		
		int [][] brd = board.getBoard();
		// Fourth in a row
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 8; j++) {
				if (brd[i][j] == 0 && board.countConnected(i,  j, Direction.S, 1) == 3) return INF - 100;
				if (brd[i][j] == 0 && board.countConnected(i, j,  Direction.S, 2) == 3) return -INF + 100;
			}
		}
		
		// Horizontal fourth in a row
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (brd[i][j] == 0 && 
						board.countConnected(i,  j, Direction.E, 1) == 3 &&
						Helper.checkHorizontal(brd, i, j)) 
					return INF - 100;
				if (brd[i][j] == 0 && 
						board.countConnected(i,  j, Direction.E, 2) == 3 &&
						Helper.checkHorizontal(brd, i, j)) 
					return -INF + 100;			
			}
		}
		
		// Simple evaluation
		int connected = Helper.connected(board);
		
		//double distance = Helper.distance(brd);

		return connected ;
	}
	
	synchronized void finish() {
		if (children[0] == null) return;
		for (int i = 0; i < 8; i++) {
			children[i].finish();
		}
		
		interrupt();
		ListOfThreads.erase(this);
	}
	
	synchronized BotThread moveToChild(int i) {
		GameBoard newBoard = board.deepCopy();
		
		int id;
		if (first) id = 1;
		else id = 2;
		
		newBoard.placeCounter(i, id);
		
		for (int k = 0; k < 8; k++) {
			if (k != i && children[i] != null) children[i].finish();
		}
		
		if (children[i] == null) children[i] = new BotThread(bot, null, !first, i, newBoard, level + 1);
		
		//int nextMove = children[i].findChild(children[i].evaluation);
		//bot.setMove(nextMove, evaluation);
		
		children[i].parent = null;
		return children[i];
	}
	
	@Override
	public void run() {
		
		//System.out.println(level);
		
		// Game over
		if (board.isGameOver()) {
			int winner = board.getWinner();
			if (winner == 1) evaluation = INF;
			else evaluation = -INF;
			if (parent != null) parent.propagate(evaluation);
			return;
		}
		
		//System.out.println("Depth " + parentDepth());
		
		evaluation = evaluate(board);
		if (parent != null) {
			parent.propagate(evaluation * level);
			//System.out.println("eval " + evaluation * level);
		}
		
		int id;
		if (first) id = 1;
		else id = 2;

		if (level > 4) return;
		
		for (int i = 0; i < 8; i++) {
			GameBoard newBoard = board.deepCopy();
			newBoard.placeCounter(i, id);
			synchronized (this) {
				children[i] = new BotThread(bot, this, !first, i, newBoard, this.level + 1);			
				ListOfThreads.insert(children[i]);
			}
			
		}
		
		ListOfThreads.erase(this);
	}
}

////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

class Node {
	Node[] children = new Node[8];
	double eval;
	Node parent;
	boolean first;
	int level;
	
	Node(Node parent, boolean first, int level) {
		this.parent = parent;
		this.first = first;
		this.level = level;
	}
}

public class Bot extends Player {

	int level = 0;
	
	long timeoutMillis;
	
	double evaluation = 0;
	
	GameBoard board;
	
	BotThread botThread = null;
	
	public synchronized void setMove(int move, double evaluation) {
		if (botThread.first && evaluation < this.evaluation) return;
		if (!botThread.first && evaluation > this.evaluation) return;
		System.out.println("Set " + move + " eval " + evaluation + " --- " + botThread.first);
		this.move = move;
	}
	
	@Override
	public void configure(long timeoutMillis) {		
		this.timeoutMillis = timeoutMillis;
		level = 0;
		board = new GameBoard();
	}

	@Override
	public void move() {
		/*
		botThread = new BotThread(this, null, true, -1, board, 0);
		ListOfThreads.insert(botThread);
		sleep(timeoutMillis / 2);
		board.placeCounter(move, 1);
		botThread.finish();
		botThread = null;
		*/
		
		double eval = -10000000;
		for (int i = 0; i < 8; i++) {
			board.placeCounter(i, 1);
			double newEval = Helper.evaluate(board);
			if (newEval > eval) {
				eval = newEval;
				move = i;
			}
			board.undoMove();
		}
		board.placeCounter(move, 1);
	}

	@Override
	public void stop() {
	}

	@Override
	public void opponentMove(int move) {
		board.placeCounter(move, 2);
		level++;
	}

	@Override
	public void finished(int winner) {
		botThread.finish();
		botThread = new BotThread(this, null, true, -1, new GameBoard(), 0);
		ListOfThreads.insert(botThread);
		level = 0;
		board = new GameBoard();
	}

}
