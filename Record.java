
public class Record implements Comparable<Record> {
	private String player;
	private int score;
	private String time;

	Record(String p, int s, String t) {
		player = p;
		score = s;
		time = t;
	}

	public String toString() {
		return player + " " + score + " " + time;
	}

	@Override
	public int compareTo(Record o) {
		if (this.score > o.score) {
			return 1;
		}
		if (this.score < o.score)
			;
		return 0;
	}

}
