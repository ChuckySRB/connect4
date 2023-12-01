package com.mozzartbet.hackaton.connect4.poluprovodnici;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.GameConsts;
import com.mozzartbet.hackaton.connect4.model.Player;
import com.mozzartbet.hackaton.connect4.view.GameView;
import com.mozzartbet.hackaton.connect4.view.MatchInfo;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

public class Bot extends Player {

    private class MiniMaxNode {
        private GameBoard gameBoard;
        private int miniMax;

        private float gain;

        private int moveMini;

        public ArrayList<MiniMaxNode> nextMoves;

        public MiniMaxNode(int minimax, GameBoard bord){
            miniMax = minimax;
            gameBoard = bord;
            nextMoves = new ArrayList<MiniMaxNode>();
            moveMini = 4;

        }

        public float calculateGain(){

            if (gameBoard.isGameOver()) {
                gain = -miniMax;
            }
            else if(nextMoves.size() == 0) {
                gain = gainLogic(3-gameBoard.getLastCounterPlaced(), gameBoard);;
            }
            else {
                gain = -miniMax;
                int nextN = nextMoves.size();
                for (int i = 0; i < nextN; i++){
                    float nextGain = miniMaxGain(nextMoves.get(i).calculateGain());
                    if (nextGain != gain){
                        moveMini = nextMoves.get(i).getCurrentMove();
                    }
                    gain = nextGain;
                }
            }
            return gain;
        }

        public int getNextMove(){
            return moveMini;
        }

        public MiniMaxNode getOneMOve(int index){
            if (nextMoves.size() >= index)
                return null;
            return nextMoves.get(index);
        }

        public int getCurrentMove(){
            return gameBoard.getLastMove().getCol();
        }

        public float miniMaxGain(float value){
            if (miniMax <  0) {
                return Math.min(value, gain);
            }
            else {
                return Math.max(value, gain);
            }
        }

        public float evaluateBoxInner(int currentPlayer){
            int boardMatrix [][]= gameBoard.getBoard();
            float cnt = 0;
            for (int j=0; j<GameConsts.ROWS; j++){
                for (int i = 3; i <=4; i++){
                    if (boardMatrix[j][i] == currentPlayer)
                        cnt++;
                    else if (boardMatrix[j][i] != 0){
                        cnt--;
                    }
                }
            }
            cnt = cnt*miniMax/3;
            return Math.min(1.0f, Math.max(-1.0f, cnt));
        }

        public float evaluateBoxOuter(int currentPlayer){
            int boardMatrix [][]= gameBoard.getBoard();
            float cnt = 0;
            for (int j=0; j<GameConsts.ROWS; j++){
                for (int i = 2; i <=5; i++){
                    if (boardMatrix[j][i] == currentPlayer)
                        cnt++;
                    else if (boardMatrix[j][i] != 0){
                        cnt--;
                    }
                }
            }
            cnt = cnt*miniMax/5;
            return Math.min(1.0f, Math.max(-1.0f, cnt));
        }

        public float evaluateLevel(){

            float eval = (float) (1 - gameBoard.getLastMove().getRow()*0.1);
            return miniMax*eval;
        }

        public float evaluateChain(int currentPlayer){

            float eval = (float) 0;
            int boardMatrix [][]= gameBoard.getBoard();
            int column = gameBoard.getLastMove().getCol();
            int row = gameBoard.getLastMove().getRow();

            int num = 0;
            int factor = 0;
            // gledaj horizont
            for (int cColumn = column-1; cColumn > 0; cColumn--){
                if(boardMatrix[row][cColumn] == currentPlayer){
                    num++;
                }
                else if (boardMatrix[row][cColumn] == 0){
                    factor++;
                    break;
                }
                else{
                    break;
                }
            }
            for (int cColumn = column+1; cColumn < GameConsts.COLUMNS; cColumn++){
                if(boardMatrix[row][cColumn] == currentPlayer){
                    num++;
                }
                else if (boardMatrix[row][cColumn] == 0){
                    factor++;
                    break;
                }
                else{
                    break;
                }
            }

            num *=factor;

            num = 0;
            factor = 0;
            // gledaj horizont
            for (int cColumn = column-1, cRow = row-1; cColumn > 0 && cRow > 0; cColumn--, cRow--){
                if(boardMatrix[cRow][cColumn] == currentPlayer){
                    num++;
                }
                else if (boardMatrix[cRow][cColumn] == 0){
                    factor++;
                    break;
                }
                else{
                    break;
                }
            }
            for (int cColumn = column+1, cRow = row+1; cColumn < GameConsts.COLUMNS && cRow < GameConsts.ROWS; cColumn++, cRow++) {
                if (boardMatrix[cRow][cColumn] == currentPlayer) {
                    num++;
                } else if (boardMatrix[cRow][cColumn] == 0) {
                    factor++;
                    break;
                } else {
                    break;
                }

            }
                num *=factor;

            eval += num/6;

            num = 0;
            factor = 0;
            // gledaj horizont
            for (int cColumn = column+1, cRow = row-1; cColumn < GameConsts.COLUMNS && cRow > 0; cColumn++, cRow--){
                if(boardMatrix[cRow][cColumn] == currentPlayer){
                    num++;
                }
                else if (boardMatrix[cRow][cColumn] == 0){
                    factor++;
                    break;
                }
                else{
                    break;
                }
            }
            for (int cColumn = column-1, cRow = row+1; cColumn > 0 && cRow < GameConsts.ROWS; cColumn--, cRow++){
                if(boardMatrix[cRow][cColumn] == currentPlayer){
                    num++;
                }
                else if (boardMatrix[cRow][cColumn] == 0){
                    factor++;
                    break;
                }
                else{
                    break;
                }
            }


                num *=factor;

                eval += num/6;

                // gledaj vertikala

                // gledaj /

                // gledaj \

                eval = Math.min(1.0f, Math.max(-1.0f, eval/3));

                return miniMax*eval;

            }

        public float gainLogic(int currentPlayer, GameBoard board) {
            // Check for winning states

            if (board.getWinner() == currentPlayer) {
                return 1.0f;
            } else if (board.getWinner() == 3-currentPlayer) {
                return -1.0f;
            }

            // Check for the number of winning positions for both players
            float middleBoxEvaluation = (float)(evaluateBoxInner(currentPlayer) * 0.2  + evaluateBoxOuter(currentPlayer) * 0.1);

            float chainEvaluation = (float)(evaluateChain(currentPlayer)*0.4);

            //
            float levelEvaluation = (float)(evaluateLevel()*0.1);
            // Combine factors to get an overall evaluation
            float evaluation = 0;
            evaluation += middleBoxEvaluation;
            evaluation += levelEvaluation;
            evaluation += chainEvaluation;

            // Normalize the evaluation to be between -1 and 1
            return Math.min(1.0f, Math.max(-1.0f, evaluation));
        }

        public float getGain(){
            return gain;
        }

        public boolean addMove(int counter, int col){
            if (this.gameBoard.isGameOver())
                return false;
            GameBoard newMove = gameBoard.deepCopy();
            boolean legal  = newMove.placeCounter(col, counter);
            if (legal)
                nextMoves.add(new MiniMaxNode(-miniMax, newMove));
            return legal;
        }

    }



    private int searchDepth = 6;
    private GameBoard gameBoard;

    private int time_needed;

    public Bot(){
        this.gameBoard = new GameBoard();
    }

    public MiniMaxNode makeTree(int depth){
        MiniMaxNode root = new MiniMaxNode(1, gameBoard.deepCopy());
        ArrayList<MiniMaxNode> nodes = new ArrayList<MiniMaxNode>();
        nodes.add(root);
        for (int i = 0; i < depth; i++){
            int n = nodes.size();
            for (int j = 0; j < n; j++){
                MiniMaxNode current = nodes.remove(0);
                int lenght = GameConsts.COLUMNS;
                for (int k = 0; k < lenght; k++){
                    if (current.addMove(3-current.gameBoard.getLastCounterPlaced(), k))
                        if (current.nextMoves.size() > 0)
                            nodes.add(current.nextMoves.get(current.nextMoves.size() - 1));
                }

            }
        }
        return root;
    }

    public int getDepth(){
        float chips = gameBoard.getCountersPlaced();
        float total_chips = GameConsts.ROWS * GameConsts.COLUMNS;
        float precentage = chips/total_chips*100;

        if (precentage < 25){
            return 5;
        }
        else if (precentage < 80){
            return 6;
        }

        else if (precentage < 90){
            return 7;
        }

        else
            return 8;
    }

    @Override
    public void configure(long timeoutMillis) {
        gameBoard.reset();
    }

    @Override
    public void move() {
        System.out.println(getDepth());
        MiniMaxNode root = makeTree(getDepth());
        float gain =root.calculateGain();
        move = root.getNextMove();
        gameBoard.placeCounter(move, 3-gameBoard.getLastCounterPlaced());
        if (gameBoard.isGameOver()){
            gameBoard.reset();

        }    }

    @Override
    public void stop() {

    }

    @Override
    public void opponentMove(int move) {
        gameBoard.placeCounter(move, 3 - gameBoard.getLastCounterPlaced());
        if (gameBoard.isGameOver()){
            gameBoard.reset();

        }
    }

    @Override
    public void finished(int winner) {
        gameBoard.reset();
    }
}
