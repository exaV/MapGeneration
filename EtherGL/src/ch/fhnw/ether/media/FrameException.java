package ch.fhnw.ether.media;

public class FrameException extends RuntimeException {
	private static final long serialVersionUID = 2639566078598593315L;

	public FrameException(String msg, Throwable t) {
		super(msg, t);
	}

	public FrameException(String msg) {
		super(msg);
	}

}
