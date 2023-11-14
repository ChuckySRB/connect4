package com.mozzartbet.hackaton.connect4.bot.walama;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;
import static com.mozzartbet.hackaton.connect4.util.ThreadHelper.sleep;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Player;

public class Bot extends Player {

	public final static String filename = "src\\main\\java\\com\\mozzartbet\\hackaton\\connect4\\bot\\walama\\precomputed12.txt";
	//public final static String filename = "precomputed13smaller.txt";
	public final static boolean READ_FROM_FILE = true;
	
	private final static int param1 = 10;
	private final static int param2 = 1;
	private final static int param2opp = 1;
	private final static int param3opp = 1;
	private final static int param3 = 2;
	private final static double param4 = 1;
	
	private final static double minWinThreshold = 0.3;
	private final static double minLoseThreshold = 0.3;
	private final static double maxWinThreshold = 0.7;
	private final static double maxLoseThreshold = 0.7;
	private final static double diffThreshold = 0.2;
	
	
	
	long timeoutMillis;
	
	GameBoard trueGameBoard;
	GameBoard myGameBoard;
	int currentDepth;
	boolean firstMove;
	int playerId;
	int otherPlayerId;
	
	volatile HashMap<Long, Double> precomputed_prob;
	volatile HashMap<Long, Double> precomputed_min;
	volatile HashMap<Long, Double> precomputed_max;
	int maxPrecomputedDepth;
	
	
	public void setCurrentMove(int newMove) {
		move = newMove;
	}
	
	public int getCurrentMove() {
		return move;
	}
	
	@Override
	public void configure(long timeoutMillis) {
		// TODO Auto-generated method stub
		this.timeoutMillis = timeoutMillis;
		trueGameBoard = new GameBoard();
		myGameBoard = new GameBoard();
		
		move = 0;
		
		if (precomputed_prob == null) {
			getPrecomputed();
		}
		
		firstMove = true;
		currentDepth = 0;
		

	}

	private int getBestPrecomputed() {
		
		if (currentDepth > 15) {
			return -1;
		}
		
		myGameBoard = trueGameBoard.deepCopy();
		
		int maxColInd = 0;
		double maxVal = 0.0;		
		if (playerId == 2)
			maxVal = 1.0;
		
		double biggestMax = 0.0;
		int biggestMaxInd = 0;
		double smallestMin = 1.0;
		int smallestMinInd = 0;

		double smallestMax = 1.0;
		int smallestMaxInd = 0;
		double biggestMin = 0.0;
		int biggestMinInd = 0;
		
		ArrayList<Double> values = new ArrayList<Double>();
		
		for (int col = 0; col < GameConsts.COLUMNS; col ++) {
			
			if (myGameBoard.findDepth(col) == -1) {
				continue;
			}
			
			myGameBoard.placeCounter(col, playerId);
			long serialized = PreFunk.serializeBoardFaster(myGameBoard);
			myGameBoard.undoMove();
			
			double val = precomputed_prob.getOrDefault(serialized, 0.5);
			double val_min = precomputed_min.getOrDefault(serialized, 0.5);
			double val_max = precomputed_max.getOrDefault(serialized, 0.5);
			
			System.out.println("For col " + col + " got prob " + val + " min " + val_min + " max " + val_max);
			
			values.add(val);
			
			if ((val > maxVal && playerId == 1) || (val < maxVal && playerId == 2)) {
				maxColInd = col;
				maxVal = val;
			}
			
			if (biggestMax < val_max) {
				biggestMax = val_max;
				biggestMaxInd = col;
			}

			if (smallestMax > val_max) {
				smallestMax = val_max;
				smallestMaxInd = col;
			}

			if (smallestMin < val_min) {
				smallestMin = val_min;
				biggestMaxInd = col;
			}

			if (biggestMin > val_min) {
				biggestMin = val_min;
				biggestMinInd = col;
			}
			
		}
		
		boolean allsame = true;
		
		for (int i = 1; i < values.size(); i ++) {
			if (Math.abs(values.get(i-1) - values.get(i)) > 0.00000001) {
				allsame = false;
				System.out.println("not same for " + i + " " + (i-1) + " " + values.get(i-1) + " " + values.get(i));
			}
		}
		
		if (allsame) {	//check this also
			maxColInd = -1;
		}
		
		else {	// if not all same, check for min and max values

			if (playerId == 1) { // juri veci max da pobedins, veci min da je manja sansa da izgubis
				if (smallestMin < minLoseThreshold && biggestMin - smallestMin > diffThreshold) {
					//get away from his win !!!
					maxColInd = biggestMinInd;
					System.out.println("getting away from his win ");
					System.out.println("smallest min " + smallestMin);
					System.out.println("biggest min " + biggestMin);
					
				}
				if (biggestMax > maxWinThreshold) {
					//go for it !
					maxColInd = biggestMaxInd;
					System.out.println("going for it! ");
					System.out.println("biggest max " + biggestMax);
				}
			}
			else {// juri manji min da pobedins, manji max da je manja sansa da izgubis
				if (biggestMax > maxLoseThreshold && biggestMax - smallestMax > diffThreshold) {
					//get away from his win !!!
					maxColInd = smallestMaxInd;
					System.out.println("getting away from his win ");
					System.out.println("biggest max " + biggestMax);
					System.out.println("smallest max " + smallestMax);
				}
				if (smallestMin < minWinThreshold) {
					//go for it !
					maxColInd = smallestMinInd;
					System.out.println("going for it! ");
					System.out.println("smallest min " + smallestMin);
				}
			}
		}
		
		return maxColInd;
	}
	
	
	private int getLargestConnected(GameBoard board, int num2, int num3) {
		if (num3 >0 )
			return 3;
		if (num2 > 0)
			return 2;
		return 1;
	}
	
	private int getNumConnected(GameBoard board, int num, int player) {
		
		int rawBoard[][] = board.getBoard();
		
		int horizontal = 0;
		for (int row = 0 ; row < GameConsts.ROWS; row ++) {
			int connected = 0;
			for (int col = 0; col < GameConsts.COLUMNS; col ++) {
				if (rawBoard[row][col] == player) {
					connected ++;
					if (connected >= num) {
						horizontal ++;
					}
				}
				else {
					connected = 0;
				}
			}
		}
		
		int vertical = 0;
		for (int col = 0; col < GameConsts.COLUMNS; col ++) {
			int connected = 0;
			for (int row = 0 ; row < GameConsts.ROWS; row ++) {
				if (rawBoard[row][col] == player) {
					connected ++;
					if (connected >= num) {
						vertical ++;
					}
				}
				else {
					connected = 0;
				}
			}
		}
		
		int diagSE = 0;
		for (int row = 0 ; row < GameConsts.ROWS; row ++) {
			int connected = 0;
			int startRow = row;
			int startCol = 0;
			while(true) {
				if (rawBoard[startRow][startCol] == player) {
					connected ++;
					if (connected >= num) {
						diagSE ++;
					}
				}
				else {
					connected = 0;
				}
				
				startRow ++;
				startCol ++;
				
				if (startRow >= GameConsts.ROWS || startCol >= GameConsts.COLUMNS) {
					break;
				}
				
			}
		}
		for (int col = 1 ; col < GameConsts.COLUMNS; col ++) {
			int connected = 0;
			int startRow = 0;
			int startCol = col;
			while(true) {
				if (rawBoard[startRow][startCol] == player) {
					connected ++;
					if (connected >= num) {
						diagSE ++;
					}
				}
				else {
					connected = 0;
				}
				
				startRow ++;
				startCol ++;
				
				if (startRow >= GameConsts.ROWS || startCol >= GameConsts.COLUMNS) {
					break;
				}
				
			}
		}

		int diagSW = 0;
		for (int row = 0 ; row < GameConsts.ROWS; row ++) {
			int connected = 0;
			int startRow = row;
			int startCol = GameConsts.COLUMNS - 1;
			while(true) {
				if (rawBoard[startRow][startCol] == player) {
					connected ++;
					if (connected >= num) {
						diagSW ++;
					}
				}
				else {
					connected = 0;
				}
				
				startRow ++;
				startCol --;
				
				if (startRow >= GameConsts.ROWS || startCol < 0) {
					break;
				}
				
			}
		}
		for (int col = 0 ; col < GameConsts.COLUMNS - 1; col ++) {
			int connected = 0;
			int startRow = 0;
			int startCol = col;
			while(true) {
				if (rawBoard[startRow][startCol] == player) {
					connected ++;
					if (connected >= num) {
						diagSW ++;
					}
				}
				else {
					connected = 0;
				}
				
				startRow ++;
				startCol --;
				
				if (startRow >= GameConsts.ROWS || startCol < 0) {
					break;
				}
				
			}
		}
		
		return horizontal + vertical + diagSE + diagSW;
		
		//return 0;
	}
	
	private double getOpponentMaxScore(GameBoard board) {
		GameBoard testBoard = board.deepCopy();
		
		double maxScore = 0;
		for (int col = 0; col < GameConsts.COLUMNS; col ++) {
			
			if (testBoard.findDepth(col) == -1) {
				continue;
			}
			
			//System.out.println("mnjoh " + testBoard.isGameOver());
			
			testBoard.placeCounter(col, otherPlayerId);
			
			if (testBoard.isGameOver()) {	//yep, this is a win 
//				for (int i = 0 ; i < GameConsts.ROWS; i ++) {
//					for (int j = 0 ; j < GameConsts.COLUMNS; j ++) {
//						System.out.print(testBoard.getBoard()[i][j] + " ");
//					}
//					System.out.println();
//				}
//				System.out.println("noooo!, he'll win! :(");
				
				return 100000000.0;		
			}

			int feature2 = getNumConnected(testBoard, 2, otherPlayerId);
			int feature3 = getNumConnected(testBoard, 3, otherPlayerId);
			int feature1 = getLargestConnected(testBoard,feature2, feature3);
			
			double score = (feature1-2) * param1 + feature2 * param2opp + feature3 * param3opp ;
			//System.out.println("scoreee " + score);
			
			if (maxScore < score) {
				maxScore = score;
			}
			
			testBoard.undoMove();
		}
		return maxScore;
	}
	
	private int getBestConnected() {
		myGameBoard = trueGameBoard.deepCopy();

		double maxScore = -10000;	
		int maxInd = 0;
		for (int col = 0; col < GameConsts.COLUMNS; col ++) {
			
			if (myGameBoard.findDepth(col) == -1) {
				continue;
			}
			
			myGameBoard.placeCounter(col, playerId);
			
			if (myGameBoard.isGameOver()) {	//yep, this is a win 
				System.out.println("Hooray!, will win!");
				
				return col;				
			}

			int feature2 = getNumConnected(myGameBoard, 2, playerId);
			int feature3 = getNumConnected(myGameBoard, 3, playerId);
			int feature1 = getLargestConnected(myGameBoard,feature2, feature3);
			double feature4 = getOpponentMaxScore(myGameBoard);
			
			double score = (feature1-2) * param1 + feature2 * param2 + feature3 * param3 - feature4 * param4 ;
			
			if (maxScore < score) {
				maxScore =score;
				maxInd = col;
			}
			
			System.out.println("Score for " + col + " is " + score);
			System.out.println("feature1 " + feature1);
			System.out.println("feature2 " + feature2);
			System.out.println("feature3 " + feature3);
			System.out.println("feature4 " + feature4);
			
			myGameBoard.undoMove();
		}
		return maxInd;
		
	}
	
	@Override
	public void move() {
		// TODO Auto-generated method stub
		System.out.println("Ajde razmisljaj malo");
		
		if (firstMove) {
			firstMove = false;
			playerId = 1;
			otherPlayerId = 2;
			System.out.println("ja sam player 1");
		}
		
		//hack the first few moves, and steel time
		System.out.println("current depth is "+ currentDepth); // this is actually the move number
		if (currentDepth <= 1) {
			System.out.println("This move is hacked");
			this.move = 3;
			sleep(500);
		}
		
		else {
			//fetches the best precomputed move
			int nextMove = getBestPrecomputed();
			
			System.out.println("best precomputed " + nextMove);
			
			//if no precomputed moves, than do some magic
			if (nextMove == -1) {
				nextMove = getBestConnected();
				System.out.println("Not precomputed!");
			}

			this.move = nextMove;
		}
		
		System.out.println("cu igram " + move);
		
		//udpate gamestate
		trueGameBoard.placeCounter(this.move, playerId);
		currentDepth++;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		System.out.println("Dosta si razmisljao");

	}

	@Override
	public void opponentMove(int move) {
		// TODO Auto-generated method stub
		System.out.println("Drugar je igrao " + move);
		
		if (firstMove) {
			firstMove = false;
			playerId = 2;
			otherPlayerId = 1;
			System.out.println("ja sam player 2");
		}

		//update game state
		trueGameBoard.placeCounter(move, otherPlayerId);
		currentDepth++;

	}

	@Override
	public void finished(int winner) {
		// TODO Auto-generated method stub
		System.out.println("Yay");

	}
	
	private void getPrecomputed() {
		precomputed_prob = new HashMap<Long,Double>();
		precomputed_min = new HashMap<Long,Double>();
		precomputed_max = new HashMap<Long,Double>();
		
		Thread thread = new Thread(new Funk(this));
		thread.start();
		
	}

}
