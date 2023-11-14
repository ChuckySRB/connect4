package com.mozzartbet.hackaton.connect4.competition;

import static com.google.common.collect.Lists.*;
import static com.mozzartbet.hackaton.connect4.competition.CompetitionConsts.*;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;

public class Competition {

	String name;
	List<String> players = new ArrayList<>();

	GroupPhase groupPhase;
	KnockoutPhase knockoutPhase;

	//

	public Competition() {
	}

	public Competition(String name) {
		this.name = name;
	}

	public void addPlayers(Collection<String> players) {
		this.players.addAll(players);
	}

	//

	public GroupPhase generateGroupPhase() {
		groupPhase = new GroupPhase(GROUP_COUNT, GROUP_PHASE_WINS);

		int totalPlayers = players.size();
		int groupSize = (int) Math.ceil(1.0d * totalPlayers / GROUP_COUNT);

		List<String> shuffled = new ArrayList<>();
		shuffled.addAll(players);
		Collections.shuffle(shuffled);

		partition(shuffled, groupSize).forEach(part -> groupPhase.newGroup(part));

		return groupPhase.generateMatches();
	}

	public KnockoutPhase generateKnockoutPhase() {
		knockoutPhase = new KnockoutPhase();
		knockoutPhase.generateQuarterFinal(groupPhase.groupWinners());
		return knockoutPhase;
	}

	public Competition updateMatch(Match m) {
		switch (m.getPhase()) {
		case GROUP:
			Group group = groupPhase.findGroup(m.getGroup());
			updateMatch(group.getMatches(), m);
			group.updatePoints(m);
			break;
		case QUARTER_FINAL:
			updateMatch(knockoutPhase.getQuarterFinal(), m);
			break;
		case SEMI_FINAL:
			updateMatch(knockoutPhase.getSemiFinal(), m);
			break;
		case THIRD_PLACE:
			updateMatch(asList(knockoutPhase.getThirdPlace()), m);
			break;
		case FINAL:
			updateMatch(asList(knockoutPhase.getTheFinal()), m);
			break;
		default:
			throw new IllegalStateException("Invalid match phase: " + m);
		}

		return this;
	}

	void updateMatch(List<Match> matches, Match m) {
		for (Match existing : matches) {
			if (Objects.equal(m.getId(), existing.getId())) {
				existing.updateFrom(m);
				return;
			}
		}
	}

	//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPlayers() {
		return players;
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}

	public GroupPhase getGroupPhase() {
		return groupPhase;
	}

	public void setGroupPhase(GroupPhase groupPhase) {
		this.groupPhase = groupPhase;
	}

	public KnockoutPhase getKnockoutPhase() {
		return knockoutPhase;
	}

	public void setKnockoutPhase(KnockoutPhase knockoutPhase) {
		this.knockoutPhase = knockoutPhase;
	}

}
