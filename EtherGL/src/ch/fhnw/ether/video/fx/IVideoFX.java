package ch.fhnw.ether.video.fx;

import ch.fhnw.ether.media.IFX;
import ch.fhnw.ether.video.IFrameSource;
import ch.fhnw.ether.video.ISequentialFrameSource;

public interface IVideoFX extends ISequentialFrameSource, IFX {
	IFrameSource[] getSources();
	int            getNumSources();
}
