package com.mozzartbet.hackaton.connect4.bot.m_markobot;

import com.mozzartbet.hackaton.connect4.model.Direction;
import com.mozzartbet.hackaton.connect4.model.Player;

import static com.mozzartbet.hackaton.connect4.model.Direction.E;
import static com.mozzartbet.hackaton.connect4.model.Direction.NE;
import static com.mozzartbet.hackaton.connect4.model.Direction.NW;
import static com.mozzartbet.hackaton.connect4.model.Direction.S;
import static com.mozzartbet.hackaton.connect4.model.Direction.SE;
import static com.mozzartbet.hackaton.connect4.model.Direction.SW;
import static com.mozzartbet.hackaton.connect4.model.Direction.W;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.*;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Bot extends Player {


	public void configure(long timeoutMillis) {
	

	}

	public void move() {
		ThreadLocalRandom rnd = ThreadLocalRandom.current();
		move = rnd.nextInt(COLUMNS);
	
		
	}

	public void stop() {

	}

	public void opponentMove(int move) {
		
	
	}

	public void finished(int winner) {
	
	}
	

	

	
}
