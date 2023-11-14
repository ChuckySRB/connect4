package com.mozzartbet.hackaton.connect4.bot.mv_bot;

public class SearchConfig {
	public int E_WIN;
	public int A_WIN;
	
	
	public SearchConfig(){
		E_WIN=0;
		A_WIN=0;
	}
	
	public SearchConfig(SearchConfig c){
		E_WIN=c.E_WIN;
		A_WIN=c.A_WIN;
	}
	
	public SearchConfig(int enemyWins, int allyWins){
		E_WIN=enemyWins;
		A_WIN=allyWins;
	}
	

}
