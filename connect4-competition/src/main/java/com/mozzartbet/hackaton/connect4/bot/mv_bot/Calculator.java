package com.mozzartbet.hackaton.connect4.bot.mv_bot;

import javax.swing.JOptionPane;

import com.mozzartbet.hackaton.connect4.model.GameConsts;

public class Calculator {
	public static boolean cancelSearch=false;
	public static int DepthOfSearch=7;
	
	public static int width=GameConsts.COLUMNS;
	public static int height=GameConsts.ROWS;
	public static boolean iCANDOIT=false;
	public static int IDIDIT=-1;
	
	public static boolean dontPlayFlag=false;
	public static int moveNotToPlay=-1;

	
	public static int findMove(MyBoard b,int whoseTurn, int depth){
		int bestOutcome=1;
		int worstoutcome=-1;
		int bestMove=0;
		int  outcome=0;
		int w=b.getWinner();
		int move=0;
		
		if(w!=0){
			if(w==1)return 10;
			else return -10;
		}
		
		
		for(int i=0;i<GameConsts.COLUMNS;i++)
		if(b.findDepth(i)<GameConsts.ROWS && depth!=0){
			move=findMove(new MyBoard(b, i, whoseTurn), whoseTurn*(-1), depth-1);
			if(outcome>bestOutcome){
				bestOutcome=outcome;
				bestMove=i;
				
			}
			if(worstoutcome>outcome){
				
			}
		}
		
	
		return bestMove;
	}
	
	public static AdvancedMove find(MyBoard b,int LMOVE,int whoseTurn, int depth){
		iCANDOIT=false;
		IDIDIT=-1;
		dontPlayFlag=false;
		moveNotToPlay=-5;
		AdvancedMove move=new AdvancedMove();
		int winner=b.getWinner();
		int br=0;
		if(winner==1 && depth==DepthOfSearch-1){
			iCANDOIT=true;
			
			IDIDIT=LMOVE;
			move.winner=winner;
			move.move=LMOVE;
			move.sourceDepth=depth;
			move.risk=-100000;
			return move;
		}
		
		
		
		if(winner==-1 && depth==DepthOfSearch-2){
			
			moveNotToPlay=LMOVE;
			dontPlayFlag=true;
			move.winner=winner;
			move.move=LMOVE;
			move.sourceDepth=depth;
			move.risk=1110;
			return move;
		}
		
		if(winner!=0){
			move.winner=winner;
			move.move=LMOVE;
			move.sourceDepth=depth;
			move.risk=winner*(depth)*10;
			return move;
		}
		
		if(depth<1){
			return new AdvancedMove();
		}
		
		for(int i=0;i<width;i++)
			if(b.findDepth(i)<GameConsts.ROWS){
			AdvancedMove m=find(new MyBoard(b, i, whoseTurn), i, whoseTurn*(-1), depth-1);
			if(iCANDOIT){
				return null;
			}
			int risk=m.winner*m.sourceDepth;
			risk*=-1;
			m.risk+=risk;
			
			
			
			if(m.risk<br){
				move=m;
				br=m.risk;
			}
		}
		
		
	return move;	
	}
	
	
	
	public static SearchConfig searchMove(MyBoard b,int whoseTurn,int depthLeft){
		int bestOutcome=-100;
		int bestMove=-1;
		SearchConfig  outcome;
		SearchConfig cfg=new SearchConfig();
		int w=b.getWinner();
		if(w!=0){
			if(w==1)
				cfg.A_WIN=1;
			else cfg.E_WIN=1;
			
			return cfg;
			
		}
		
		if(depthLeft==0)
			return cfg;
		
		for(int i=0;i<GameConsts.COLUMNS;i++){
			if(b.findDepth(i)<GameConsts.ROWS){
				outcome=searchMove(new MyBoard(b, i, whoseTurn), whoseTurn*(-1), depthLeft-1);
				int ors=outcome.A_WIN-outcome.E_WIN;
				ors*=-1;
				if(outcome.A_WIN-outcome.E_WIN>bestOutcome){
					bestOutcome=ors;
					bestMove=i;
					cfg=outcome;
					
					
				}
			}
		}
		
		
		
		return cfg;
	}

}
