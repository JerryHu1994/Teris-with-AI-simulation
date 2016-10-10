import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * TerisFrame represents the menu bar and its corresponding action listener
 * 
 * @author Jerry
 *
 */
public class TerisMenu extends JMenuBar implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JMenu GAME, ABOUT;
	private final JMenuItem NEWGAME, RECORD, RULE, AUTHOR, EXIT, AI;
	private TerisPanel panel;
	private JFrame window;
	private Game game;
	private final String rules = " Left Arrow : move the block towards left \n Right Arrow : move the block towards right \n Up Arrow : rotate the block "
			+ "\n a:Turn on and off the AI mode" + "\n Space : pause & restart the game";
	private final String authorInfo = " Author: Jieru Hu (Jerry) \n Student from the University of Wisconsin Madison \n Majors in Mechanical"
			+ "Engineering & Computer Science & Applied Mathematics \n Hometown:Ningbo, China \n Email:jhu76@wisc.edu"
			+ "\n Go Badgers !!! ";
	private final String AIInfo = "Once the AI mode is turned on, the AI would predict the next best location \nto fit the current block. The predicted location is composed of white squares. \n"
			+ "\nThis AI uses a combination of modified BFS and a tuned heuristic function to make prediction.";

	public TerisMenu(TerisPanel p, JFrame win, Game g) {
		panel = p;
		window = win;
		game = g;

		// initialize the JMenus
		GAME = new JMenu("Game");

		ABOUT = new JMenu("About");
		// initialize the JMenuItems
		NEWGAME = new JMenuItem("New game");
		RECORD = new JMenuItem("Record");

		RULE = new JMenuItem("Rules");
		AUTHOR = new JMenuItem("Author");
		AI = new JMenuItem("AI");
		EXIT = new JMenuItem("Exit");

		// add the actionlistener
		NEWGAME.addActionListener(this);
		RECORD.addActionListener(this);

		RULE.addActionListener(this);
		AUTHOR.addActionListener(this);
		EXIT.addActionListener(this);
		AI.addActionListener(this);
		// add menu
		add(GAME);

		add(ABOUT);

		// add menuitem
		GAME.add(NEWGAME);
		GAME.add(RECORD);
		GAME.add(EXIT);

		ABOUT.add(RULE);
		ABOUT.add(AI);
		ABOUT.add(AUTHOR);
	}

	// Add all the JMenuItems to the Frame
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource().equals(NEWGAME)) {
			game.newGame();
		}
		if (e.getSource().equals(RECORD)) {
			panel.pauseGame();
			JOptionPane.showMessageDialog(window, "       Best Scores" + "\n" + game.getScoreHistory(), "Scoreboard",
					JOptionPane.INFORMATION_MESSAGE);
		}
		if (e.getSource().equals(EXIT)) {
			System.exit(0);
		}

		if (e.getSource().equals(RULE)) {
			panel.pauseGame();
			JOptionPane.showMessageDialog(window, rules, "Game Rules", JOptionPane.INFORMATION_MESSAGE);
		}
		if (e.getSource().equals(AUTHOR)) {
			panel.pauseGame();
			JOptionPane.showMessageDialog(window, authorInfo, "About the Author", JOptionPane.INFORMATION_MESSAGE);
		}
		if (e.getSource().equals(AI)) {
			panel.pauseGame();
			JOptionPane.showMessageDialog(window, AIInfo, "About AI", JOptionPane.INFORMATION_MESSAGE);
		}

	}

}
