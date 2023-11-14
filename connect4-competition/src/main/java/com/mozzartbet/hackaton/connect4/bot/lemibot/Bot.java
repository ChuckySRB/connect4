package com.mozzartbet.hackaton.connect4.bot.lemibot;

import java.util.Random;

import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {
	final static int ROWS = GameConsts.ROWS;
	final static int COLS = GameConsts.COLUMNS;

	InsiderGameModel model = new InsiderGameModel();
	int myID = 0;
	long time;
	
	@Override
	public void configure(long timeoutMillis) {
		model = new InsiderGameModel();
		myID = 0;
	}

	@Override
	public void move() {
		if (myID == 0) {
			myID = 1;
//			System.out.println(myID);
		}
		time = System.currentTimeMillis();
		// samo nek' je random!
		Random r = new Random();
		this.move = r.nextInt(8);

		int best = Integer.MIN_VALUE;
		int[] scores = new int[COLS];
		for (int c = 0; c < COLS; c++) {
			scores[c] = Integer.MIN_VALUE;
			
			if (!model.canMakeMove(c)) {
				continue;
			}
			model.makeMove(c);
			scores[c] = model.calculateScore(myID);
			if (scores[c] > best) {
				best = scores[c];
				this.move = c;
			}
			model.undoMove();
		}
		
		//this.move = search(myID, 0).move;
		model.makeMove(this.move);
	}

	class Ret {
		int move;
		double score;
		public Ret(int move, double score) {
			super();
			this.move = move;
			this.score = score;
		}
		public Ret() {
			this.move = 0;
			this.score = 0;
		}
	}
	private Ret search(int me, int level) {

		if (level > 5) {
			double val = model.calculateScore(me) / Integer.MAX_VALUE;
			return new Ret(0, val);
		}
		int col = 0;
		double best = -1.0;
		double[] scores = new double[COLS];
		for (int c = 0; c < COLS; c++) {
			scores[c] = -1.0;
			if (!model.canMakeMove(c)) {
				continue;
			}
			model.makeMove(c);
			if (model.calculateScore(me) == Integer.MAX_VALUE) {
				return new Ret(c, 1.0);
			}
			Ret r = search(model.getOpposite(me), level + 1);
			scores[c] = -r.score;
			
			if (scores[c] > best) {
				best = scores[c];
				col = c;
			}
			model.undoMove();
		}
		return new Ret(col, best);
	}

	@Override
	public void stop() {

	}

	@Override
	public void opponentMove(int move) {
		if (myID == 0) {
			myID = 2;
//			System.out.println(myID);
		}

		model.makeMove(move);
	}

	@Override
	public void finished(int winner) {

	}

}
