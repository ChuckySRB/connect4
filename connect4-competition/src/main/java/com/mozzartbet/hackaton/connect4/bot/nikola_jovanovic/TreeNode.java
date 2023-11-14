package com.mozzartbet.hackaton.connect4.bot.nikola_jovanovic;

import java.util.LinkedList;

public class TreeNode {

	public int[][] board;
	public float koef;
	public int prevMove;
	LinkedList<TreeNode> sons;
	
	public TreeNode(int [][]board, int prevMove) {
		this.board = board;
		koef = 0;
		this.prevMove = prevMove;
		sons = new LinkedList<>();
	}
	
	public void propageteKoefs() {
		float koefs = 1, sum = koef;
		for (TreeNode node : sons) {
			node.propageteKoefs();
			koefs++;
			sum += node.koef;
		}
		koef = sum + koef / koefs;
	}
}
