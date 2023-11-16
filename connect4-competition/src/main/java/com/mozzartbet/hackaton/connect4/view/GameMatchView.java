package com.mozzartbet.hackaton.connect4.view;

import static java.awt.BorderLayout.*;
import static java.util.Collections.*;
import static java.util.concurrent.Executors.*;
import static java.util.concurrent.TimeUnit.*;
import static javax.swing.JFrame.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mozzartbet.hackaton.connect4.model.GameBoard;

public class GameMatchView {

	final Logger logger = LoggerFactory.getLogger(getClass());

	// Game tracking

	final DisplayedBoard displayedBoard;
	final MatchProvider matchProvider;

	final Object refreshLock = new Object();

	List<MatchInfo> available = emptyList();
	MatchInfo current;

	// UI

	JTextArea title;
	JComboBox<MatchInfo> matchBox;
	JPanel mainPanel;

	//

	public GameMatchView(MatchProvider matchProvider) {
		this.matchProvider = matchProvider;
		this.displayedBoard = new DisplayedBoard(new GameBoard());

		//

		final JFrame frame = new JFrame("Connect Four [Live]");
		frame.setLocation(310, 130);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel titlePanel = new JPanel();
		title = new JTextArea("<waiting>");
		titlePanel.add(title);

		//

		JPanel bottomRow = new JPanel();

		matchBox = new JComboBox<>();
		matchBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				current = (MatchInfo) matchBox.getSelectedItem();
				new RefreshCurrent().run();
			}
		});
		bottomRow.add(matchBox);

		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				current = null;
				new RefreshAvailable().run();
				new RefreshCurrent().run();
			}
		});
		refresh.setEnabled(true);
		bottomRow.add(refresh);

		//

		JPanel optionButtons = new JPanel();
		optionButtons.setLayout(new BorderLayout());
		optionButtons.add(bottomRow, NORTH);

		displayedBoard.setPreferredSize(new Dimension(598, 516));

		displayedBoard.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				displayedBoard.repaint();
			}
		});

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		mainPanel.add(titlePanel, NORTH);
		mainPanel.add(displayedBoard, CENTER);
		mainPanel.add(optionButtons, SOUTH);

		frame.add(mainPanel, CENTER);

		frame.pack();
		frame.setVisible(true);

		scheduleRefresh();
	}

	//

	final ScheduledExecutorService scheduler = newScheduledThreadPool(2);

	protected void scheduleRefresh() {
		scheduler.scheduleWithFixedDelay(new RefreshCurrent(), 2000, 10, MILLISECONDS);
		scheduler.scheduleWithFixedDelay(new RefreshAvailable(), 1000, 50, MILLISECONDS);
	}

	class RefreshCurrent implements Runnable {
		@Override
		public void run() {
			synchronized (refreshLock) {
				if (current == null && !available.isEmpty()) {
					current = available.get(0);
				}

				if (current != null) {
					current = matchProvider.refresh(current);
					title.setText(current.getTitle());
					displayedBoard.setBoard(current.getBoard());
					mainPanel.repaint();

					//logger.debug("{} - {}", current, current.getBoard().getLastMove());
				}
			}
		}
	}

	class RefreshAvailable implements Runnable {
		@Override
		public void run() {
			try {
				synchronized (refreshLock) {
					available = matchProvider.availableMatches();

					ComboBoxModel<MatchInfo> model = new DefaultComboBoxModel<>(available.toArray(new MatchInfo[0]));
					matchBox.setModel(model);
					matchBox.repaint();

					logger.debug("{}", available);

					if (current != null && current.getBoard().isGameOver()) {
						current = null;
					}
				}
			} catch (RuntimeException e) {
				logger.error("Refresh available error!", e);
			}
		}

	}

	//

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GameMatchView(new LiveMatchProvider());
			}
		});
	}

}
