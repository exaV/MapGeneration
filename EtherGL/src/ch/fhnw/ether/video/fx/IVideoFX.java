package ch.fhnw.ether.video.fx;

import ch.fhnw.ether.media.IFX;
import ch.fhnw.ether.video.ISequentialFrameSource;
import ch.fhnw.ether.video.IVideoFrameSource;

public interface IVideoFX extends ISequentialFrameSource, IFX {
	IVideoFrameSource[] getSources();
	int                 getNumSources();
}
