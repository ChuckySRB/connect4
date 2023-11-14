package com.mozzartbet.hackaton.connect4.service;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Throwables.*;
import static com.mozzartbet.hackaton.connect4.competition.CompetitionConsts.*;
import static com.mozzartbet.hackaton.connect4.competition.Game.Outcome.*;
import static com.mozzartbet.hackaton.connect4.competition.MatchPhase.*;
import static java.util.Arrays.*;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import com.mozzartbet.hackaton.connect4.competition.Competition;
import com.mozzartbet.hackaton.connect4.competition.Game;
import com.mozzartbet.hackaton.connect4.competition.GroupPhase;
import com.mozzartbet.hackaton.connect4.competition.KnockoutPhase;
import com.mozzartbet.hackaton.connect4.competition.Match;
import com.mozzartbet.hackaton.connect4.competition.MatchPhase;
import com.mozzartbet.hackaton.connect4.mongo.JongoManager;
import com.mozzartbet.hackaton.connect4.service.executor.BotGameExecutor;

public class CompetitionServiceImpl implements CompetitionService {

	@Override
	public Competition generateCompetition(String name, List<String> playerNames) {
		competitions().drop();
		matches().drop();

		Competition c = new Competition(name);
		c.addPlayers(playerNames);

		saveCompetition(c);
		return c;
	}

	@Override
	public GroupPhase generateGroupPhase() {
		Competition c = findCompetition();

		c.generateGroupPhase();

		saveCompetition(c);
		saveMatches(c.getGroupPhase().getAllMatches());

		return c.getGroupPhase();
	}

	@Override
	public KnockoutPhase generateQuarterFinal() {
		checkNoUnknownMatches(GROUP);

		Competition c = findCompetition();
		c.generateKnockoutPhase();

		saveCompetition(c);
		saveMatches(c.getKnockoutPhase().getQuarterFinal());

		return c.getKnockoutPhase();
	}

	@Override
	public KnockoutPhase generateSemiFinal() {
		checkNoUnknownMatches(QUARTER_FINAL);

		Competition c = findCompetition();
		c.getKnockoutPhase().generateSemiFinal();

		saveCompetition(c);
		saveMatches(c.getKnockoutPhase().getSemiFinal());

		return c.getKnockoutPhase();
	}

	@Override
	public KnockoutPhase generateThirdPlace() {
		checkNoUnknownMatches(SEMI_FINAL);

		Competition c = findCompetition();
		c.getKnockoutPhase().generateThirdPlace();

		saveCompetition(c);
		saveMatches(asList(c.getKnockoutPhase().getThirdPlace()));

		return c.getKnockoutPhase();
	}

	@Override
	public KnockoutPhase generateFinal() {
		checkNoUnknownMatches(SEMI_FINAL);

		Competition c = findCompetition();
		c.getKnockoutPhase().generateFinal();

		saveCompetition(c);
		saveMatches(asList(c.getKnockoutPhase().getTheFinal()));

		return c.getKnockoutPhase();
	}

	private void checkNoUnknownMatches(MatchPhase phase) {
		long k = matches().count("{ phase: #, outcome: #}", phase, UNKNOWN);
		checkState(k == 0, "Not all " + phase + " matches finished: " + k);
	}

	//

	@Override
	public MatchPhase currentPhase() {
		Competition c = findCompetition();

		if (c.getGroupPhase() == null) {
			return null;
		}
		if (c.getKnockoutPhase() == null) {
			return GROUP;
		}

		return c.getKnockoutPhase().getPhase();
	}

	@Override
	public Match nextMatch() {
		MatchPhase phase = currentPhase();
		if (phase.equals(FINISHED)) {
			return null;
		}

		MongoCursor<Match> im;

		im = matches().find("{ phase: #, outcome: #, startTime: { $ne : null } }", phase, UNKNOWN)
				.sort("{ id: 1 }").as(Match.class);

		if (!im.hasNext()) {
			im = matches().find("{ phase: #, outcome: # }", phase, UNKNOWN)
					.sort("{ id: 1 }").as(Match.class);
		}

		if (!im.hasNext()) {
			// next phase!!!
			return null;
		}

		Match m = im.next();

		if (m.getStartTime() == null) {
			m.start();
			updateMatch(m);
		}

		return m;
	}

	@Override
	public boolean runMatchGame(Match m) {
		Game game = m.nextGame();

		if (game == null) {
			return true;
		}

		runGame(m, game);

		if (m.finished()) {
			m.end();
		}

		updateMatch(m);

		return m.finished();
	}

	public void updateMatch(Match m) {
		saveMatch(m);

		Competition c = findCompetition();
		c.updateMatch(m);
		saveCompetition(c);
	}

	private void runGame(Match m, Game game) {
		BotGameExecutor executor = new BotGameExecutor(TIMEOUT_MILLIS);
		executor.runGame(game, () -> saveMatch(m));
	}

	@Override
	public MatchPhase nextPhase() {
		Competition c = findCompetition();

		if (c.getGroupPhase() == null) {
			generateGroupPhase();
			return currentPhase();
		}

		KnockoutPhase kp = c.getKnockoutPhase();

		if (kp == null) {
			generateQuarterFinal();
		} else if (kp.getPhase().equals(QUARTER_FINAL)) {
			generateSemiFinal();
		} else if (kp.getPhase().equals(SEMI_FINAL)) {
			generateThirdPlace();
		} else if (kp.getPhase().equals(THIRD_PLACE)) {
			generateFinal();
		} else if (kp.getPhase().equals(FINAL)) {
			kp.setPhase(FINISHED);
			return FINISHED;
		}

		return currentPhase();
	}

	//

	void saveCompetition(Competition c) {
		competitions().update("{ name: # }", c.getName()).upsert().with(c);
	}

	void saveMatch(Match m) {
		matches().update("{ id: # }", m.getId()).upsert().with(m);
	}

	void saveMatches(Collection<Match> matches) {
		matches.forEach(m -> saveMatch(m));
	}

	Competition c = null;

	Competition findCompetition() {
		if (c == null) {
			try {
				try (MongoCursor<Competition> i = competitions().find().as(Competition.class)) {
					c = i.next();
				}
			} catch (IOException e) {
				throw propagate(e);
			}
		}
		return c;
	}

	//

	Jongo jongo() {
		return JongoManager.jongo();
	}

	MongoCollection competitions() {
		return jongo().getCollection("competitions");
	}

	MongoCollection matches() {
		return jongo().getCollection("matches");
	}

}
