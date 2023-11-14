package com.mozzartbet.hackaton.connect4.competition;

import static com.mozzartbet.hackaton.connect4.competition.MatchPhase.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozzartbet.hackaton.connect4.service.CompetitionService;
import com.mozzartbet.hackaton.connect4.service.CompetitionServiceImpl;

public class CompetitionStagesTest {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	final CompetitionService competitionService = new CompetitionServiceImpl();

	public void generatePlayers() {
		int totalPlayers = 15;
		competitionService.generateCompetition("hackaton-2016",
				rangeClosed(1, totalPlayers).mapToObj(i -> "dummy_" + i).collect(toList()));
	}
	@Test
	public void addPlayers() {
		competitionService.generateCompetition("hackaton-2016",
				asList(
						"abi", "acko", "bn140314d", "dux", "ks",
						"lemibot", "m_markobot", "milosk", "mladen_brankovic", "mv_bot", 
						"nikola_jovanovic", "petja", "Vesna", "vs", "walama"));
	}
	
	public void runGroupPhase() {
		runMatches(null, GROUP);
	}

	public void runQuarterFinal() {
		runMatches(GROUP, QUARTER_FINAL);
	}
	
	public void runSemiFinal() {
		runMatches(QUARTER_FINAL, SEMI_FINAL);
	}

	public void runThirdPlace() {
		runMatches(SEMI_FINAL, THIRD_PLACE);
	}

	@Test
	public void runFinal() {
		runMatches(THIRD_PLACE, FINAL);
	}

	//
	
	private void runMatches(MatchPhase curr, MatchPhase next) {
		MatchPhase phase = competitionService.currentPhase();
		if (!next.equals(phase)) {
			assertEquals(curr, phase);
			phase = competitionService.nextPhase();
			assertEquals(next, phase);
		}

		Match match = competitionService.nextMatch();
		
		while (match != null) {
			runMatch(match);
			match = competitionService.nextMatch();
		}
		
		logger.debug("==== Finished {} phase =====", phase);

	}

	private void runMatch(Match match) {
		logger.debug("Starting {}", match);
		
		boolean finished = false;
		while (!finished) {
			finished = competitionService.runMatchGame(match);
			logger.debug("-- {}", match.lastGame());
		}
		
		logger.debug("Ended {}", match);
	}
	
}