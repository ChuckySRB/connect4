package com.mozzartbet.hackaton.connect4.competition;

public class Move {

	int counter;
	int move;
	boolean valid;
	int row;
	int col;

	public Move() {
	}

	public Move(int counter, int move, boolean valid) {
		this(counter, move, valid, -1, move);
	}

	Move(int counter, int move, boolean valid, int row, int col) {
		this.counter = counter;
		this.move = move;
		this.row = row;
		this.col = col;
		this.valid = valid;
	}

	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	@Override
	public String toString() {
		return String.format("Move [counter=%s, move=%s, valid=%s, row=%s, col=%s]", counter, move, valid, row, col);
	}

}