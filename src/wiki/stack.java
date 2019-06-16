package wiki;

public class stack {
	private int stack[];
	private int index;

	public stack() {
		stack = new int[wiki.LEN];
		index = -1;
	}

	public void push(int page) {
		stack[++index] = page;
	}

	public int pop() {
		return stack[index--];
	}

	public int count() {
		return index;
	}
}
