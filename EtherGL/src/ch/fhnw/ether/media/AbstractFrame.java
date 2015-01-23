package ch.fhnw.ether.media;

public abstract class AbstractFrame {
	public static final double ASAP = -1;
	
	public final double playOutTime;

	public AbstractFrame(double playOutTime) {
		this.playOutTime = playOutTime;
	}
}
