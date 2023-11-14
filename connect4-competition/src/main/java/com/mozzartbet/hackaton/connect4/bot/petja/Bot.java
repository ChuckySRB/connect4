package com.mozzartbet.hackaton.connect4.bot.petja;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;
import static com.mozzartbet.hackaton.connect4.util.ThreadHelper.sleep;

import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

	long timeoutMillis = 100;
	int opponentMoves = 0;
	int id;
	MyBoard board;
	public Brain brain;
	static long i;

	@Override
	public void configure(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
		board = new MyBoard();
		brain = new Brain(board);
		i = timeoutMillis;
		if(i == 1000)System.out.println("configured");
	}

	@Override
	public void move() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		brain.Calculate();
		if(brain.getDefence() != null && brain.getAttack()!= null){
			if(brain.getDefence().getLevel()>brain.getAttack().getLevel()){
				move = brain.getDefence().getMove();
				if(i == 1000)System.out.println("played def");
			} else {move = brain.getAttack().getMove();
			if(i == 1000)System.out.println("played att");
			}
			if(i == 1000)System.out.println("att " + brain.getAttack().toString());
			if(i == 1000)System.out.println("def " + brain.getDefence().toString());
		}
		else if (brain.getDefence() != null) move = brain.getDefence().getMove();
		else if (brain.getAttack() != null) move = brain.getAttack().getMove();
		else move = rnd.nextInt(COLUMNS);
		for(int a:brain.doNot){
			if(i == 1000)System.out.println(a);
		}
		board.addMove(move, 1);
	}

	@Override
	public void stop() {
		
	}

	@Override
	public void opponentMove(int move) {
		board.addMove(move, 2);

	}

	@Override
	public void finished(int winner) {
		board.printBoard();
	}


}
