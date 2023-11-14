package com.mozzartbet.hackaton.connect4.bot.mv_bot;

import com.mozzartbet.hackaton.connect4.model.GameConsts;

public class MyBoard {
	final int height = GameConsts.ROWS;
	final int width = GameConsts.COLUMNS;
	int move,lastMoved=0;
	int[][] board;
	
	//whoMoved me=1, enemy = -1;
	
	//moves se dropuju u col
	
	public MyBoard(){
		board = new int[width][height];
		init();		
	}
	
	public MyBoard(MyBoard b, int newMove, int whoMoved){
		board = b.copyBoard();
		board[newMove][findDepth(newMove)]=whoMoved;
		lastMoved=whoMoved;
		
		
	}
	
	public void printBoard(){
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++)
				System.out.print("["+board[i][j]+"]");
			System.out.println();
		}
	}
	
	
	
	public void addMove(int move, int whoMoved){
		int i=0;
		while(findDepth((move+i)%8)>5)
			i++;
		board[move][findDepth((move+i)%8)]=whoMoved;
		
	}
	
	public int getWinner(){
		int w;
		w=checkRows();
		if(w!=0) return w;
		w=checkColumns();
		if(w!=0) return w;
		w=checkLDiagonal();
		if(w!=0) return w;
		w=checkRDiagonal();
		if(w!=0) return w;
		
		
		
		return 0;
	}
	
	private int checkRows(){
		int cnt=0;
		
		
		for(int j=0;j<height;j++){
			cnt=0;
			for(int i=1;i<width;i++){
				if(board[i-1][j]==board[i][j]&& board[i][j]!=0)
					cnt++;
				else cnt=0;
				
				if(cnt==3)
					return board[i][j];
			
			}
		}
	
		return 0;
	}
	
	private int checkColumns(){
		int cnt=0;
		
		for(int i=0;i<width;i++){
			cnt=0;
			for(int j=1;j<height;j++){
				if(board[i][j-1]==board[i][j] && board[i][j]!=0)
					cnt++;
				else cnt=0;
				if(cnt==3)
					return board[i][j];
			
			}
		}
		
		return 0;
	}
	
	private int checkRDiagonal(){
		int x,y,cnt=0;
		
		for(int i=0;i<width-GameConsts.IN_A_ROW+1;i++){
			x=i; y=0;
			while(x<width-1 && y<height-1){
				x++;
				y++;
				if(board[x-1][y-1]==board[x][y] && board[x][y]!=0)
					cnt++;
				else cnt=0;
				if(cnt==3)
					return board[x][y];
			}
		}
		
		for(int i=1;i<height-GameConsts.IN_A_ROW+1;i++){
			x=0; y=i;
			while(x<width-1 && y<height-1){
				x++;
				y++;
				if(board[x-1][y-1]==board[x][y] && board[x][y]!=0)
					cnt++;
				else cnt=0;
				if(cnt==3)
					return board[x][y];
			}
		}
		
		return 0;
	}
	
	private int checkLDiagonal(){
		int x,y,cnt=0;
		
		for(int i=0;i<width-GameConsts.IN_A_ROW+1;i++){
			x=i; y=height-1;
			while(x<width-1 && y>0){
				x++;
				y--;
				if(board[x-1][y+1]==board[x][y] && board[x][y]!=0)
					cnt++;
				else cnt=0;
				if(cnt==3)
					return board[x][y];
			}
		}
		
		for(int i=1;i<height-GameConsts.IN_A_ROW+1;i++){
			x=0; y=height-1-i;
			while(x<width-1 && y>0){
				x++;
				y--;
				if(board[x-1][y+1]==board[x][y] && board[x][y]!=0)
					cnt++;
				else cnt=0;
				if(cnt==3)
					return board[x][y];
			}
		}
		
		return 0;
	}
	
	private void init(){
		for (int i = 0; i < width; i++) 
			for (int j = 0; j < height; j++) 
				board[i][j] = 0;
	}
	
	public int[][] copyBoard(){
		int[][]copy=new int[width][height];
		for (int i = 0; i < width; i++) 
			for (int j = 0; j < height; j++) 
				copy[i][j] = board[i][j];
		
		
		return copy;
	}
	
	public int findDepth(int col) {
		int depth = 0;
		for(int i=0;i<height;i++){
			if(board[col][i]!=0)
					depth++;
			
		}
		
		return depth;
	}

}
