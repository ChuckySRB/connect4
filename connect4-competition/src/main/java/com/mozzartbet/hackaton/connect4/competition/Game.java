package com.mozzartbet.hackaton.connect4.competition;

import static com.mozzartbet.hackaton.connect4.competition.Game.Outcome.*;
import static java.lang.String.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mozzartbet.hackaton.connect4.model.GameBoard;

public class Game {

	String one;
	String two;

	LocalDateTime startTime;

	List<Move> moves = new ArrayList<>();

	public enum Outcome {
		UNKNOWN,
		WINNER_ONE,
		WINNER_TWO,
		DRAW;
	};

	Outcome outcome = UNKNOWN;
	LocalDateTime endTime;

	//

	public Game() {
	}

	Game(String one, String two, int count) {
		this.one = (count % 2 == 0) ? one : two;
		this.two = (count % 2 == 0) ? two : one;
	}

	public void start() {
		startTime = LocalDateTime.now();
	}

	public void end(Outcome outcome) {
		this.outcome = outcome;
		endTime = LocalDateTime.now();
	}

	public String winner() {
		return decode(outcome, WINNER_ONE, one, WINNER_TWO, two, null);
	}

	//

	public void populateBoard(GameBoard board) {
		if (board != null) {
			moves.forEach(m -> board.placeCounter(m.getCol(), m.getCounter()));
		}
	}

	
	//
	public String getOne() {
		return one;
	}

	public void setOne(String one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public List<Move> getMoves() {
		return moves;
	}

	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}

	public Outcome getOutcome() {
		return outcome;
	}

	public void setOutcome(Outcome outcome) {
		this.outcome = outcome;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return format("[Game `%s` vs `%s` outcome=%s, winner=%s, startTime=%s, endTime=%s]",
				one, two, outcome, winner(), startTime, endTime);
	}
	
	//
	
	static String decode(Outcome o, Outcome o1, String s1, Outcome o2, String s2, String s3) {
		return (o.equals(o1)) ? s1 : ((o.equals(o2)) ? s2 : s3);
	}

}
