package com.mozzartbet.hackaton.connect4.bot.dux;

import java.util.Arrays;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.GameConsts;

public class MyBoard {
	
	public static int[] SCORE_GOOD = {0, 2, 100, 1000, 100000000};
	
	int ROWS = GameConsts.ROWS;
	int COLS = GameConsts.COLUMNS;
	int EMPTY = 0;
	
	private int[][] board;
	private int playerToMoveNum;

	private int latestRow = -1;
	private int latestCol = -1;

	private int movesDone;

	private int evalValue;

	public MyBoard(){
		board = new int[ROWS][COLS];

		for (int[] row : board){
			Arrays.fill(row, EMPTY);
		}

		playerToMoveNum = 1;

		movesDone = 0;
		evalValue = 0;
	}

	public MyBoard(int playerNum, int[][] initialBoard, int movesMade, int eval){
		board = new int[ROWS][COLS];

		for (int row = 0; row < ROWS; row++){
			for (int column = 0; column < COLS; column++){
				board[row][column] = initialBoard[row][column];
			}
		}

		playerToMoveNum = playerNum;

		movesDone = movesMade;
		evalValue = eval;
	}

	public int[][] getBoard() {
		return board;
	}

	public int getPlayerNum() {
		return playerToMoveNum;
	}

	public int getMovesPlayed(){
		return movesDone;
	}

	public int grabEvalValue(){
		return evalValue;
	}

	public boolean isValidMove(int col) {
		return !isColumnFull(col);
	}

	public void makeMove(int col) {
		
		if (!isValidMove(col)) {
			for(col=0; col<COLS; col++) if (isValidMove(col)) break;
		}

		int openRow = findOpenRow(col);

		playerToMoveNum = 3 - playerToMoveNum;

		evalValue = -1 * evalValue;

		evalValue = evalValue - evalAdjust(openRow, col);
		board[openRow][col] = getPlayerNum();
		evalValue = evalValue + evalAdjust(openRow, col);

		movesDone++;

		latestRow = openRow;
		latestCol = col;

	}

	private int findOpenRow(int col){
		for (int i = 0; i < ROWS; i++){
			if (board[i][col] == EMPTY){
				return i;
			}
		}

		return -1;
	}

	private int findTop(int col){
		int row = ROWS - 1;

		while (board[row][col] == EMPTY && row > 0){
			row--;
		}

		return row;
	}

	private int evalAdjust(int openRow, int column){
		int leftOffset, rightOffset, leftBound, rightBound;

		int opponent = 3 - this.playerToMoveNum;
		int mainPlayer = this.playerToMoveNum;

		leftBound = Math.max(column - 3, 0);
		rightBound = Math.min(7, column + 3);

		int horizValue = evalPossibilities(mainPlayer, opponent, leftBound, rightBound,
				openRow, 0);

		leftOffset = Math.min(Math.min(openRow, column), 3);
		rightOffset = Math.min(Math.min(5 - openRow, 7 - column), 3);
		int offsetOpenRow = openRow - leftOffset;
		int offsetRightColumn = column + rightOffset;
		int offsetLeftColumn = column - leftOffset;
		int diagonalDelta = 1;

		int diagValueOne = evalPossibilities(mainPlayer, opponent, offsetLeftColumn, offsetRightColumn, 
				offsetOpenRow, diagonalDelta);

		leftOffset = Math.min(Math.min(5 - openRow, column), 3);
		rightOffset = Math.min(Math.min(openRow, 7 - column), 3);

		offsetOpenRow = openRow + leftOffset;
		offsetRightColumn = column + rightOffset;
		offsetLeftColumn = column - leftOffset;
		diagonalDelta = -1;

		int diagValueTwo = evalPossibilities(mainPlayer, opponent, offsetLeftColumn, 
				offsetRightColumn, offsetOpenRow, diagonalDelta);

		int verticalValue = connect4Verticals(mainPlayer, opponent, openRow, column);

		int sum = verticalValue + horizValue + diagValueOne + diagValueTwo;

		return sum;
	}

	private int connect4Verticals(int mainPlayer, int opponent, int row, int column){
		int possibleBottom;
		possibleBottom = Math.max(0, row - 3);
		int possibleTop = possibleBottom + 4;

		int playerCount = 0;
		int opponentCount = 0;
		int verticalValue = 0;

		for (int checkRow = possibleBottom; checkRow < possibleTop; checkRow++){
			if (board[checkRow][column] == opponent){
				opponentCount = opponentCount + 1;
			} else if (board[checkRow][column] == mainPlayer){
				playerCount = playerCount + 1;
			}
		}
		
		verticalValue = scoreConnected(playerCount, opponentCount, verticalValue);

		return verticalValue;
	}

	public int scoreConnected(int playerCount, int opponentCount, int sum){
		if (playerCount == 0){
			sum = sum - SCORE_GOOD[opponentCount];
		} else if (opponentCount == 0) {
			sum = sum + SCORE_GOOD[playerCount];
		}

		return sum;
	}

	private int evalPossibilities(int mainPlayer, int opponent, int leftBound, 
			int rightBound, int currentRow, int offsetRow){

		int boundDiff = rightBound - leftBound;
		int opponentCount = 0;
		int playerCount = 0;
		int sum = 0;
		int checkColumn = leftBound;
		int checkRow = currentRow; 

		int diagonalDelta = offsetRow * 4;

		if (boundDiff < 3) {
			return 0;
		}

		for (; checkColumn <= leftBound + 3; checkRow += offsetRow) {

			if (board[checkRow][checkColumn] == opponent){
				opponentCount = opponentCount + 1;
			} else if (board[checkRow][checkColumn] == mainPlayer){
				playerCount = playerCount + 1;
			}
			checkColumn = checkColumn + 1;

		}

		sum = scoreConnected(playerCount, opponentCount, sum);

		for (; checkColumn <= rightBound; checkRow += offsetRow){
			if (board[(checkRow - diagonalDelta)][(checkColumn - 4)] == opponent){
				opponentCount = opponentCount -1;
			} 

			if (board[(checkRow - diagonalDelta)][(checkColumn - 4)] == mainPlayer) {
				playerCount = playerCount -1;
			}

			if (board[checkRow][checkColumn] == opponent){
				opponentCount = opponentCount + 1;
			}

			if (board[checkRow][checkColumn] == mainPlayer) {
				playerCount = playerCount + 1;
			}

			sum = scoreConnected(playerCount, opponentCount, sum);

			checkColumn = checkColumn + 1;
		}

		return sum;
	}

	public void undoMove(int column, int stateEval){
		int row = this.findTop(column);

		board[row][column] = EMPTY;

		playerToMoveNum = 3 - playerToMoveNum;

		evalValue = stateEval;
		movesDone--;
	}

	private boolean isColumnFull(int col) {
		return !(board[ROWS - 1][col] == EMPTY);
	}

	public boolean isFull() {
		return (movesDone == ROWS * COLS);
	}

	private boolean checkForFour(int row, int column,
			int rowOffset, int colOffset){

		int winCounter = 0;
		
		int oppRow = 3 * rowOffset + row; 
		int oppColumn = 3 * colOffset + column;

		if ( (movesDone < 7 ) || (oppRow >= ROWS) || (oppColumn >= COLS) ||
				(oppRow < 0) || (oppColumn < 0) ||
				(row < 0) || (column < 0) || 
				(row >= ROWS) || (column >= COLS)){
			return false;
		}

		for (int i = 1; i < 5; i++){
			if (board[row][column] == playerToMoveNum){
				winCounter++;
			}

			row += rowOffset;
			column += colOffset;
		}

		return (winCounter == 4);
	}

	public boolean gameIsOver() {
		if ( isFull() ){
			return true;
		}

		if ( checkForFour(latestRow, latestCol, -1, 0)) return true;

		for (int offset = 0; offset < 4; offset++){
			if ( checkForFour(latestRow, latestCol - offset, 0, 1)) return true;
			if ( checkForFour(latestRow - offset, latestCol + offset, 1, -1)) return true;
			if ( checkForFour(latestRow - offset, latestCol - offset, 1, 1)) return true;
		}

		return false;
	}	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append("   ");
		for (int i = 0; i < COLS; i++) {
			sb.append(i).append(" ");
		}
		sb.append("\n");

		sb.append("   ");
		for (int i = 0; i < COLS; i++) {
			sb.append("--");
		}
		sb.append("\n");

		for (int i = ROWS-1; i>=0; i--) {
			sb.append(Integer.toString(i)).append("| ");
			for (int j = 0; j < COLS; j++) {
				sb.append(Integer.toString(board[i][j])).append(" ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}
}
