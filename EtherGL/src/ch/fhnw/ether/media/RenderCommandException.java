package ch.fhnw.ether.media;

public class RenderCommandException extends Exception {
	private static final long serialVersionUID = 6892481928277819888L;

	public RenderCommandException(Throwable t) {
		super(t);
	}
	
	public RenderCommandException(String msg) {
		super(msg);
	}
}
