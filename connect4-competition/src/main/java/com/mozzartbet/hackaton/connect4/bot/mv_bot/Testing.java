package com.mozzartbet.hackaton.connect4.bot.mv_bot;

public class Testing {

	public static void main(String[] args) {
		MyBoard mb=new MyBoard();
		
		mb.addMove(0, 1);
		mb.addMove(0, 1);
		mb.addMove(0, 1);
		mb.addMove(0, 1);
		mb.addMove(0, 1);
		mb.addMove(0, 1);
		System.out.println(mb.findDepth(0));
		
		
		mb.printBoard();

	}

}
