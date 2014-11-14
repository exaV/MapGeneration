package ch.fhnw.ether.video;

public interface IGLRandomAccessVideoTrack extends IRandomAccessVideoTrack {
	void loadFrame(long frame, int textureId);
	void loadFrame(double time, int textureId);
}
