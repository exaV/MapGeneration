package ch.fhnw.ether.audio;

public interface IAudioSource {
	float  getSampleRate();
	int    getNumChannels();
	long   getLengthInFrames();	
	double getLengthInSeconds();
	float  getFrameRate();

	default AudioFrame createAudioFrame(long sTime, int frameSize) {
		return createAudioFrame(sTime, new float[frameSize]);
	}

	default AudioFrame createAudioFrame(long sTime, float[] data) {
		return new AudioFrame(sTime, getNumChannels(), getSampleRate(), data);
	}
	
}
