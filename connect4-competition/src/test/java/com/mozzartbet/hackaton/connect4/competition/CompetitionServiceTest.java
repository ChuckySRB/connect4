package com.mozzartbet.hackaton.connect4.competition;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozzartbet.hackaton.connect4.service.CompetitionService;
import com.mozzartbet.hackaton.connect4.service.CompetitionServiceImpl;

public class CompetitionServiceTest {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	final CompetitionService competitionService = new CompetitionServiceImpl();

	@Test
	public void testTournament() {
		int totalPlayers = 16;
		competitionService.generateCompetition("hackaton-2016",
				rangeClosed(1, totalPlayers).mapToObj(i -> "dummy_" + i).collect(toList()));
		
		logger.debug("===== Group phase ====== ");
		competitionService.generateGroupPhase();
	
		Match match = competitionService.nextMatch();
		
		while (match != null) {
			logger.debug("Starting {}", match);
			
			boolean finished = false;
			while (!finished) {
				finished = competitionService.runMatchGame(match);
				logger.debug("-- {}", match.lastGame());
			}
			
			logger.debug("Ended {}", match);
			
			//
			
			match = competitionService.nextMatch();
			
			if (match == null) {
				MatchPhase phase = competitionService.nextPhase();
				logger.debug("===== {} phase ===== ", phase);
				match = competitionService.nextMatch();
			}
		}
	}
	
	

}
