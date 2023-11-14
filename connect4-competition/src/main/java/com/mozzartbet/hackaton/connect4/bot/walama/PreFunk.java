package com.mozzartbet.hackaton.connect4.bot.walama;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.LineNumberInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.management.RuntimeErrorException;
import javax.xml.crypto.dsig.keyinfo.KeyValue;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.GameConsts;

public class PreFunk {
	
	private final static int MAX_DEPTH = 13;
	private final static int MAX_DEPTH_PRINT = 3;
	private final static boolean PRINT_INSTANT = false;
	//private final static boolean PRINT_SEGMENTED = true;
	
	public static void serializeBoardFaster(GameBoard board, long serialized, long serializedInv) {
		int[][] rawBoard = board.getBoard();
		
		serialized = 0;
			
		for (int col = 0 ; col < GameConsts.COLUMNS; col ++) {
			int row = GameConsts.ROWS-1;
			
			while (row >= 0) {
				if (rawBoard[row][col] == 0) {
					break;
				}
				
				serialized <<= 2;
				serialized += rawBoard[row][col];
				if (serialized < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
				
				row --;
			}
			if (row >= 0) {
				serialized <<= 2;
				if (serialized < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
			}			
		}
		
		serializedInv = 0;
		
		for (int col = GameConsts.COLUMNS - 1 ; col >= 0; col --) {
			int row = GameConsts.ROWS-1;
			
			while (row >= 0) {
				if (rawBoard[row][col] == 0) {
					break;
				}
				
				serializedInv <<= 2;
				serializedInv += rawBoard[row][col];
				if (serializedInv < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
				
				row --;
			}
			if (row >= 0) {
				serializedInv <<= 2;
				if (serializedInv < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
			}			
		}
		
	}	
	
	public static long serializeBoardFaster(GameBoard board) {
		int[][] rawBoard = board.getBoard();
		
		long serialized = 0;
		
		for (int col = 0 ; col < GameConsts.COLUMNS; col ++) {
			int row = GameConsts.ROWS-1;
			
			while (row >= 0) {
				if (rawBoard[row][col] == 0) {
					break;
				}
				serialized <<= 2;
				serialized += rawBoard[row][col];
				if (serialized < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
				row --;
			}
			if (row >= 0) {
				serialized <<= 2;
				if (serialized < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
			}
			
		}
		
		return serialized;
	}
	
	public static long serializeBoardInvFaster(GameBoard board) {
		int[][] rawBoard = board.getBoard();
		
		long serialized = 0;
		
		for (int col = GameConsts.COLUMNS - 1 ; col >= 0; col --) {
			int row = GameConsts.ROWS-1;
			
			while (row >= 0) {
				if (rawBoard[row][col] == 0) {
					break;
				}
				serialized <<= 2;
				serialized += rawBoard[row][col];
				if (serialized < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
				row --;
			}
			if (row >= 0) {
				serialized <<= 2;
				if (serialized < 0) {
					System.out.println("NE RADI TI KAKO TREBA;");
				}
			}
			
		}
		
		return serialized;
	}
	
	
	public static void serializeBoard(GameBoard board, String serialized, String serializedInv) {
		int[][] rawBoard = board.getBoard();
		
		
		String[] boardStrings = new String[GameConsts.COLUMNS];
		
		for (int col = 0 ; col < GameConsts.COLUMNS; col ++) {
			StringBuilder sb = new StringBuilder();
			int row = GameConsts.ROWS-1;
			
			while (row >= 0) {
				if (rawBoard[row][col] == 0) {
					break;
				}
				sb.append(rawBoard[row][col]);
				row --;
			}
			if (row >= 0) {
				sb.append("0");
			}
			
			boardStrings[col] = sb.toString();
			
		}
		
		StringBuilder sb = new StringBuilder();
		for (int col = 0 ; col < GameConsts.COLUMNS; col ++) {
			sb.append(boardStrings[col]);
		}
		serialized = sb.toString();
		
		StringBuilder sbInv = new StringBuilder();
		for (int col = GameConsts.COLUMNS - 1 ; col >= 0; col --) {
			sbInv.append(boardStrings[col]);
		}
		serializedInv = sbInv.toString();
		
	}	
	
	public static String serializeBoard(GameBoard board) {
		int[][] rawBoard = board.getBoard();
		
		StringBuilder sb = new StringBuilder();
		
		for (int col = 0 ; col < GameConsts.COLUMNS; col ++) {
			int row = GameConsts.ROWS-1;
			
			while (row >= 0) {
				if (rawBoard[row][col] == 0) {
					break;
				}
				sb.append(rawBoard[row][col]);
				row --;
			}
			if (row >= 0) {
				sb.append("0");
			}
			
		}
		
		return sb.toString();
	}
	
	
	private static class Board {
		
		public HashMap<Long,Double> probs;
		public ArrayList<HashMap<Long,Double>> hashMapByLevels;
		public HashMap<Long,Double> probs_min;
		public ArrayList<HashMap<Long,Double>> hashMapByLevels_min;
		public HashMap<Long,Double> probs_max;
		public ArrayList<HashMap<Long,Double>> hashMapByLevels_max;
		
		public int rows;
		public int columns;
		public GameBoard table;
		
		public int maxDepth;
		NodeState nodeStates[];
		int currentDepth;
		
		PrintWriter out;

		public void initTable() {
			table = new GameBoard();
			probs = new HashMap<Long, Double>();
			probs_min = new HashMap<Long, Double>();
			probs_max = new HashMap<Long, Double>();
			
			if (PRINT_INSTANT) {
				try {
					out = new PrintWriter(Bot.filename);
					out.print(MAX_DEPTH + " ");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
		}
		
		public void initProbLoop(int maxDepth) {

			this.maxDepth = maxDepth;
			nodeStates = new NodeState[maxDepth];
			for (int i = 0 ; i < maxDepth; i ++) {
				nodeStates[i] = new NodeState();
			}
			currentDepth = 0;
			hashMapByLevels = new ArrayList<HashMap<Long, Double>>();
			for (int i = 0 ; i < maxDepth; i ++) {
				hashMapByLevels.add(new HashMap<Long,Double>());
			}
			hashMapByLevels_min = new ArrayList<HashMap<Long, Double>>();
			for (int i = 0 ; i < maxDepth; i ++) {
				hashMapByLevels_min.add(new HashMap<Long,Double>());
			}
			hashMapByLevels_max = new ArrayList<HashMap<Long, Double>>();
			for (int i = 0 ; i < maxDepth; i ++) {
				hashMapByLevels_max.add(new HashMap<Long,Double>());
			}
		}
		
		private class NodeState {
			int numValidDescendants;
			double probabilitySum;
			double minProbability;
			double maxProbability;
			int currentCounter;
			
			public void reset() {
				numValidDescendants = 0;
				probabilitySum = 0;
				currentCounter = 0;
				minProbability = 1;
				maxProbability = 0;
			}
		}
		
		private void goUp(
				double meanProbability,
				double minProbability,
				double maxProbability) {
			
			currentDepth --;
			
			if (currentDepth >= 0) {
//				if (nodeStates[currentDepth].minProbability > minProbability) {
//					nodeStates[currentDepth].minProbability = minProbability;
//				}
//				if (nodeStates[currentDepth].maxProbability < maxProbability) {
//					nodeStates[currentDepth].maxProbability = maxProbability;
//				}
				if (nodeStates[currentDepth].minProbability > meanProbability) {
					nodeStates[currentDepth].minProbability = meanProbability;
				}
				if (nodeStates[currentDepth].maxProbability < meanProbability) {
					nodeStates[currentDepth].maxProbability = meanProbability;
				}
				nodeStates[currentDepth].probabilitySum += meanProbability;
				nodeStates[currentDepth].numValidDescendants ++;
			}
		}
		
		private void goDown() {
			
		}
		
		private void putInMap(
				double meanProbability,
				double minProbability,
				double maxProbability) {
			long serialized = serializeBoardFaster(table);
			long serializedInv = serializeBoardInvFaster(table);
			
			
			if (PRINT_INSTANT) {
				out.print(serialized + " " + meanProbability + " ");
				out.print(minProbability + " ");
				out.print(maxProbability + " ");
			}
			else {
				probs.put(serialized, meanProbability);
				probs.put(serializedInv, meanProbability);
				
				hashMapByLevels.get(currentDepth).put(serialized, meanProbability);
				hashMapByLevels.get(currentDepth).put(serializedInv, meanProbability);
				
				probs_min.put(serialized, minProbability);
				probs_min.put(serializedInv, minProbability);
				
				hashMapByLevels_min.get(currentDepth).put(serialized, minProbability);
				hashMapByLevels_min.get(currentDepth).put(serializedInv, minProbability);
				
				probs_max.put(serialized, maxProbability);
				probs_max.put(serializedInv, maxProbability);
				
				hashMapByLevels_max.get(currentDepth).put(serialized, maxProbability);
				hashMapByLevels_max.get(currentDepth).put(serializedInv, maxProbability);
			}			
		}
		
		public void calcProb() {
			
			
			
			while(currentDepth >= 0) {
				// warning too much output
				//System.out.println("Beginning of beautiful friendship.");
				
				int currPlayer = currentDepth % 2 + 1;
				
				while(
						nodeStates[currentDepth].currentCounter < columns &&
						table.findDepth(nodeStates[currentDepth].currentCounter) == -1) {
					if (currentDepth <= MAX_DEPTH_PRINT) {
						System.out.println(
								"Increasing counter for depth " + 
								currentDepth + 
								" " + 
								nodeStates[currentDepth].currentCounter);
					}
					
//					if (currentDepth == 0) {
//						try {
//							
//							System.out.println("Writing to file");
//							PrintWriter out = new PrintWriter(Bot.filename + nodeStates[currentDepth].currentCounter);
//							
//							out.print(MAX_DEPTH + " ");
//							
//							//first get maps for first few moves
//							int cnt = 0;
//							for (int i = 0; i < hashMapByLevels.size(); i ++) {
//								HashMap<Long, Double> mapa = hashMapByLevels.get(i);
//								HashMap<Long, Double> mapa_min = hashMapByLevels_min.get(i);
//								HashMap<Long, Double> mapa_max = hashMapByLevels_max.get(i);
//								
//								for (long key : mapa.keySet()) { 
//									out.print(key + " " + mapa.get(key) + " ");
//									out.print(mapa_min.get(key) + " ");
//									out.print(mapa_max.get(key) + " ");
//								}
//								System.out.println("Finished hashmap level " + cnt++);
//							}
//							
//							out.close();
//							
//							probs.clear();
//							probs_min.clear();
//							probs_max.clear();
//							
//							for (int i = 0; i < hashMapByLevels.size(); i ++) {
//								hashMapByLevels.get(i).clear();
//								hashMapByLevels_min.get(i).clear();
//								hashMapByLevels_max.get(i).clear();
//							}
//							
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
					nodeStates[currentDepth].currentCounter ++;	
				}
				
				if (nodeStates[currentDepth].currentCounter == columns) { //this node is finished! go up.

					double meanProbability = 
							nodeStates[currentDepth].probabilitySum /
							nodeStates[currentDepth].numValidDescendants;
					
					double minProbability = nodeStates[currentDepth].minProbability;
					double maxProbability = nodeStates[currentDepth].maxProbability;
					
					if (meanProbability > 0.4 && meanProbability < 0.6) {
						putInMap(meanProbability, minProbability, maxProbability);
					}
					table.undoMove();
					
					goUp(meanProbability, minProbability, maxProbability);
				}

				else {	// go to depth on this col
					
					if (currentDepth <= MAX_DEPTH_PRINT) {
						System.out.println(
								"Increasing counter for depth " + 
								currentDepth + 
								" " + 
								nodeStates[currentDepth].currentCounter);
					}
					
//					if (currentDepth == 0) {
//						try {
//							
//							System.out.println("Writing to file");
//							PrintWriter out = new PrintWriter(Bot.filename + nodeStates[currentDepth].currentCounter);
//							
//							out.print(MAX_DEPTH + " ");
//							
//							//first get maps for first few moves
//							int cnt = 0;
//							for (int i = 0; i < hashMapByLevels.size(); i ++) {
//								HashMap<Long, Double> mapa = hashMapByLevels.get(i);
//								HashMap<Long, Double> mapa_min = hashMapByLevels_min.get(i);
//								HashMap<Long, Double> mapa_max = hashMapByLevels_max.get(i);
//								
//								for (long key : mapa.keySet()) { 
//									out.print(key + " " + mapa.get(key) + " ");
//									out.print(mapa_min.get(key) + " ");
//									out.print(mapa_max.get(key) + " ");
//								}
//								System.out.println("Finished hashmap level " + cnt++);
//							}
//							
//							out.close();
//							
//							probs.clear();
//							probs_min.clear();
//							probs_max.clear();
//							
//							for (int i = 0; i < hashMapByLevels.size(); i ++) {
//								hashMapByLevels.get(i).clear();
//								hashMapByLevels_min.get(i).clear();
//								hashMapByLevels_max.get(i).clear();
//							}
//							
//						} catch (FileNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					
					
					//first increase the counter!
					int currentCol = nodeStates[currentDepth].currentCounter;
					nodeStates[currentDepth].currentCounter ++;
					
					//check if maxdepth reached
					if (currentDepth == maxDepth-1) {	//go up again
						double meanProbability = 0.5;
						double minProbability = 0.5;
						double maxProbability = 0.5;
						
						currentDepth ++;
						goUp(meanProbability, minProbability, maxProbability);
						
						continue;
					}

					//place coin and go down
					currentDepth ++;
					table.placeCounter(currentCol, currPlayer);
					nodeStates[currentDepth].reset();
					
					//check if already was this state
					long key = serializeBoardFaster(table);
					if (probs.containsKey(key)) {
						//warning, too much output
						//System.out.println("Hit!");
						
						double meanProbability = probs.get(key);
						double minProbability = probs_min.get(key);
						double maxProbability = probs_max.get(key);
						
						table.undoMove();
						
						goUp(meanProbability, minProbability, maxProbability);
					}
					
					//check if win
					if (table.isGameOver()) {	//go up again
						double meanProbability = 0;
						double minProbability = 0;
						double maxProbability = 0;
						if (table.getWinner() == 1) {
							meanProbability = minProbability = maxProbability = 1;
						}
						if (table.getWinner() == 2) {
							meanProbability = minProbability = maxProbability = 0;
						}
						
						putInMap(meanProbability, minProbability, maxProbability);
						table.undoMove();
						
						goUp(meanProbability, minProbability, maxProbability);
						
						
						continue;
					}
					
				}
				
			}
			
			if (PRINT_INSTANT) {
				out.close();
			}
			
		}
	}
	
	public static void main(String[] args) {
		
		System.out.println("Its aliiiiive.\nLet's do the precompute.");
		
		
		Board board = new Board();
		
		board.rows = GameConsts.ROWS;
		board.columns = GameConsts.COLUMNS;
		
		board.initTable();
		board.initProbLoop(MAX_DEPTH);
		
		board.calcProb();
		
		int cntNotEven = 0;
		int cntBetter = 0;
		int cntWorse = 0;
		int cntEven = 0;
		for (long key : board.probs.keySet()) {
			if (board.probs.get(key) == 0.5) {
				cntEven ++;
			}
			else {
				cntNotEven ++;
				if (board.probs.get(key) < 0.5) {
					cntWorse ++;
				}
				else {
					cntBetter ++;
				}
			}
		}
		
		System.out.println("even " + cntEven);
		System.out.println("evennot " + cntNotEven);
		System.out.println("even worse " + cntWorse);
		System.out.println("even better " + cntBetter);
		
		if (!PRINT_INSTANT) {
			try {
				
				System.out.println("Writing to file");
				PrintWriter out = new PrintWriter(Bot.filename);
				
				out.print(MAX_DEPTH + " ");
				
				//first get maps for first few moves
				int cnt = 0;
				for (int i = 0; i < board.hashMapByLevels.size(); i ++) {
					HashMap<Long, Double> mapa = board.hashMapByLevels.get(i);
					HashMap<Long, Double> mapa_min = board.hashMapByLevels_min.get(i);
					HashMap<Long, Double> mapa_max = board.hashMapByLevels_max.get(i);
					
					for (long key : mapa.keySet()) { 
						out.print(key + " " + mapa.get(key) + " ");
						out.print(mapa_min.get(key) + " ");
						out.print(mapa_max.get(key) + " ");
					}
					System.out.println("Finished hashmap level " + cnt++);
				}
				
				out.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("ENDE");
				
	}
	
	
}
