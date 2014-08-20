package ch.fhnw.util;

public class Pair<TL, TR> {
	public final TL left;
	public final TR right;
	
	public Pair(TL left, TR right) {
		this.left  = left;
		this.right = right;
	}
}
