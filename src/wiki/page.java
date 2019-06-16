package wiki;

import java.util.ArrayList;
import java.util.List;

public class page {
	int index;
	String title;
	List<Integer> reference;
	boolean visited;

	public page(int index, String title) {
		this.index = index;
		this.title = title;
		reference = new ArrayList<Integer>();
		visited = false;
	}
}