package com.mozzartbet.hackaton.connect4.bot.petja;

import static com.mozzartbet.hackaton.connect4.model.Direction.E;
import static com.mozzartbet.hackaton.connect4.model.Direction.NE;
import static com.mozzartbet.hackaton.connect4.model.Direction.NW;
import static com.mozzartbet.hackaton.connect4.model.Direction.S;
import static com.mozzartbet.hackaton.connect4.model.Direction.SE;
import static com.mozzartbet.hackaton.connect4.model.Direction.SW;
import static com.mozzartbet.hackaton.connect4.model.Direction.W;
import static com.mozzartbet.hackaton.connect4.model.GameConsts.IN_A_ROW;

import java.awt.Point;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import javax.tools.DocumentationTool.DocumentationTask;

import com.mozzartbet.hackaton.connect4.model.Direction;
import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Move;

public class Brain {
	protected final int maxRows = GameConsts.ROWS;
	protected final int maxCols = GameConsts.COLUMNS;

	MyBoard board;

	private MyMove attack;
	private MyMove defence;
	public ArrayList<Integer> doNot;

	public Brain(MyBoard board) {
		this.board = board;
		doNot = new ArrayList<>();
	}

	public MyMove getAttack() {
		return attack;
	}

	public MyMove getDefence() {
		return defence;
	}

	public boolean Calculate() {
		suggested = 10;
		defence = null;
		attack = null;
		doNot.clear();
		for (Point point : board.getHisMoves()) {
			getSuggestedDefence(2, point.x, point.y);
		}
		for (Point point : board.getHisMoves()) {
			getDoNots(2, point.x, point.y);
		}
		for (Point point : board.getMyMoves()) {
			getSuggestedAttack(1, point.x, point.y);
		}
		if (defence == null && attack == null)
			return false;
		else
			return true;
	}

	int suggested;

	private void getSuggestedDefence(int counter, int i, int j) {
		int maxCount = 0;
		if (defence != null)
			maxCount = defence.getLevel();
		int row = i;
		int col = j;

		int count = countConnected(row + 1, col, S, counter);
		if (count >= maxCount) {
			maxCount = count;
			int x = row;
			while (x > 0 && board.getBoard()[x][col] == counter) {
				x--;
			}
			if (row > 0 && board.getBoard()[x][col] == 0)
				defence = new MyMove(col, maxCount);
		}

		count = countConnected(row, col + 1, E, counter) + countConnected(row, col - 1, W, counter);
		if (count >= maxCount) {
			maxCount = count;
			int y = col;
			while (y > 0 && board.getBoard()[row][y] == counter) {
				y--;
			}
			if (board.getBoard()[row][y] == 0 && findDepth(y) == row)
				defence = new MyMove(y, maxCount);
			y = col;
			while (y < 7 && board.getBoard()[row][y] == counter) {
				y++;
			}
			if (board.getBoard()[row][y] == 0 && findDepth(y) == row)
				defence = new MyMove(y, maxCount);
		}

		count = countConnected(row - 1, col + 1, NE, counter) + countConnected(row + 1, col - 1, SW, counter);
		if (count >= maxCount) {
			maxCount = count;
			int x = row;
			int y = col;
			while (x > 0 && y < 7 && board.getBoard()[x][y] == counter) {
				x--;
				y++;
			}
			if (board.getBoard()[x][y] == 0 && findDepth(y) == x)
				defence = new MyMove(y, maxCount);
			x = row;
			y = col;
			while (x < 5 && y > 0 && board.getBoard()[x][y] == counter) {
				x++;
				y--;
			}
			if (board.getBoard()[x][y] == 0 && findDepth(y) == x)
				defence = new MyMove(y, maxCount);
		}

		count = countConnected(row - 1, col - 1, NW, counter) + countConnected(row + 1, col + 1, SE, counter);
		if (count >= maxCount) {
			maxCount = count;
			int x = row;
			int y = col;
			while (x > 0 && y > 0 && board.getBoard()[x][y] == counter) {
				x--;
				y--;
			}
			if (board.getBoard()[x][y] == 0 && findDepth(y) == x) {
				defence = new MyMove(y, maxCount);
				if (Bot.i == 1000)
					System.out.println("found it");
			}
			x = row;
			y = col;
			while (x < 5 && y < 7 && board.getBoard()[x][y] == counter) {
				x++;
				y++;
			}
			if (board.getBoard()[x][y] == 0 && findDepth(y) == x)
				defence = new MyMove(y, maxCount);
		}
	}

	private void getSuggestedAttack(int counter, int i, int j) {
		int maxCount = 0;
		if (attack != null)
			maxCount = attack.getLevel();
		int row = i;
		int col = j;

		int count = countConnected(row + 1, col, S, counter);
		if (count >= maxCount) {
			maxCount = count;
			int x = row;
			while (x > 0 && board.getBoard()[x][col] == counter) {
				x--;
			}
			if (row > 0 && board.getBoard()[x][col] == 0)
				attack = new MyMove(col, maxCount);
		}

		count = countConnected(row, col + 1, E, counter) + countConnected(row, col - 1, W, counter);
		if (count >= maxCount) {
			maxCount = count;
			int y = col;
			while (y > 0 && board.getBoard()[row][y] == counter) {
				y--;
			}
			if (findDepth(y) == row) {
			
					attack = new MyMove(y, maxCount);
				
			}
			y = col;
			while (y < 7 && board.getBoard()[row][y] == counter) {
				y++;
			}
			if (findDepth(y) == row) {
				
					attack = new MyMove(y, maxCount);
				
			}
		}

		count = countConnected(row - 1, col + 1, NE, counter) + countConnected(row + 1, col - 1, SW, counter);
		if (count >= maxCount) {
			maxCount = count;
			int x = row;
			int y = col;
			while (x > 0 && y < 7 && board.getBoard()[x][y] == counter) {
				x--;
				y++;
			}
			if (findDepth(y) == x) {
			
					attack = new MyMove(y, maxCount);
				
			}
			x = row;
			y = col;
			while (x < 5 && y > 0 && board.getBoard()[x][y] == counter) {
				x++;
				y--;
			}
			if (findDepth(y) == x) {
				
					attack = new MyMove(y, maxCount);
				
			}
		}

		count = countConnected(row - 1, col - 1, NW, counter) + countConnected(row + 1, col + 1, SE, counter);
		if (count >= maxCount) {
			maxCount = count;
			int x = row;
			int y = col;
			while (x > 0 && y > 0 && board.getBoard()[x][y] == counter) {
				x--;
				y--;
			}
			if (findDepth(y) == x) {
				
					attack = new MyMove(y, maxCount);
				
			}
			x = row;
			y = col;
			while (x < 5 && y < 7 && board.getBoard()[x][y] == counter) {
				x++;
				y++;
			}
			if (findDepth(y) == x) {
			
					attack = new MyMove(y, maxCount);
				
			}
		}
	}

	private void getDoNots(int counter, int i, int j) {
		int row = i;
		int col = j;

		int count = countConnected(row, col + 1, E, counter) + countConnected(row, col - 1, W, counter);
		if (count == 2) {
			int y = col;
			while (y > 0 && board.getBoard()[row][y] == counter) {
				y--;
			}
			if (findDepth(y) == (row + 1))
				doNot.add(y);
			y = col;
			while (y < 7 && board.getBoard()[row][y] == counter) {
				y++;
			}
			if (findDepth(y) == (row + 1))
				doNot.add(y);
		}

		count = countConnected(row - 1, col + 1, NE, counter) + countConnected(row + 1, col - 1, SW, counter);
		if (count == 2) {
			int x = row;
			int y = col;
			while (x > 0 && y < 7 && board.getBoard()[x][y] == counter) {
				x--;
				y++;
			}
			if (findDepth(y) == (row + 1))
				doNot.add(y);
			x = row;
			y = col;
			while (x < 5 && y > 0 && board.getBoard()[x][y] == counter) {
				x++;
				y--;
			}
			if (findDepth(y) == (row + 1))
				doNot.add(y);
		}

		count = countConnected(row - 1, col - 1, NW, counter) + countConnected(row + 1, col + 1, SE, counter);
		if (count == 2) {
			int x = row;
			int y = col;
			while (x > 0 && y > 0 && board.getBoard()[x][y] == counter) {
				x--;
				y--;
			}
			if (findDepth(y) == (row + 1)) {
				doNot.add(y);
				if (Bot.i == 1000)
					System.out.println("found it");
			}
			x = row;
			y = col;
			while (x < 5 && y < 7 && board.getBoard()[x][y] == counter) {
				x++;
				y++;
			}
			if (findDepth(y) == (row + 1))
				doNot.add(y);
		}
	}

	public int countConnected(int row, int col, Direction dir, int counter) {
		if (row < GameConsts.ROWS && row > -1 && col < GameConsts.COLUMNS && col > -1
				&& board.getBoard()[row][col] == counter) {
			switch (dir) {
			case N:
				return 1 + countConnected(row - 1, col, dir, counter);
			case S:
				return 1 + countConnected(row + 1, col, dir, counter);
			case E:
				return 1 + countConnected(row, col + 1, dir, counter);
			case W:
				return 1 + countConnected(row, col - 1, dir, counter);
			case NE:
				return 1 + countConnected(row - 1, col + 1, dir, counter);
			case NW:
				return 1 + countConnected(row - 1, col - 1, dir, counter);
			case SE:
				return 1 + countConnected(row + 1, col + 1, dir, counter);
			case SW:
				return 1 + countConnected(row + 1, col - 1, dir, counter);
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	public int findDepth(int col) {
		int depth = 0;
		while (depth < maxRows && board.getBoard()[depth][col] == 0) {
			depth++;
		}
		return depth - 1;
	}

}
