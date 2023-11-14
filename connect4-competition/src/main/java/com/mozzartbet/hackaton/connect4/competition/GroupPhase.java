package com.mozzartbet.hackaton.connect4.competition;

import static com.mozzartbet.hackaton.connect4.competition.CompetitionConsts.*;

import java.util.ArrayList;
import java.util.List;

public class GroupPhase {

	int groupCount;
	int totalWins;

	List<Group> groups = new ArrayList<>();

	public GroupPhase() {
	}

	public GroupPhase(int groupCount, int totalWins) {
		this.groupCount = groupCount;
		this.totalWins = totalWins;
	}

	GroupPhase newGroup(List<String> playerNames) {
		groups.add(new Group(Character.toString((char) ('A' + groups.size()))).addPlayers(playerNames));
		return this;
	}

	Group findGroup(String name) {
		return groups.stream().filter(g -> name.equals(g.name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException(name));
	}

	GroupPhase generateMatches() {
		int count = 1;
		for (Group group : groups) {
			group.generateMatches(count++, totalWins);
		}
		return this;
	}

	List<List<String>> groupWinners() {
		List<List<String>> winners = new ArrayList<>(groups.size());
		int top = QUARTER_FINAL_MATCHES / groupCount;
		groups.forEach(g -> winners.add(g.getTop(top)));
		return winners;
	}

	public List<Match> getAllMatches() {
		List<Match> all = new ArrayList<>();
		groups.forEach(g -> all.addAll(g.getMatches()));
		return all;
	}

	//

	public int getGroupCount() {
		return groupCount;
	}

	public void setGroupCount(int groupCount) {
		this.groupCount = groupCount;
	}

	public int getTotalWins() {
		return totalWins;
	}

	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

}
