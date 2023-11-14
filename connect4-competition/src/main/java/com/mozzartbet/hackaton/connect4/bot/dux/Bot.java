package com.mozzartbet.hackaton.connect4.bot.dux;

import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {
	
	int COLS = GameConsts.COLUMNS;
	int ROWS = GameConsts.ROWS;
	
	int SCORE_WIN = 100000000;
	int[] SCORE_MyInCol = {0,0,1,3,3,1,0,0};
	int[] SCORE_OpInCol = {0,0,-1,-3,-3,-1,0,0};
	//int[] SCORE_MyConnected = {0,0,3,10,1000};
	//int[] SCORE_OpConnected = {0,0,-3,-10,1000};
	int[] SCORE_MyConnected = {0, 2, 100, 1000, 100000000};
	int[] SCORE_OpConnected = {0, -2, -100, -1000, -100000000};
	int[] SCORE_GOOD = {0, 2, 100, 1000, 100000000};
	
	int[] col_priorities = {3,4,2,5,1,6,0,7};
	
	int NUM_COLS = 8;
	int MAX_DEPTH = 12;
	
	MyBoard globalBoard;
	MyBoard calcBoard;
	
	int MyColor;
	int OpColor;
	
	int evalValue;
	int playerToMoveNum;
	
	boolean stopped;
	
	int potez = 0;

	@Override
	public void configure(long timeoutMillis) {
		// TODO Auto-generated method stub
		
		// called on new game
		globalBoard = new MyBoard();
		MyColor = -1;
		OpColor = -1;
		potez = 0;
		playerToMoveNum = 1;
		evalValue = 0;
	}
	
	class Move{
		public int value;
		public int move;
		
		public Move(int value, int move){
			this.value = value;
			this.move = move;
		}
	}
	
	public void myMove(int move) {
		potez++;
		
		if (MyColor == -1) {
			MyColor = 1;
			OpColor = 2;
		}
		
		//globalBoard.placeCounter(move, MyColor);
		globalBoard.makeMove(move);
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		
		if (MyColor == -1) {
			MyColor = 1;
			OpColor = 2;
		}
		
		potez++;
		
		stopped = false;
		
		if (potez % 10 == 0) MAX_DEPTH++;
		
		//calcBoard = globalBoard.deepCopy();
		calcBoard = new MyBoard(globalBoard.getPlayerNum(), globalBoard.getBoard(), globalBoard.getMovesPlayed(), globalBoard.grabEvalValue());
		
		//System.out.println(calcBoard);
		
		playerToMoveNum = MyColor;
		
		calcMoveNew(1, -Integer.MAX_VALUE, Integer.MAX_VALUE);

		if (!stopped) {
			//globalBoard.placeCounter(move, MyColor);
			globalBoard.makeMove(move);
		}
		
		//System.out.println(move);
	}
	
	private Move calcMoveNew(int depth, int low, int high){

		Move[] movesArray = checkMoves(calcBoard);
		Move bestMove = new Move(-Integer.MAX_VALUE, -10); 

		for (int i = 0; i < 7 && bestMove.value < high; i++){
			int column = movesArray[i].move;

			if (calcBoard.isValidMove(column)){
				Move currentMove;

				int evalValue = calcBoard.grabEvalValue();

				calcBoard.makeMove(column);

				if (calcBoard.gameIsOver()){
					if (calcBoard.isFull()){
						currentMove = new Move(0, column);
					} 
					currentMove = new Move(SCORE_GOOD[4], column);

				} 
				else if (depth < MAX_DEPTH){
					currentMove = calcMoveNew(depth + 1, -high, -low);
					currentMove.value = (currentMove.value * -1);
					currentMove.move = column;

				} else { 
					currentMove = new Move(calcBoard.grabEvalValue(), column);
				}

				if (currentMove.value > bestMove.value){
					bestMove = currentMove;
					if (depth == 1) move = currentMove.move;
					low = Math.max(bestMove.value, low);
				}

				calcBoard.undoMove(column, evalValue);
			}

		}

		return bestMove;
	}
	
	private Move[] checkMoves(MyBoard calcBoard){
		int stateEval;
		Move[] movesArray = new Move[COLS];

		stateEval = calcBoard.grabEvalValue();

		for (int i = 0; i < COLS; i++){
			int theMove = col_priorities[i];

			movesArray[i] = new Move(-Integer.MAX_VALUE, theMove);
			if (calcBoard.isValidMove(theMove)){
				calcBoard.makeMove(theMove);
				movesArray[i].value = calcBoard.grabEvalValue();
				calcBoard.undoMove(theMove, stateEval);
			} 
		}

		for (int i = 1; i < COLS; i++){
			for (int compare = i; (compare >=1 && movesArray[compare].value > movesArray[compare - 1].value); compare--){
				Move placeholder = movesArray[compare];
				movesArray[compare] = movesArray[compare - 1];
				movesArray[compare - 1] = placeholder;
			}

		}

		return movesArray;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		stopped = true;
		
		//globalBoard.placeCounter(move, MyColor);
		globalBoard.makeMove(move);
	}

	@Override
	public void opponentMove(int move) {
		// TODO Auto-generated method stub
		
		if (MyColor == -1) {
			MyColor = 2;
			OpColor = 1;
		}
		
		//globalBoard.placeCounter(move, OpColor);
		globalBoard.makeMove(move);
	}

	@Override
	public void finished(int winner) {
		// TODO Auto-generated method stub
		
	}

}
