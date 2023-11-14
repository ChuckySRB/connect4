package com.mozzartbet.hackaton.connect4.competition;

import static java.time.ZoneId.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class PlayerPosition implements Comparable<PlayerPosition> {

	String name;

	int points;

	int totalWins;
	int totalLoses;
	long totalTime;

	double winCoef;
	
	public PlayerPosition() {
	}

	public PlayerPosition(String name) {
		this.name = name;
		this.points = 0;
	}

	public PlayerPosition addPoints(Match m) {
		boolean winner = name.equals(m.winner());

		if (winner) {
			points++;
		}

		totalWins += (name.equals(m.one)) ? m.winsOne : m.winsTwo;
		totalLoses += (name.equals(m.one)) ? m.winsTwo : m.winsOne;
		calculateWinCoef();
		
		totalTime += elapsedTime(m.startTime, m.endTime, systemDefault());

		return this;
	}

	private static long elapsedTime(LocalDateTime start, LocalDateTime end, ZoneId zoneId) {
		return end.atZone(zoneId).toInstant().toEpochMilli()
				- start.atZone(zoneId).toInstant().toEpochMilli();

	}

	@Override
	public int compareTo(PlayerPosition p) {
		int c = -Integer.compare(points, p.points);
		if (c != 0) {
			return c;
		}

		c = - Double.compare(getWinCoef(), p.getWinCoef());
		if (c != 0) {
			return c;
		}

		c = -Long.compare(totalTime, p.totalTime);
		return c;
	}

	double calculateWinCoef() {
		int total = totalWins + totalLoses;
		winCoef = (total > 0) ? (1d * totalWins / total) : 0d;
		return winCoef;
	}

	//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getTotalWins() {
		return totalWins;
	}

	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}

	public int getTotalLoses() {
		return totalLoses;
	}

	public void setTotalLoses(int totalLoses) {
		this.totalLoses = totalLoses;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public double getWinCoef() {
		return winCoef;
	}
	
	public void setWinCoef(double winCoef) {
		this.winCoef = winCoef;
	}

	@Override
	public String toString() {
		return String.format(
				"PlayerPosition [name=%s, points=%s, totalWins=%s, totalLoses=%s, totalTime=%s, winCoef=%s]", name,
				points, totalWins, totalLoses, totalTime, winCoef);
	}
	

}
