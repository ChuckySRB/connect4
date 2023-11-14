package com.mozzartbet.hackaton.connect4.bot.nikola_jovanovic;

import com.mozzartbet.hackaton.connect4.model.Player;
import com.mozzartbet.hackaton.connect4.model.GameConsts;

import java.util.LinkedList;
import java.util.Random;

import com.mozzartbet.hackaton.connect4.model.Direction;

public class Bot extends Player {

	Random rnd = new Random();
	LinkedList<TreeNode> currList, nextLvlList;
	TreeNode root;
	float timeout, moveTime;
	int[][] board;
	int playerInd;
	
	@Override
	public void configure(long timeoutMillis) {
		// TODO Auto-generated method stub
		board = new int[GameConsts.ROWS][GameConsts.COLUMNS];
		for (int i = 0; i < GameConsts.ROWS; i++) {
			for (int j = 0; j < GameConsts.COLUMNS; j++) {
				board[i][j] = -1;
			}
		}
		timeout = timeoutMillis;
		playerInd = -1;
		System.out.format("timeout: %f%n", timeout);
	}

	@Override
	public void move() {
		// TODO Auto-generated method stub
		if (playerInd == -1) playerInd = 0;
		
		System.out.println();
		System.out.println();
		System.out.println();
		moveTime = System.currentTimeMillis();
		int [][]pBoard = board.clone();
		move = -1;
		float maxConn = 0;
		for (int col = 0; col < GameConsts.COLUMNS; col++) {
			int row = findTopPos(col);
			if (row == GameConsts.ROWS - 1) continue;
			if (move == -1) move = col;
			else {
				pBoard[row + 1][col] = playerInd;
				
				if (checkWin(playerInd, pBoard)) {
					System.out.format(boardString(pBoard));
					System.out.format("%d%n", col);
					System.out.println("Wining move!");
					move = col;
					return;
				}
				float plSum = getSumForPlayer(pBoard, playerInd);
				float opSum = getSumForPlayer(pBoard, (playerInd + 1) % 2);
				if (plSum + opSum != 0){
					if (plSum / (plSum + opSum) > maxConn || plSum / (opSum + opSum) > maxConn) {
						System.out.format("col: %d %f %f MaxConn: %f CurrConn: %f%n", col, plSum, opSum, maxConn, plSum / (plSum + opSum));
						maxConn = plSum / (plSum + opSum);
						move = col;
					}
				}
				/*if (plSum + opSum > maxConn) {
					//System.out.format("col: %d %d %d MaxConn: %f CurrConn: %d%n", col, plSum, opSum, maxConn, plSum + opSum);
					maxConn = plSum + opSum;
					move = col;
				}*/
				
				pBoard[row + 1][col] = -1;
			}
		}
		
		currList = new LinkedList<>();
		nextLvlList = new LinkedList<>();
		root = new TreeNode(board, -1);
		currList.addLast(root);
		generateTree();
		root.propageteKoefs();
		if (root.sons.size() != 0)
		{
			if (root.koef == 0) System.out.println("Izgubio sam!");
			TreeNode maxKoefNode = root.sons.getFirst();
			for (TreeNode curr : root.sons) {
				System.out.println(curr.koef + " " + curr.prevMove);
				if (maxKoefNode.koef < curr.koef) maxKoefNode = curr;
				if (maxKoefNode.koef == curr.koef) if (rnd.nextDouble() > 0.3) maxKoefNode = curr; 
			}
			System.out.println("Updated move");
			move = maxKoefNode.prevMove;
		} else System.out.println("empty root");
		
		tableInsert(move, playerInd);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		tableInsert(move, playerInd);
	}

	@Override
	public void opponentMove(int move) {
		// TODO Auto-generated method stub
		if (playerInd == -1) playerInd = 1;
		
		if (!tableInsert(move, (playerInd + 1) % 2)) System.out.println("Wrong opponent move!");
	}

	@Override
	public void finished(int winner) {
		// TODO Auto-generated method stub
		System.out.println(boardString(board));
	}
	
	private void generateTree() {
		int co = 0;
		while (co++ < 3) {//System.currentTimeMillis() < moveTime + timeout * 0.) {
			while (!currList.isEmpty()) {
				TreeNode curr = currList.removeFirst();
				
				for (int colP = 0; colP < GameConsts.COLUMNS; colP++) {
					int rowP = findTopPos(colP);
					if (rowP == GameConsts.ROWS - 1) continue;
					boolean exists = false;
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							if (i != 0 && j != 0) {
								try {
									if (curr.board[rowP + i][colP + j] != -1) exists = true;
								} catch (Exception e) {
									
								}
							}
						}
					}
					if (!exists) continue;
					curr.board[rowP + 1][colP] = playerInd;

					float sumP = getSumForPlayer(curr.board, playerInd);
					float sumO = getSumForPlayer(curr.board, (playerInd + 1) % 2);
					if (checkWin(playerInd, curr.board)) curr.koef = 1;
					//else if (sumP + sumO == 0) curr.koef = 0;
					else {
						try {
							curr.koef = sumP / (sumP + sumO);	
						} catch (Exception e) {
							System.out.println(colP + "%n" + boardString(curr.board));
							curr.koef = 0.01f;
						}
						
						for (int colO = 0; colO < GameConsts.COLUMNS; colO++) {
							int rowO = findTopPos(colO);
							if (rowO == GameConsts.ROWS - 1) continue;
							curr.board[rowO + 1][colO] = (playerInd + 1) % 2;
							
							if (checkWin((playerInd + 1) % 2, curr.board)) {
								//System.out.println(boardString(curr.board) + "asd");
								//curr.koef = 0;
								for (int i = 0; i < curr.sons.size() && nextLvlList.getLast().prevMove == colP; i++) {
									nextLvlList.removeLast();
									curr.sons.removeLast();
								}
								curr.board[rowO + 1][colO] = -1;
								break;
							} else {
								//if (rowP > 1) System.out.println(boardString(curr.board) + "qwe");
								TreeNode newNode = new TreeNode(board.clone(), colP);
								nextLvlList.addLast(newNode);
								curr.sons.addLast(newNode);	
							}

							curr.board[rowO + 1][colO] = -1;
						}
					}
					
					curr.board[rowP + 1][colP] = -1;
				}
				
			}
			currList = nextLvlList;
			nextLvlList = new LinkedList<>();
		}
	}
	
	private float getSumForPlayer(int [][]board, int id) {
		float sum = 0;
		for (int col = 0; col < GameConsts.COLUMNS; col++) {
			int  row = findTopPos(col);
			if (row == -1) continue;
			
			sum += 1;
			final float div = 2.5f;
			final int toAdd = 20;
			
			float conn = countConnected(row - 1, col, Direction.S, id, board); 
			if (conn == 3) sum += toAdd;
			if (countConnectedAvail(row - 1, col, Direction.S, id, board) < 3) conn /= div;
			sum += conn;
			
			conn = countConnected(row, col + 1, Direction.E, id, board) + countConnected(row, col - 1, Direction.W, id, board);
			if (conn == 3) sum += toAdd;
			if (countConnectedAvail(row, col + 1, Direction.E, id, board) + countConnectedAvail(row, col - 1, Direction.W, id, board) < 3) conn /= div;
			sum += conn;
			
			conn = countConnected(row - 1, col + 1, Direction.SE, id, board) + countConnected(row - 1, col - 1, Direction.SW, id, board);
			if (conn == 3) sum += toAdd;
			if (countConnectedAvail(row - 1, col + 1, Direction.SE, id, board) + countConnectedAvail(row - 1, col - 1, Direction.SW, id, board) < 3) conn /= div;
			sum += conn;
			
			conn = countConnected(row + 1, col + 1, Direction.NE, id, board) + countConnected(row + 1, col - 1, Direction.NW, id, board);
			if (conn == 3) sum += toAdd;
			if (countConnectedAvail(row + 1, col + 1, Direction.NE, id, board) + countConnectedAvail(row + 1, col - 1, Direction.NW, id, board) < 3) conn /= div;
			sum += conn;
		}
		return sum;
	}
	
	private boolean tableInsert(int move, int ID) {
		int i = 0;
		while (i < GameConsts.ROWS && board[i][move] != -1) i++;
		if (i < GameConsts.ROWS) {
			board[i][move] = ID;
			return true;
		}
		return false;
	}

	private boolean checkWin(int counter, int [][]board) {
		int maxCount = GameConsts.IN_A_ROW - 1;
		
		for (int col = 0; col < GameConsts.COLUMNS; col++) {
		
			int row = findTopPos(col);
			if (row == -1 || board[row][col] != counter) continue;

			int count = countConnected(row - 1, col, Direction.S, counter, board);
			if (count >= maxCount) {
				return true;
			}
	
			count = countConnected(row, col + 1, Direction.E, counter, board)
					+ countConnected(row, col - 1, Direction.W, counter, board);
			if (count >= maxCount) {
				return true;
			}
	
			count = countConnected(row + 1, col + 1, Direction.NE, counter, board)
					+ countConnected(row - 1, col - 1, Direction.SW, counter, board);
			if (count >= maxCount) {
				return true;
			}
	
			count = countConnected(row + 1, col - 1, Direction.NW, counter, board)
					+ countConnected(row - 1, col + 1, Direction.SE, counter, board);
			if (count >= maxCount) {
				return true;
			}	
		}

		return false;
	}
	
	private int countConnected(int row, int col, Direction dir, int counter, int [][]board) {
		if (row < GameConsts.ROWS && row > -1 && col < GameConsts.COLUMNS && col > -1
				&& board[row][col] == counter) {
			switch (dir) {
			case N:
				return 1 + countConnected(row + 1, col, dir, counter, board);
			case S:
				return 1 + countConnected(row - 1, col, dir, counter, board);
			case E:
				return 1 + countConnected(row, col + 1, dir, counter, board);
			case W:
				return 1 + countConnected(row, col - 1, dir, counter, board);
			case NE:
				return 1 + countConnected(row + 1, col + 1, dir, counter, board);
			case NW:
				return 1 + countConnected(row + 1, col - 1, dir, counter, board);
			case SE:
				return 1 + countConnected(row - 1, col + 1, dir, counter, board);
			case SW:
				return 1 + countConnected(row - 1, col - 1, dir, counter, board);
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	private int countConnectedAvail(int row, int col, Direction dir, int counter, int [][]board) {
		if (row < GameConsts.ROWS && row > -1 && col < GameConsts.COLUMNS && col > -1
				&& (board[row][col] == counter || board[row][col] == -1)) {
			switch (dir) {
			case N:
				return 1 + countConnectedAvail(row + 1, col, dir, counter, board);
			case S:
				return 1 + countConnectedAvail(row - 1, col, dir, counter, board);
			case E:
				return 1 + countConnectedAvail(row, col + 1, dir, counter, board);
			case W:
				return 1 + countConnectedAvail(row, col - 1, dir, counter, board);
			case NE:
				return 1 + countConnectedAvail(row + 1, col + 1, dir, counter, board);
			case NW:
				return 1 + countConnectedAvail(row + 1, col - 1, dir, counter, board);
			case SE:
				return 1 + countConnectedAvail(row - 1, col + 1, dir, counter, board);
			case SW:
				return 1 + countConnectedAvail(row - 1, col - 1, dir, counter, board);
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	private int findTopPos(int col) {
		for (int top = 0; top < GameConsts.ROWS; top++) if (board[top][col] == -1) return top - 1;
		return GameConsts.ROWS - 1;
	}
	
	public String boardString(int [][]board) {
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append("   ");
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			sb.append(i).append(" ");
		}
		sb.append("\n");

		sb.append("   ");
		for (int i = 0; i < GameConsts.COLUMNS; i++) {
			sb.append("--");
		}
		sb.append("\n");

		for (int i = GameConsts.ROWS - 1; i >= 0 ; i--) {
			sb.append(Integer.toString(i)).append("| ");
			for (int j = 0; j < GameConsts.COLUMNS; j++) {
				sb.append(Integer.toString(board[i][j] + 1)).append(" ");
			}
			sb.append("\n");
		}

		return sb.toString();
	}
	
}
