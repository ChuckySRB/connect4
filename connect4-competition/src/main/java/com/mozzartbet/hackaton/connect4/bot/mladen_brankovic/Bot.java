package com.mozzartbet.hackaton.connect4.bot.mladen_brankovic;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;
import static com.mozzartbet.hackaton.connect4.util.ThreadHelper.sleep;

import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

	long timeoutMillis = 50;
	int opponentMoves = 0;
	GameBoard gameBoard;
	int [][] board;
	int igram_prvi,max1,max2;
	int c=0;
	int p=0;
	int k=0;
	@Override
	public void configure(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis; 
		int c=0;
		int p=0;
		int k=0;
	}

	@Override
	public void move() 
	{
		if(k<3)
		{
			if(k==0)
				move=4;
			if(k==1)
				move=5;
			if(k==2)
			{
				move=3;
			}
			k++;
		}
		else
		{
		if(p==1)
		c++;
		if (p==0)
		{
			move = (2+c)%7;
			p=1;
		}
		else 
			{
				move = Math.abs((4-c)%7);
				p=0;
			}
		
		}
	}

	@Override
	public void stop() {
		move = ThreadLocalRandom.current().nextInt(COLUMNS);
		
	}

	@Override
	public void opponentMove(int move) {
		opponentMoves++;
	}

	@Override
	public void finished(int winner) {
		// TODO Auto-generated method stub

	}

}
