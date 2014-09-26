package ch.fhnw.util;

public class Pair<TF, TS> {
	public final TF first;
	public final TS second;
	
	public Pair(TF first, TS second) {
		this.first  = first;
		this.second = second;
	}
	
	public static <TF, TS> Pair<TF, TS> make(TF first, TS second) {
		return new Pair<TF, TS>(first, second);
	}
}
