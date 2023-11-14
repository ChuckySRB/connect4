package com.mozzartbet.hackaton.connect4.bot.mv_bot;

public class AdvancedMove {
	int move;
	int winner;
	int sourceDepth;
	int risk;
	
	public AdvancedMove(int m,int w,int sd,int r){
		move=m;
		winner=w;
		sourceDepth=sd;
		risk=r;
		
	}

	public AdvancedMove() {
		// TODO Auto-generated constructor stub
	}

}
