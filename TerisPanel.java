import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The Panel implements different behaviors of the game, keyboard listener,
 * painting.
 * 
 * @author Jerry
 *
 */
public class TerisPanel extends JPanel implements KeyListener, ActionListener {

	private final Color colorlist[] = { Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
	private static final long serialVersionUID = 1L;
	private block currentblock;
	private block nextblock;
	private int score;
	int[][] gameMap = new int[20][10];
	private int gridwidth = 20;
	private int gridoffsetx;
	private int gridoffsety;
	private double frequency = 500;
	private int[][] coord = new int[4][2];
	private int[][] nextcoord = new int[4][2];
	private int[][] shade = new int[4][2]; // the preview of the fixed blocks
	private JFrame window;
	private Game game;
	private BufferedImage badger;
	private int level;
	private Timer timer;
	private boolean isUpdating;
	private int countdown;
	private String gameStatus;
	private int levelscore;
	private boolean levelup;
	private TerisAI ai;
	private int[][] bestMove;
	private boolean displayAI;
	private String AIonoff;

	public TerisPanel(JFrame win, int gsize, Game g) {
		game = g;
		score = 0;
		levelscore = score;
		level = 1;
		isUpdating = false;
		levelup = false;
		currentblock = new block();
		nextblock = new block();
		// execute the ActionListener every 500ms
		initializeMap();
		setTimer(frequency);
		gridwidth = gsize;
		gridoffsetx = gridwidth / 2;
		gridoffsety = 4 * gridoffsetx;
		calCoord();
		calNextCoord();
		ai = new TerisAI(this);
		displayAI = false;
		bestMove = new int[4][2];
		AIonoff = "OFF";
		window = win;
		setBackground(Color.BLACK);
		// read the badger image
		try {
			badger = ImageIO.read(getClass().getResourceAsStream("logo.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startGame();
	}

	private void startGame() {
		// create the count down animation
		countdown = 4;
		gameStatus = "Preparing";
		timer.setDelay(1000);
		timer.start();

	}

	// initialize the gameMap index toz zero
	public void initializeMap() {
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				gameMap[i][j] = -1;
			}
		}
	}

	public void setTimer(double frequency2) {
		frequency = frequency2;
		timer = new Timer((int) frequency, this);
		timer.setInitialDelay(500);
	}

	public void setgridwidth(int w) {
		gridwidth = w;
	}

	public block getCurrblock() {
		return currentblock;
	}

	public int[][] getCurrMap() {
		return gameMap;
	}

	// pause the game
	public void pauseGame() {
		timer.stop();
		gameStatus = "Paused";
	}

	// resume the game
	public void resumeGame() {
		timer.start();
		gameStatus = "Playing";
	}

	// rotate the block
	public void turn() {
		currentblock.rotateCCW();
		calCoord();
	}

	// move the current block to left
	public void moveLeft() {
		currentblock.setX(currentblock.getX() - 1);
		calCoord();
	}

	// move the current block to right
	public void moveRight() {
		currentblock.setX(currentblock.getX() + 1);
		calCoord();
	}

	// move the current block to bottom
	public void moveDown() {
		currentblock.setY(currentblock.getY() + 1);
		calCoord();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (isUpdating) {
			return;
		}

		switch (e.getKeyCode()) {
		// move the block down
		case KeyEvent.VK_DOWN:
			if (isFixed(coord)) {
				break;
			}
			if (canmoveDown()) {
				moveDown();
			}
			repaint();
			break;
		// turn the block counterclockwise
		case KeyEvent.VK_UP:
			if (canTurn()) {
				turn();
			}
			repaint();
			break;
		// move the block to left
		case KeyEvent.VK_LEFT:
			if (canmoveLeft()) {
				moveLeft();
			}
			repaint();
			break;
		// move the block to right
		case KeyEvent.VK_RIGHT:
			if (canmoveRight()) {
				moveRight();
			}
			repaint();
			break;
		// pause the game
		case KeyEvent.VK_SPACE:

			if (gameStatus.equals("Playing")) {
				pauseGame();
			} else {
				if (gameStatus.equals("Paused")) {

					resumeGame();
				}
			}
			// turn on the AI mode
		case KeyEvent.VK_A:
			if (displayAI) {
				displayAI = false;
				AIonoff = "OFF";
			} else {
				displayAI = true;
				AIonoff = "ON";
			}
			break;
		}
	}

	// Determine if the block can turn
	private boolean canTurn() {

		int[] curr = currentblock.getrotateShape();
		for (int i = 0; i < curr.length; i++) {
			if (curr[i] == 1) {
				int factor = i / 4;
				int remainer = i % 4;
				int tempx = currentblock.getX() + remainer;
				int tempy = currentblock.getY() + factor;
				if (tempy > 19 || tempx > 9) {
					return false;
				}
				if (gameMap[tempy][tempx] != -1) {
					return false;
				}
			}
		}
		return true;
	}

	// Determine if the block is already fixed
	public boolean isFixed(int[][] list) {
		// check if the block reaches the bottom
		for (int i = 0; i < 4; i++) {
			int y = list[i][1];
			if (y == 19) {
				return true;
			}
		}
		// check if there is a block under the current block
		for (int i = 0; i < 4; i++) {
			int x = list[i][0];
			int y = list[i][1];
			if (y == 19) {
				return true;
			}
			if (gameMap[y + 1][x] != -1) {
				return true;
			}

		}
		return false;
	}

	// Determines if the block is able to move right
	public boolean canmoveRight() {
		// for each block, check on the gamegameMap if it is already occupied or
		// it
		// reaches the boundary
		for (int i = 0; i < 4; i++) {
			int x = coord[i][0] + 1;
			int y = coord[i][1];
			if (x > 9) {
				return false;
			}
			if (gameMap[y][x] != -1) {
				return false;
			}

		}
		return true;
	}

	// Determines if the block is able to move left
	public boolean canmoveLeft() {
		// for each block, check on the gamegameMap if it is already occupied or
		// it
		// reaches the boundary
		for (int i = 0; i < 4; i++) {
			int x = coord[i][0] - 1;
			int y = coord[i][1];
			if (x < 0) {
				return false;
			}
			if (gameMap[y][x] != -1) {
				return false;
			}

		}
		return true;
	}

	// Determines if the block is able to move down
	public boolean canmoveDown() {
		// TODO Auto-generated method stub
		if (!isFixed(coord)) {
			return true;
		}
		return false;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// upgrade the game level if needed
	private void upgrade() {
		if (levelscore >= 100) {
			levelup = true;
			level++;
			frequency = frequency * 0.9;
			timer.setDelay((int) frequency);
			levelscore = 0;
		}
	}

	// The action performed corresponding to the timer
	public void actionPerformed(ActionEvent e) {

		// calculate the current coordinates which was filled by current block
		if (gameStatus.equals("Playing")) {

			upgrade();
			calCoord();

			if (!isFixed(coord)) {
				// move the block down
				moveDown();
			} else {

				if (gameEnd()) {
					String username = JOptionPane.showInputDialog(window,
							"\n" + "The game has ended. You scored " + "\n" + "Please enter your nickname:" + "\n",
							"Game Finished", JOptionPane.QUESTION_MESSAGE);
					Date d = new Date();
					game.addRecord(new Record(username, score, d.toString()));
					timer.stop();
					Object[] options = { "Restart ! ", "Exit..." };
					// exit or restart
					int n = JOptionPane.showOptionDialog(window, "Would you like another game?", "Battle Again",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]); // default
																													// button
																													// title
					if (n == 1) {
						System.exit(1);
					} else {
						game.newGame();
					}

				}

				fixcurrentblock();
				removeline();
				currentblock = nextblock;
				calCoord();
				nextblock = new block();
				calNextCoord();
				bestMove = ai.predict();

			}
		}
		repaint();
	}

	// calculate next block coordinates for printing
	private void calNextCoord() {
		int[] next = nextblock.getshape();
		int count = 0;
		for (int i = 0; i < next.length; i++) {
			if (next[i] == 1) {
				int factor = i / 4;
				int remainer = i % 4;
				nextcoord[count][0] = 13 * gridwidth + remainer * gridwidth;
				nextcoord[count][1] = 14 * gridwidth + factor * gridwidth;
				count++;
			}
		}

	}

	private void calCoord() {
		// calculate the current block coordinates
		int[] curr = currentblock.getshape();
		int count = 0;
		for (int i = 0; i < curr.length; i++) {
			if (curr[i] == 1) {
				int factor = i / 4;
				int remainer = i % 4;
				coord[count][0] = currentblock.getX() + remainer;
				coord[count][1] = currentblock.getY() + factor;
				count++;
			}
		}
		// calculate the shading coordinates of the current block
		// start from the current block, find the lowest position it can be
		// fitted
		for (int i = 0; i < 4; i++) {
			shade[i][0] = coord[i][0];
			shade[i][1] = coord[i][1];
		}

		while (!isFixed(shade)) {
			// move the shade down
			for (int i = 0; i < 4; i++) {
				shade[i][1]++;
			}
		}
	}
	// TODO Auto-generated method stub

	// check if the game has ended
	private boolean gameEnd() {
		for (int i = 0; i < 4; i++) {
			int x = coord[i][0];
			int y = coord[i][1];
			if (y == 0) {
				return true;
			}
		}
		return false;
	}

	// fix the current block onto the gamegameMap
	private void fixcurrentblock() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 4; i++) {
			int x = coord[i][0];
			int y = coord[i][1];
			gameMap[y][x] = currentblock.getColorindex();

		}
	}

	// remove the line which is already full and update the gamegameMap
	private void removeline() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 4; i++) {
			int y = coord[i][1];
			if (isfull(gameMap[y])) {
				score += 10;
				levelscore += 10;
				// move the upper blocks down
				for (int j = y; j > 0; j--) {
					System.arraycopy(gameMap[j - 1], 0, gameMap[j], 0, 10);
				}
			}

		}
		repaint();

	}

	// check if a line is full
	private boolean isfull(int[] is) {

		for (int i = 0; i < is.length; i++) {

			if (is[i] == -1) {
				return false;
			}
		}
		return true;
	}

	// paint the screen
	public void paint(Graphics gg) {
		super.paint(gg);
		Graphics2D g = (Graphics2D) gg;

		g.setColor(Color.gray);
		// draw horizontal lines
		for (int i = 0; i < 21; i++) {
			g.drawLine(gridoffsetx, gridoffsety + i * gridwidth, gridoffsetx + 10 * gridwidth,
					gridoffsety + i * gridwidth);
		}
		// draw vertical lines
		for (int i = 0; i < 11; i++) {
			g.drawLine(gridoffsetx + i * gridwidth, gridoffsety, gridoffsetx + i * gridwidth,
					gridoffsety + 20 * gridwidth);
		}
		// draw everything else
		Font font1 = new Font("Times New Roman", Font.BOLD, 30);
		g.setFont(font1);
		g.setColor(Color.WHITE);
		g.drawString("Teris Game", 3 * gridwidth, (int) (1.5 * gridwidth));

		g.setColor(Color.WHITE);
		g.drawString("Your Score: \n  " + score, 12 * gridwidth, 8 * gridwidth);
		g.drawString("Level: \n  " + level, 12 * gridwidth, 10 * gridwidth);
		g.drawString("Next Block", 12 * gridwidth, 13 * gridwidth);

		int w = badger.getWidth();
		int h = badger.getHeight();
		int scale = 2;
		g.drawImage(badger, 350, 50, w / scale, h / scale, this);

		// draw the countdown animation
		if (gameStatus.equals("Preparing")) {
			if (countdown == 0) {
				gameStatus = "Playing";
				timer.setDelay((int) frequency);
				return;
			}
			g.setColor(Color.WHITE);
			Font font2 = new Font("Times New Roman", Font.BOLD, 80);
			g.setFont(font2);
			g.drawString(String.valueOf(countdown), 5 * gridwidth, 10 * gridwidth);
			countdown--;
			return;
		}

		// draw the blocks which are already painted
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				if (gameMap[i][j] != -1) {
					int realx = gridoffsetx + gridwidth * j;
					int realy = gridoffsety + gridwidth * i;

					g.setColor(colorlist[gameMap[i][j]]);
					g.fillRect(realx, realy, gridwidth, gridwidth);
					g.setColor(colorlist[gameMap[i][j]].brighter());
					// draw the shading effect on the block
					g.fillPolygon(
							new int[] { realx, realx, realx + 5, realx + 5, realx + gridwidth - 5, realx + gridwidth },
							new int[] { realy, realy + gridwidth, realy + gridwidth - 5, realy + 5, realy + 5, realy },
							6);

					g.setColor(colorlist[gameMap[i][j]].darker());
					g.fillPolygon(
							new int[] { realx, realx + 5, realx + gridwidth - 5, realx + gridwidth - 5,
									realx + gridwidth, realx + gridwidth },
							new int[] { realy + gridwidth, realy + gridwidth - 5, realy + gridwidth - 5, realy + 5,
									realy, realy + gridwidth },
							6);

				}
			}
		}

		// draw the current block
		for (int i = 0; i < 4; i++) {
			// convert to the real coordination
			int realx = gridoffsetx + gridwidth * coord[i][0];
			int realy = gridoffsety + gridwidth * coord[i][1];

			// draw the next block display
			g.setColor(colorlist[currentblock.getColorindex()]);
			g.fillRect(realx, realy, gridwidth, gridwidth);
			g.setColor(colorlist[currentblock.getColorindex()].brighter());
			// draw the shading effect on the block
			g.fillPolygon(new int[] { realx, realx, realx + 5, realx + 5, realx + gridwidth - 5, realx + gridwidth },
					new int[] { realy, realy + gridwidth, realy + gridwidth - 5, realy + 5, realy + 5, realy }, 6);

			g.setColor(colorlist[currentblock.getColorindex()].darker());
			g.fillPolygon(
					new int[] { realx, realx + 5, realx + gridwidth - 5, realx + gridwidth - 5, realx + gridwidth,
							realx + gridwidth },
					new int[] { realy + gridwidth, realy + gridwidth - 5, realy + gridwidth - 5, realy + 5, realy,
							realy + gridwidth },
					6);
		}

		for (int i = 0; i < 4; i++) {
			g.setColor(colorlist[nextblock.getColorindex()]);
			int realx = nextcoord[i][0];
			int realy = nextcoord[i][1];
			g.fillRect(realx, realy, gridwidth, gridwidth);
			g.setColor(colorlist[nextblock.getColorindex()].brighter());
			// draw the shading effect on the block
			g.fillPolygon(new int[] { realx, realx, realx + 5, realx + 5, realx + gridwidth - 5, realx + gridwidth },
					new int[] { realy, realy + gridwidth, realy + gridwidth - 5, realy + 5, realy + 5, realy }, 6);

			g.setColor(colorlist[nextblock.getColorindex()].darker());
			g.fillPolygon(
					new int[] { realx, realx + 5, realx + gridwidth - 5, realx + gridwidth - 5, realx + gridwidth,
							realx + gridwidth },
					new int[] { realy + gridwidth, realy + gridwidth - 5, realy + gridwidth - 5, realy + 5, realy,
							realy + gridwidth },
					6);
			g.setColor(Color.GRAY);
			for (int j = 0; j < 4; j++) {
				g.drawLine(nextcoord[i][0], nextcoord[i][1], nextcoord[i][0], nextcoord[i][1] + gridwidth);
				g.drawLine(nextcoord[i][0], nextcoord[i][1], nextcoord[i][0] + gridwidth, nextcoord[i][1]);
				g.drawLine(nextcoord[i][0] + gridwidth, nextcoord[i][1], nextcoord[i][0] + gridwidth,
						nextcoord[i][1] + gridwidth);
				g.drawLine(nextcoord[i][0], nextcoord[i][1] + gridwidth, nextcoord[i][0] + gridwidth,
						nextcoord[i][1] + gridwidth);
			}
		}

		// draw the shade of the current block, if the block is not fixed
		if (isFixed(coord)) {
		} else {
			g.setColor(colorlist[currentblock.getColorindex()]);
			for (int i = 0; i < 4; i++) {
				// convert to the real coordination
				int realx = gridoffsetx + gridwidth * shade[i][0];
				int realy = gridoffsety + gridwidth * shade[i][1];
				g.drawLine(realx, realy, realx + gridwidth, realy);
				g.drawLine(realx, realy, realx, realy + gridwidth);
				g.drawLine(realx, realy + gridwidth, realx + gridwidth, realy + gridwidth);
				g.drawLine(realx + gridwidth, realy, realx + gridwidth, realy + gridwidth);
			}

		}

		// draw the AI switch
		Font font4 = new Font("Times", Font.ITALIC, 30);
		g.setColor(Color.RED);
		g.setFont(font4);
		g.drawString("AI MODE: " + AIonoff, 11 * gridwidth + 10, 20 * gridwidth);
		Font font5 = new Font("Times", Font.ITALIC, 20);
		g.setFont(font5);
		g.drawString("Press a to switch ON/OFF AI", 11 * gridwidth, 21 * gridwidth);
		// draw the best move predicted by AI
		g.setColor(Color.WHITE);
		if (displayAI) {
			for (int i = 0; i < 4; i++) {
				int realx = gridoffsetx + gridwidth * bestMove[i][0];
				int realy = gridoffsety + gridwidth * bestMove[i][1];
				g.fillRect(realx, realy, gridwidth, gridwidth);
			}
		}

		// draw the level up
		if (levelup) {
			Font font3 = new Font("Times", Font.ITALIC, 90);
			g.setColor(Color.red);
			g.setFont(font3);
			g.drawString("Level Up !!!", 3 * gridwidth, 11 * gridwidth);
			levelup = false;
		}

	}

}
