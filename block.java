import java.awt.Color;
import java.util.Random;

/**
 * Block represents each shape in each iteration.
 * 
 * @author Jerry
 *
 */
public class block {
	// different shapes of the block
	public final int shapelist[][][] = new int[][][] {
			// I shape
			{ { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
					{ 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 } },
			// reverse Z shape
			{ { 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 } },
			// Z shape
			{ { 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 } },
			// J shape
			{ { 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
			// square shape
			{ { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
			// L shape
			{ { 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 }, { 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
					{ 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
			// T shape
			{ { 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
					{ 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
					{ 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 } },

	};

	private int x;
	int y;// coordination
	private int type;// the type of the block
	private int rotation;// the rotation state of the type
	private int[] shape;// the current shape
	private int colorindex;// the color of the block

	public block() {
		this.x = 4;
		this.y = 0;
		Random ran = new Random();
		this.type = ran.nextInt(7);
		this.rotation = ran.nextInt(4);
		this.shape = shapelist[type][rotation];
		this.colorindex = ran.nextInt(5);

	}

	public block(int x, int y, int type, int rotation) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.rotation = rotation;
		this.shape = shapelist[type][rotation];
		this.colorindex = 0;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int gettype() {
		return type;
	}

	public void setX(int newx) {
		x = newx;
	}

	public void setY(int newy) {
		y = newy;
	}

	public int getrotation() {
		return rotation;
	}

	// rotation the block counterclockwise
	public void rotateCCW() {
		boolean check = true;

		// check if there is enough room for rotation
		rotation = (rotation + 1) % 4;
		int[] temp = shapelist[type][rotation];
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == 1) {
				int factor = i / 4;
				int remainer = i % 4;
				int tempx = x + remainer;
				int tempy = y + factor;
				if (tempx < 0 || tempx > 9 || tempy < 0 || tempy > 19) {
					check = false;
				}
			}
		}
		if (check) {
			updateShape();
		} else {
			rotation = (rotation - 1) % 4;
		}
	}

	// update the shape after rotation
	public void updateShape() {
		shape = shapelist[type][rotation];
	}

	// return the current shape
	public int[] getshape() {
		return shape;
	}

	// return the shape after rotation
	public int[] getrotateShape() {
		int temp = (rotation + 1) % 4;
		return shapelist[type][temp];
	}

	// return the current index
	public int getColorindex() {
		return colorindex;
	}

}
