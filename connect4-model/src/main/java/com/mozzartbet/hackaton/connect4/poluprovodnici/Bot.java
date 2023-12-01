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

        private int move;

        public ArrayList<MiniMaxNode> nextMoves;

        public MiniMaxNode(int minimax, GameBoard bord){
            miniMax = minimax;
            gameBoard = bord;
            nextMoves = new ArrayList<MiniMaxNode>();
            move = 0;

        }

        public float calculateGain(){

            if (gameBoard.isGameOver()) {
                gain = miniMax;
            }
            else if(nextMoves.size() == 0) {
                gain = gainLogic();;
            }
            else {
                gain = -miniMax;
                int nextN = nextMoves.size();
                for (int i = 0; i < nextN; i++){
                    float nextGain = nextMoves.get(i).calculateGain();
                    if (nextGain != gain){
                        move = nextMoves.get(i).getCurrentMove();
                    }
                    gain = miniMaxGain(nextGain);
                }
            }
            return gain;
        }

        public int getNextMove(){
            return move;
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
        public float gainLogic(){
            return 0;
        }

        public float getGain(){
            return gain;
        }

        public boolean addMove(int counter, int col){
            GameBoard newMove = gameBoard.deepCopy();
            boolean legal  = newMove.placeCounter(col, counter);
            if (legal)
                nextMoves.add(new MiniMaxNode(-miniMax, newMove));
            return legal;
        }

    }



    private int searchDepth = 5;
    private GameBoard gameBoard;

    private int time_needed;

    public Bot(){
        this.gameBoard = new GameBoard();
    }

    public MiniMaxNode makeTree(int depth){
        MiniMaxNode root = new MiniMaxNode(1, gameBoard);
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

    @Override
    public void configure(long timeoutMillis) {

    }

    @Override
    public void move() {
        MiniMaxNode root = makeTree(4);
        float gain =root.calculateGain();
        move = root.getNextMove();
        gameBoard.placeCounter(move, 3-gameBoard.getLastCounterPlaced());
    }

    @Override
    public void stop() {

    }

    @Override
    public void opponentMove(int move) {
        gameBoard.placeCounter(move, 3 - gameBoard.getLastCounterPlaced());
    }

    @Override
    public void finished(int winner) {

    }
}
