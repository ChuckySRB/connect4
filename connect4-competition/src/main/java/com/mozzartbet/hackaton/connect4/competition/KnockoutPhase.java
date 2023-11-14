package com.mozzartbet.hackaton.connect4.competition;

import static com.mozzartbet.hackaton.connect4.competition.CompetitionConsts.*;
import static com.mozzartbet.hackaton.connect4.competition.MatchPhase.*;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;

public class KnockoutPhase {

	List<Match> quarterFinal;
	List<Match> semiFinal;
	
	Match thirdPlace;
	Match theFinal;

	MatchPhase phase;

	//

	public KnockoutPhase() {
	}

	KnockoutPhase generateQuarterFinal(List<List<String>> gWinners) {
		quarterFinal = new ArrayList<>();

		phase = QUARTER_FINAL;
		long id = QUARTER_FINAL_MATCH_ID;

		for (int g = 0; g < gWinners.size() / 2; g++) {
			List<String> aWinners = gWinners.get(g);
			List<String> bWinners = gWinners.get(gWinners.size() - g - 1);
			
			for (int i = 0, n = aWinners.size(); i < n; i++) {
				Match m = new Match(id++, phase, aWinners.get(i), bWinners.get(n - i - 1), QUARTER_FINAL_WINS);
				quarterFinal.add(m);
			}
		}

		return this;
	}

	public KnockoutPhase generateSemiFinal() {
		List<String> qWinners = winners(quarterFinal);

		semiFinal = new ArrayList<>();

		phase = SEMI_FINAL;
		long id = SEMI_FINAL_MATCH_ID;

		int n = qWinners.size();
		for (int i = 0; i < n / 2; i++) {
			Match m = new Match(id++, phase, qWinners.get(i), qWinners.get(n - i - 1), SEMI_FINAL_WINS);
			semiFinal.add(m);
		}
		
		return this;
	}

	public KnockoutPhase generateThirdPlace() {
		long id = THIRD_PLACE_MATCH_ID;
		phase = THIRD_PLACE;
		
		List<String> sLosers = losers(semiFinal);
		thirdPlace = new Match(id, phase, sLosers.get(0), sLosers.get(1), FINAL_WINS);
		
		return this;
	}

	public KnockoutPhase generateFinal() {
		long id = FINAL_MATCH_ID;
		phase = FINAL;
		
		List<String> sWinners = winners(semiFinal);
		theFinal = new Match(id, phase, sWinners.get(0), sWinners.get(1), FINAL_WINS);
		
		return this;
	}

	List<String> winners(List<Match> matches) {
		return matches.stream().map(m -> m.winner()).collect(toList());
	}

	List<String> losers(List<Match> matches) {
		return matches.stream().map(m -> m.loser()).collect(toList());
	}

	//

	public MatchPhase getPhase() {
		return phase;
	}

	public void setPhase(MatchPhase phase) {
		this.phase = phase;
	}

	public Match getTheFinal() {
		return theFinal;
	}

	public void setTheFinal(Match theFinal) {
		this.theFinal = theFinal;
	}

	public Match getThirdPlace() {
		return thirdPlace;
	}
	
	public void setThirdPlace(Match thirdPlace) {
		this.thirdPlace = thirdPlace;
	}
	
	public List<Match> getSemiFinal() {
		return semiFinal;
	}

	public void setSemiFinal(List<Match> semiFinal) {
		this.semiFinal = semiFinal;
	}

	public List<Match> getQuarterFinal() {
		return quarterFinal;
	}

	public void setQuarterFinal(List<Match> quarterFinal) {
		this.quarterFinal = quarterFinal;
	}

}
