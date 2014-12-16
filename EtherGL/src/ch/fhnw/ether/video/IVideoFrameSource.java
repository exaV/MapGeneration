package ch.fhnw.ether.video;

import java.util.Set;

import ch.fhnw.ether.image.FloatFrame;
import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.Grey16Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.FrameException;
import ch.fhnw.ether.media.IFrameSource;

public interface IVideoFrameSource extends IFrameSource {
	static final Class<? extends Frame>[] FTS_RGB8   = cast(RGB8Frame.class); 
	static final Class<? extends Frame>[] FTS_RGBA8  = cast(RGBA8Frame.class); 
	static final Class<? extends Frame>[] FTS_FLOAT  = cast(FloatFrame.class); 
	static final Class<? extends Frame>[] FTS_GREY16 = cast(Grey16Frame.class); 


	static final Class<? extends Frame>[] FTS_RGB8_RGBA8 = cast(RGB8Frame.class,  RGBA8Frame.class); 
	static final Class<? extends Frame>[] FTS_RGBA8_RGB8 = cast(RGBA8Frame.class, RGB8Frame.class); 

	static final Class<? extends Frame>[] FTS_RGBA8_RGB8_GREY16_FLOAT = cast(RGBA8Frame.class, RGB8Frame.class, Grey16Frame.class, FloatFrame.class); 

	int                      getWidth();
	int                      getHeight();
	Class<? extends Frame>[] getFrameTypes();

	@SuppressWarnings("unchecked")
	static Class<? extends Frame>[] cast(Class<?> ... types) {
		return (Class<? extends Frame>[]) types;
	}
	
	void setPreferredFrameTypes(Set<Class<? extends Frame>> frameTypes);
	
	static void updatePreferredFrameTypes(Set<Class<? extends Frame>> toUpdate, Set<Class<? extends Frame>> update) {
		toUpdate.retainAll(update);
		if(toUpdate.isEmpty())
			throw new FrameException("No compatible frame types found");
	}
}
