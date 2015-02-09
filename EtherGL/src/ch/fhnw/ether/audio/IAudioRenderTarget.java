package ch.fhnw.ether.audio;

import ch.fhnw.ether.media.IRenderTarget;

public interface IAudioRenderTarget extends IRenderTarget {
	void       setFrame(AudioFrame frame);
	AudioFrame getFrame();
	int        getNumChannels();
	float      getSampleRate();
}
