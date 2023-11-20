package com.mozzartbet.hackaton.connect4.bot.YNWA;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

    private GameBoard gameBoard;
    @Override
    public void configure(long timeoutMillis) {
    	
    }

    @Override
    public void move() {
    		move = 5;
    }

    @Override
    public void stop() {

    }

    @Override
    public void opponentMove(int move) {

    }

    @Override
    public void finished(int winner) {

    }
}
