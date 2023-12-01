package com.mozzartbet.hackaton.connect4.poluprovodnici;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

    private GameBoard gameBoard;

    private int time_needed;

    @Override
    public void configure(long timeoutMillis) {

    }

    @Override
    public void move() {
        move = 2;
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
