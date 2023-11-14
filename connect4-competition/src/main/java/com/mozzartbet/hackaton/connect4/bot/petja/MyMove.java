package com.mozzartbet.hackaton.connect4.bot.petja;

public class MyMove {
private int move;
private int level;
public MyMove(int move, int level) {
	super();
	this.move = move;
	this.level = level;
}
@Override
public String toString() {
	return "Move [move=" + move + ", level=" + level + "]";
}
public int getMove() {
	return move;
}
public int getLevel() {
	return level;
}
@Override
public boolean equals(Object arg0) {
	// TODO Auto-generated method stub
	if(((MyMove)arg0).level == level) System.out.println("do not " + level);
	return ((MyMove)arg0).level == level;
}
}
