import java.util.LinkedList;
import java.util.Queue;

/**
 * The Teris AI seraches and predicts the best possible move of the next block
 * based on heuristic function.
 * 
 * @author Jerry
 *
 */
public class TerisAI {

	private TerisPanel panel;
	private int[][] map;// current Gamemap in the game
	private block curr;// current block in the game
	int[] heightlist;// list of the maximum heights
	private int btype;

	// constructor
	public TerisAI(TerisPanel p) {
		panel = p;
		heightlist = new int[10];
	}

	// update the current map and block
	private void update() {
		map = panel.getCurrMap();
		curr = panel.getCurrblock();
		btype = curr.gettype();
	}

	// fill the block into current map
	private void fill(int[][] list) {
		for (int i = 0; i < 4; i++) {
			int x = list[i][0];
			int y = list[i][1];
			map[y][x] = 0;
		}
	}

	// remove the block into the current map
	private void clean(int[][] list) {
		for (int i = 0; i < 4; i++) {
			int x = list[i][0];
			int y = list[i][1];
			map[y][x] = -1;
		}
	}

	// Determine if the block is already fixed
	public boolean isFixed(int[][] list) {

		// check if there is a block under the current block
		for (int i = 0; i < 4; i++) {
			int x = list[i][0];
			int y = list[i][1];
			if (y == 19) {
				return true;
			}
			if (map[y + 1][x] != -1) {
				return true;
			}

		}
		return false;
	}

	// Determines if the block is able to move right
	public boolean canmoveRight(int[][] coord) {
		// for each block, check on the gameMap if it is already occupied or
		// it
		// reaches the boundary
		for (int i = 0; i < 4; i++) {
			int x = coord[i][0] + 1;
			int y = coord[i][1];
			if (x > 9) {
				return false;
			}
			if (map[y][x] != -1) {
				return false;
			}

		}
		return true;
	}

	// Determines if the block is able to move left
	public boolean canmoveLeft(int[][] coord) {
		// for each block, check on the gameMap if it is already occupied or
		// it
		// reaches the boundary
		for (int i = 0; i < 4; i++) {
			int x = coord[i][0] - 1;
			int y = coord[i][1];
			if (x < 0) {
				return false;
			}
			if (map[y][x] != -1) {
				return false;
			}

		}
		return true;
	}

	// Determines if the block is able to move down
	public boolean canmoveDown(int[][] coord) {
		// TODO Auto-generated method stub
		if (!isFixed(coord)) {
			return true;
		}
		return false;
	}

	// calculate the coordinates of the block
	private int[][] calCoord(block b) {
		// calculate the current block coordinates
		int[] curr = b.getshape();
		int count = 0;
		int coord[][] = new int[4][2];
		for (int i = 0; i < curr.length; i++) {
			if (curr[i] == 1) {
				int factor = i / 4;
				int remainer = i % 4;
				coord[count][0] = b.getX() + remainer;
				coord[count][1] = b.getY() + factor;
				count++;
			}
		}
		return coord;
	}

	// predicts the next optimal location of the block
	public int[][] predict() {
		update();
		// determine the iteration cycle
		int ite = 2;

		if (btype == 3 || btype == 5 || btype == 6) {
			ite = 4;
		}

		if (btype == 4) {
			ite = 1;
		}

		double bestscore = Integer.MIN_VALUE;
		int[][] bestLoc = new int[4][2];
		double score = 0;
		for (int i = 0; i < ite; i++) {
			// start bfs to search for best location for current shape
			Queue<block> q = new LinkedList<block>();
			int[][] visited = new int[28][18];
			q.add(curr);
			visited[curr.getY() + 4][curr.getX() + 4] = 1;
			while (!q.isEmpty()) {
				// System.out.println("mm");
				block currb = (block) q.remove();
				int[][] tempcoord = calCoord(currb);

				// calculated and update the score if necessary
				if (isFixed(tempcoord)) {
					fill(tempcoord);
					score = calScore();
					// System.out.println(score);
					if (score > bestscore) {
						bestscore = score;
						bestLoc = tempcoord;

					}
					clean(tempcoord);
				}
				// search for three possible expansions

				// below
				if (canmoveDown(tempcoord)) {
					// print(tempcoord);
					if (visited[currb.getY() + 1 + 4][currb.getX() + 4] != 1) {
						q.add(new block(currb.getX(), currb.getY() + 1, currb.gettype(), currb.getrotation()));
						visited[currb.getY() + 1 + 4][currb.getX() + 4] = 1;
						// System.out.println("bian" +currb.getY());
					}
				}
				// left
				if (canmoveLeft(tempcoord)) {
					if (visited[currb.getY() + 4][currb.getX() - 1 + 4] != 1) {
						q.add(new block(currb.getX() - 1, currb.getY(), currb.gettype(), currb.getrotation()));
						visited[currb.getY() + 4][currb.getX() - 1 + 4] = 1;
					}
				}
				// right
				if (canmoveRight(tempcoord)) {
					if (visited[currb.getY() + 4][currb.getX() + 1 + 4] != 1) {
						q.add(new block(currb.getX() + 1, currb.getY(), currb.gettype(), currb.getrotation()));
						visited[currb.getY() + 4][currb.getX() + 1 + 4] = 1;
					}
				}

			}
			// rotate the block
			curr.rotateCCW();
		}
		return bestLoc;
	}

	// calculate score
	private double calScore() {
		double a = -0.510066;
		double b = 0.760666;
		double c = -0.35663;
		double d = -0.184483;
		calHeights();
		return a * calAggreHeight() + b * calCompleteLine() + c * calHoles() + d * calBumpiness();
	}

	// calculate the aggregate height of the current map
	private double calAggreHeight() {
		double sum = 0;
		for (int i = 0; i < 10; i++) {
			sum += heightlist[i];
		}
		return sum;

	}

	// calculate the complete lines of the current map
	private double calCompleteLine() {
		double sum = 0;
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 10; j++) {
				if (map[i][j] == -1) {
					break;
				}
				if (j == 9) {
					sum += 1;
				}
			}
		}
		return sum;
	}

	private double calHoles() {
		double sum = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 19; j > 0; j--) {
				// reach maximum height
				if (j == heightlist[i]) {
					break;
				}
				// if a hole is found
				if (map[j][i] == -1 && map[j - 1][i] != -1) {
					sum++;
				}
			}
		}
		return sum;

	}

	private double calBumpiness() {
		double sum = 0;
		for (int i = 0; i < 9; i++) {
			sum += Math.abs(heightlist[i] - heightlist[i + 1]);
		}
		return sum;

	}

	private void calHeights() {
		for (int i = 0; i < 10; i++) {
			int track = 0;
			while (map[track][i] == -1) {
				if (track >= 19) {
					track = 20;
					break;
				} else {
					track++;
				}
			}
			heightlist[i] = 20 - track;
		}
	}

	private void print(int[][] li) {
		for (int i = 0; i < 4; i++) {
			System.out.println(li[i][0] + " " + li[i][1]);
		}
	}
}
