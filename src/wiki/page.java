package wiki;

import java.util.ArrayList;
import java.util.List;

public class page {
	int index;
	String title;
	List<Integer> reference;
	boolean visited;
	float rank;
	float present;

	public page(int index, String title) {
		rank = 1000;
		present = 0;
		this.index = index;
		this.title = title;
		reference = new ArrayList<Integer>();
		visited = false;
	}

	public float getRank() {
		return rank;
	}
}