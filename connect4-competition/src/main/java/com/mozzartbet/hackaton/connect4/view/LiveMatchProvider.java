package com.mozzartbet.hackaton.connect4.view;

import static com.google.common.base.Preconditions.*;
import static com.mozzartbet.hackaton.connect4.competition.Game.Outcome.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.*;
import static java.util.stream.StreamSupport.*;

import java.util.List;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozzartbet.hackaton.connect4.competition.Game;
import com.mozzartbet.hackaton.connect4.competition.Match;
import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.mongo.JongoManager;

public class LiveMatchProvider implements MatchProvider {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<MatchInfo> availableMatches() {
		Iterable<Match> i = matches()
				.find("{ outcome: #, startTime: { $ne : null } }", UNKNOWN)
				.sort("{ id: 1 }").as(Match.class);

		return stream(i.spliterator(), false)
				.map(m -> new MatchInfo(m.getId(), describe(m)))
				.collect(toList());
	}

	@Override
	public MatchInfo refresh(MatchInfo info) {
		Match m = matches().findOne("{ id: # }", info.getId()).as(Match.class);
		checkState(info.getId().equals(m.getId()));

		Game last = m.lastGame();

		logger.debug("Refresh {} - {}", m.getId(), last);
		
		info.setTitle(describe(m));
		
		info.setBoard(new GameBoard());

		if (last != null) {
			last.populateBoard(info.getBoard());
		}

		return info;
	}

	private String describe(Match m) {
		StringBuilder sb = new StringBuilder();
		sb.append(format("Match #%s - `%s` vs `%s` (%s:%s:%s) to %s wins",
				m.getId(), m.getOne(), m.getTwo(),
				m.getWinsOne(), m.getWinsTwo(), m.getDraws(),
				m.getTotalWins()));

		Game last = m.lastGame();
		if (last != null) {
			sb.append(format("\nGame #%s - Red `%s` vs Yellow `%s`- %s move(s)",
					m.getGames().size(),
					last.getOne(), last.getTwo(),
					last.getMoves().size()));
		}
		return sb.toString();
	}

	//

	Jongo jongo() {
		return JongoManager.jongo();
	}

	MongoCollection matches() {
		return jongo().getCollection("matches");
	}

}
