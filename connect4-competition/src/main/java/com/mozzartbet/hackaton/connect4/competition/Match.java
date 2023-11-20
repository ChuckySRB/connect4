package com.mozzartbet.hackaton.connect4.competition;

import static com.mozzartbet.hackaton.connect4.competition.Game.Outcome.*;
import static java.lang.String.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mozzartbet.hackaton.connect4.competition.Game.Outcome;

public class Match {

	String id;

	MatchPhase phase;
	String group;

	String one;
	String two;

	int totalWins;

	LocalDateTime startTime;

	List<Game> games;

	Outcome outcome = UNKNOWN;

	int winsOne;
	int winsTwo;
	int draws;

	LocalDateTime endTime;

	//

	public Match() {
	}

	public Match(Long id, String group, String one, String two, int totalWins) {
		this(id, MatchPhase.GROUP, group, one, two, totalWins);
	}

	public Match(Long id, MatchPhase phase, String one, String two, int totalWins) {
		this(id, phase, null, one, two, totalWins);
	}

	Match(Long id, MatchPhase phase, String group, String one, String two, int totalWins) {
		this.id = id.toString();
		this.phase = phase;
		this.group = group;
		this.one = one;
		this.two = two;
		this.totalWins = totalWins;
	}

	//

	public Match start() {
		startTime = LocalDateTime.now();
		return this;
	}

	public Match updateFrom(Match other) {
		this.startTime = other.startTime;
		this.outcome = other.outcome;
		this.winsOne = other.winsOne;
		this.winsTwo = other.winsTwo;
		this.draws = other.draws;
		this.endTime = other.endTime;
		return this;
	}
	
	public Match end() {
		endTime = lastGame().getEndTime();
		return this;
	}

	public Game nextGame() {
		if (games == null) {
			games = new ArrayList<>();
		}
		
		if (finished()) {
			return null;
		}

		if (games.isEmpty()) {
			return newGame();
		}

		Game next = lastGame();
		if (next.winner() == null && next.outcome != DRAW) {
			return next;
		}

		return newGame();
	}

	Game newGame() {
		Game game = new Game(one, two, games.size());
		games.add(game);
		return game;
	}

	public Game lastGame() {
		return (games == null || games.isEmpty()) ? null : games.get(games.size() - 1);
	}

	public boolean finished() {
		if (outcome.equals(UNKNOWN)) {
			determineOutcome();
		}
		return outcome.equals(WINNER_ONE) || outcome.equals(WINNER_TWO);
	}

	private void determineOutcome() {
		outcome = UNKNOWN;

		winsOne = 0;
		winsTwo = 0;
		draws = 0;

		if (games == null) {
			return;
		}
		
		for (Game game : games) {
			if (game.outcome.equals(DRAW)) {
				draws++;
				continue;
			}

			String winner = game.winner();
			if (winner == null) {
				continue;
			}

			if (one.equals(winner)) {
				winsOne++;
			} else if (two.equals(winner)) {
				winsTwo++;
			}

			if (winsOne == totalWins) {
				outcome = WINNER_ONE;
				endTime = game.endTime;
			}
			if (winsTwo == totalWins) {
				outcome = WINNER_TWO;
				endTime = game.endTime;
			}
		}
	}

	public String winner() {
		return Game.decode(outcome, WINNER_ONE, one, WINNER_TWO, two, null);
	}

	public String loser() {
		return Game.decode(outcome, WINNER_ONE, two, WINNER_TWO, one, null);
	}
	
	//

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MatchPhase getPhase() {
		return phase;
	}

	public void setPhase(MatchPhase phase) {
		this.phase = phase;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

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

	public int getWinsOne() {
		return winsOne;
	}

	public void setWinsOne(int winsOne) {
		this.winsOne = winsOne;
	}

	public int getWinsTwo() {
		return winsTwo;
	}

	public void setWinsTwo(int winsTwo) {
		this.winsTwo = winsTwo;
	}

	public int getDraws() {
		return draws;
	}

	public void setDraws(int draws) {
		this.draws = draws;
	}

	public int getTotalWins() {
		return totalWins;
	}

	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Outcome getOutcome() {
		return outcome;
	}

	public void setOutcome(Outcome outcome) {
		this.outcome = outcome;
	}

	@Override
	public String toString() {
		return format("[Match id=%s %s%s `%s` vs `%s` (to %s) outcome=%s (%s:%s:%s), startTime=%s, endTime=%s]",
				id, phase, (group != null) ? " " + group : "",
				one, two, totalWins,
				outcome, winsOne, winsTwo, draws, startTime, endTime);
	}

}
