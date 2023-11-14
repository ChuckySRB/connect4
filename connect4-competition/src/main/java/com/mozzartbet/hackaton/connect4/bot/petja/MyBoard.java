package com.mozzartbet.hackaton.connect4.bot.petja;

import java.awt.Point;
import java.util.ArrayList;

public class MyBoard {

	
	protected int[][] board;
	ArrayList<Point> myMoves,hisMoves;
	
	
	public MyBoard(){
		initializeBoard();
		myMoves = new ArrayList<>();
		hisMoves = new ArrayList<>();
	}
	public int[][] getBoard() {
		return board;
	}
	private void initializeBoard(){
		board = new int[6][8];
		for(int i  = 0;i < 6;i++){
			for(int j  = 0;j < 8;j++){
				board[i][j] = 0;
			}
		}
	}
	public void printBoard(){
		
		for(int i  = 0;i < 6;i++){
			for(int j  = 0;j < 8;j++){
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
		for (int i = 0; i < 5; i++) {
			System.out.println();
		}
	}
	public void addMove(int move, int player){
		int i = 0;
		while(i < 5 && board[i+1][move] == 0){
			i++;
		}
		board[i][move] = player;
		if(player == 1)myMoves.add(new Point(i,move));
		else hisMoves.add(new Point(i,move));
	}
	public ArrayList<Point> getMyMoves() {
		return myMoves;
	}
	public ArrayList<Point> getHisMoves() {
		return hisMoves;
	}
}
