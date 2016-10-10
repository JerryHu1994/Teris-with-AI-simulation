import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The game class integrates the TerisPanel, TerisFrame and sets the
 * configuration for the game.
 * 
 * @author Jerry
 *
 */

public class Game {
	private final JFrame GameWindow;
	private TerisMenu menu;
	private TerisPanel panel;
	private final double dimensionratio = 2;
	private final double gridratio = 20;
	private int gridsize = 30;
	private ArrayList<Record> gamehistory;

	public Game() {
		GameWindow = new JFrame();
		int Width = (int) (gridsize * gridratio);
		int Height = (int) (Width * dimensionratio);
		GameWindow.setSize(Width, Height);
		GameWindow.setTitle("Teris");
		GameWindow.setLayout(new FlowLayout());
		GameWindow.setVisible(true);
		JOptionPane.showMessageDialog(GameWindow,
				"Welcome to the Teris game!" + "\n" + "Press any arrow key to start" + "\n" + "Good Luck!",
				"Welcome Window", JOptionPane.INFORMATION_MESSAGE);

		newGame();
		panel.setSize(Width, Height);
		gamehistory = new ArrayList<Record>();

	}

	// start a new game
	public void newGame() {
		panel = new TerisPanel(GameWindow, gridsize, this);
		menu = new TerisMenu(panel, GameWindow, this);
		GameWindow.setContentPane(panel);
		GameWindow.addKeyListener(panel);
		GameWindow.setJMenuBar(menu);
		GameWindow.setFocusable(true);
		GameWindow.setResizable(true);
		GameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GameWindow.setVisible(true);
	}

	// add a record
	public void addRecord(Record record) {

		gamehistory.add(record);
	}

	// sort the socres and return the score history info
	public String getScoreHistory() {
		System.out.println(gamehistory.size());
		Collections.sort(gamehistory, Collections.reverseOrder());

		String info = "";
		for (int i = 1; i < 6; i++) {
			if (i > gamehistory.size()) {
				info += i + "-----------------" + "\n";
			} else {
				info += i + " " + gamehistory.get(i - 1).toString() + "\n";
			}
		}
		return info;
	}

}
