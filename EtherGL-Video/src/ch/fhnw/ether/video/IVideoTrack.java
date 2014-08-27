package ch.fhnw.ether.video;

/**
 * Bare-bones video track interface: Video containing one track, audio is ignored. Main purpose is to decode and fetch
 * video frames.
 * 
 * @author radar
 *
 */
public interface IVideoTrack {

	void dispose();

	double getDuration();

	double getFrameRate();

	int getFrameCount();

	int getWidth();

	int getHeight();

	void rewind();

	Frame getFrame(double time);

	Frame getNextFrame();

	void loadFrame(double time, int textureId);

	void loadFrames(int numFrames, int textureId);
}
