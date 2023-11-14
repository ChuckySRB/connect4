package com.mozzartbet.hackaton.connect4.service;

import java.util.List;

import com.mozzartbet.hackaton.connect4.competition.Competition;
import com.mozzartbet.hackaton.connect4.competition.GroupPhase;
import com.mozzartbet.hackaton.connect4.competition.KnockoutPhase;
import com.mozzartbet.hackaton.connect4.competition.Match;
import com.mozzartbet.hackaton.connect4.competition.MatchPhase;

public interface CompetitionService {

	Competition generateCompetition(String name, List<String> playerNames);


	MatchPhase currentPhase();

	MatchPhase nextPhase();

	Match nextMatch();

	boolean runMatchGame(Match m);


	//
	GroupPhase generateGroupPhase();

	KnockoutPhase generateQuarterFinal();

	KnockoutPhase generateSemiFinal();

	KnockoutPhase generateThirdPlace();

	KnockoutPhase generateFinal();

}
