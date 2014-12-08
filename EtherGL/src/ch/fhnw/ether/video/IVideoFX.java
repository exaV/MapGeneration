package ch.fhnw.ether.video;

import ch.fhnw.ether.media.IFX;
import ch.fhnw.ether.media.IFrameSource;

public interface IVideoFX extends ISequentialFrameSource, IFX {
	IFrameSource[] getSources();
	void          setSources(IFrameSource[] sources);
	int           getNumSources();
}
