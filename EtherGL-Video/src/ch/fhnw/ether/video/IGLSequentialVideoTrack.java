package ch.fhnw.ether.video;

public interface IGLSequentialVideoTrack extends ISequentialVideoTrack {
	int loadFrames(int numFrames, int textureId);
}
