package com.mozzartbet.hackaton.connect4.competition;

import static com.mozzartbet.hackaton.connect4.competition.CompetitionConsts.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Group {

	String name;

	List<PlayerPosition> positions;

	List<Match> matches = new ArrayList<>();

	//

	public Group() {
	}

	public Group(String name) {
		this.name = name;
	}

	Group addPlayers(Collection<String> playerNames) {
		this.positions = playerNames.stream().map(PlayerPosition::new).collect(toList());
		return this;
	}

	void generateMatches(int count, int totalWins) {
		long id = count * GROUP_MATCH_ID;
		
		int n = positions.size();
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				Match m = new Match(id ++, name, positions.get(i).name, positions.get(j).name, totalWins);
				matches.add(m);
			}
		}
	}

	public void updatePoints(Match match) {
		if (match.finished()) {
			findByName(match.winner()).ifPresent(p -> p.addPoints(match));
			findByName(match.loser()).ifPresent(p -> p.addPoints(match));
			sortPositions();
		}
	}
	
	Optional<PlayerPosition> findByName(String name) {
		return positions.stream().filter(p -> p.name.equals(name)).findFirst();
		
	}
	public List<String> getTop(int n) {
		sortPositions();
		return positions.subList(0,  n).stream().map(p -> p.name).collect(toList());
	}

	private void sortPositions() {
		positions.sort(null);
	}

	//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PlayerPosition> getPositions() {
		return positions;
	}

	public void setPositions(List<PlayerPosition> positions) {
		this.positions = positions;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}

	@Override
	public String toString() {
		return String.format("Group [name=%s, positions=\n%s]", name, positions);
	}

}
