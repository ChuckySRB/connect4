package com.mozzartbet.hackaton.connect4.service.executor;

import com.mozzartbet.hackaton.connect4.competition.Game;

public interface GameExecutor {

	Game runGame(Game game, Runnable onMove);
	
}
