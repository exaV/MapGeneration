package ch.fhnw.ether.video;

import ch.fhnw.ether.image.Frame;

/**
 * Interface for decoding frames from a video track sequentially from the beginning. This will be very fast for most
 * implementations (e.g. Mac OS X AVFoundation).
 * 
 * @author radar
 *
 */
public interface ISequentialVideoTrack extends IVideoTrack {

	void rewind();

	Frame getNextFrame();

	int loadFrames(int numFrames, int textureId);
}
