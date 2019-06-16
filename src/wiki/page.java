package wiki;

import java.util.ArrayList;
import java.util.List;

public class page {
	String title;
	List<Integer> reference;
	public page(String title) {
		this.title = title;
		reference = new ArrayList<Integer>();
	}
}
