package ch.fhnw.ether.media;

public class EndOfSourceException extends RenderCommandException {
	private static final long serialVersionUID = 2408457450866756324L;
	
	public EndOfSourceException(AbstractFrameSource<?,?> source) {
		super(source.toString() + " ended.");
	}
}
