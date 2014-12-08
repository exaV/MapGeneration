package ch.fhnw.ether.audio.fx;

import ch.fhnw.ether.audio.IAudioFrameSource;
import ch.fhnw.ether.media.IFX;
import ch.fhnw.ether.audio.ISequentialFrameSource;

public interface IAudioFX extends ISequentialFrameSource, IFX {
	IAudioFrameSource[] getSources();
	int                 getNumSources();
}
