package com.mozzartbet.hackaton.connect4.service.executor;

import com.mozzartbet.hackaton.connect4.bot.Bots;
import com.mozzartbet.hackaton.connect4.bot.example.DummyPlayer;
import com.mozzartbet.hackaton.connect4.model.Player;

class BotLoader {

	private static final String BOT_PACKAGE = Bots.class.getPackage().getName();

	public Player loadPlayer(String name) {
		if (name.startsWith("dummy_")) {
			return new DummyPlayer();
		}
		
		Class<?> playerClass = loadPlayerClass(name);
		try {
			return (Player) playerClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to instantiate: " + playerClass, e);
		}
	}

	private Class<?> loadPlayerClass(String name) {
		String className = BOT_PACKAGE + '.' + name + ".Bot";
		try {
			Class<?> playerClass = Class.forName(className);
			if (!Player.class.isAssignableFrom(playerClass)) {
				throw new IllegalArgumentException("Not a Player class: " + playerClass);
			}

			return playerClass;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Not found: " + name, e);
		}

	}
}
