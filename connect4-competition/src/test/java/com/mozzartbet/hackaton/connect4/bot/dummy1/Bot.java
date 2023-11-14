package com.mozzartbet.hackaton.connect4.bot.dummy1;

import com.mozzartbet.hackaton.connect4.bot.example.DummyPlayer;

public class Bot extends DummyPlayer {

	@Override
	public void move() {
		super.move();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
