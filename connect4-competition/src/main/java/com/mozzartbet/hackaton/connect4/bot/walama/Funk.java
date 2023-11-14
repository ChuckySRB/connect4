package com.mozzartbet.hackaton.connect4.bot.walama;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Funk implements Runnable {

	Bot bot;
	
	public Funk(Bot bot) {	//meeeeh, ovako je najlakse da se menja move.
		this.bot = bot;
	}
	
	// test thread run
//	@Override
//	public void run() {
//		int cmd = 12345;
//		int magic = 23456;
//		int mlatka = 56789;
//		for (int i = 0; i < 1000000000; i ++) {
//			cmd = cmd * magic % mlatka;
//			if (i % 100000000 == 0) {
//				System.out.println("e de si e de si, funk " + i);
//				bot.setCurrentMove(bot.getCurrentMove() + 1);
//			}
//			
//		}
//	}
	

	@Override
	public void run() {
		if (bot.READ_FROM_FILE) {
			readFromFile();
		}
		else {
			readFromString();
		}
	}
	
	private void readFromFile() {
		Scanner s = null;
		try {
			s = new Scanner(new File(bot.filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bot.maxPrecomputedDepth = s.nextInt();
		int cnt = 0;
		while(s.hasNext()) {
			long key = s.nextLong();
			double val = s.nextDouble();
			double val_min = s.nextDouble();
			double val_max = s.nextDouble();
			bot.precomputed_prob.put(key, val);
			bot.precomputed_min.put(key, val_min);
			bot.precomputed_max.put(key, val_max);
			
			cnt ++;
			
			if (cnt % 1000000 == 0) {
				System.out.println("getting precomputed " + cnt + " of " + bot.maxPrecomputedDepth);
			}
			
		}
		System.out.println("finished loading precomputed values");
	}
	
	private void readFromString() {
		String stringInput = "";
		
		
		Scanner s = null;
		s = new Scanner(stringInput);
		
		//System.out.println("herhe");
		
		bot.maxPrecomputedDepth = s.nextInt();
		int cnt = 0;
		while(s.hasNext()) {
			long key = s.nextLong();
			double val = s.nextDouble();
			double val_min = s.nextDouble();
			double val_max = s.nextDouble();
			bot.precomputed_prob.put(key, val);
			bot.precomputed_min.put(key, val_min);
			bot.precomputed_max.put(key, val_max);
			
			cnt ++;
			
			if (cnt % 1000000 == 0) {
				System.out.println("getting precomputed " + cnt + " of " + bot.maxPrecomputedDepth);
			}
			
		}
		System.out.println("finished loading precomputed values");
	}
}
