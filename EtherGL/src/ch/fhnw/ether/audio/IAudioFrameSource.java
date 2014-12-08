package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.IFrameSource;

public interface IAudioFrameSource extends IFrameSource {
	int getChannelCount();
}
