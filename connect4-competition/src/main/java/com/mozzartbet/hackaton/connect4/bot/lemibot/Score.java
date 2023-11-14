package com.mozzartbet.hackaton.connect4.bot.lemibot;

public class Score {
	private int first;
	private int second;
	public Score() {
		this.first = 0;
		this.second = 0;
	}
	public Score(int red, int yellow) {
		this.first = red;
		this.second = yellow;
	}
	public int getFirst() {
		return first;
	}
	public int getSecond() {
		return second;
	}
	public int get(int player) {
		if (player == 1) {
			return getFirst();
		} 
		return getSecond();
	}
	public void increase(int player) {
		if (player == 1) {
			first++;
		} else {
			second++;
		}
	}
}
