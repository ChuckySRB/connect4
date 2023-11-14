package com.mozzartbet.hackaton.connect4.bot.mv_bot;

import javax.swing.JOptionPane;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {
	private boolean started=false;
	
	private GameBoard currentGameBoard=new GameBoard();
	private long timeout=0;
	private MyBoard currentBoard=new MyBoard();
	private int movesPlayed;
	public static boolean needReset=false;
	
	public Bot() {
		super();
		resetState();
	}

	@Override
	public void configure(long timeoutMillis) {
		this.timeout=timeoutMillis;
		resetState();
	}

	@Override
	public void move() {
		int d=6;
		Calculator.DepthOfSearch=d;
		Calculator.iCANDOIT=false;
		
		if(needReset){
			currentBoard=new MyBoard();
			needReset=false;
		}
		
	
			AdvancedMove m1=Calculator.find(currentBoard, 7, 1, d);
		
		
		if(Calculator.iCANDOIT)move=Calculator.IDIDIT;
		else move=m1.move;
		//move=Calculator.findMove(currentBoard, 1, 2);
		movesPlayed++;
		
		if(Calculator.dontPlayFlag&&move==Calculator.moveNotToPlay)
			move=(move+1)%8;
			
		currentBoard.addMove(move, 1);
		
		
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void opponentMove(int move) {
		movesPlayed++;
		currentBoard.addMove(move, -1);
		
		
	}

	@Override
	public void finished(int winner) {
		move=-1;
		currentGameBoard.reset();
		currentBoard=new MyBoard();
		started=false;
		movesPlayed=0;
		needReset=true;
		
		
	}
	
	private void resetState(){
		move=-1;
		currentGameBoard.reset();
		currentBoard=new MyBoard();
		started=false;
		movesPlayed=0;
		
		
	}
	
	

}
