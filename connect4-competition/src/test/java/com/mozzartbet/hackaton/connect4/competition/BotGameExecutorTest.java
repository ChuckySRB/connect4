package com.mozzartbet.hackaton.connect4.competition;

import static com.google.common.collect.Iterables.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.mozzartbet.hackaton.connect4.service.executor.BotGameExecutor;

public class BotGameExecutorTest {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	BotGameExecutor e;
	Game g;
	
	@Test
	public void testGame() {
		e = new BotGameExecutor(5000);

		g = new Game("dummy1", "dummy2", 0);
		e.runGame(g, () -> {
			logger.debug("Played: " + getLast(g.getMoves()));
		});
		
		logger.debug("Game: {}", g);
		logger.debug("Game moves: {}", Joiner.on('\n').join(g.getMoves()));
		
		logger.debug("\n{}", e.getBoard());
	}
}
