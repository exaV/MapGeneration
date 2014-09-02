package ch.fhnw.ether.video;

import ch.fhnw.ether.image.Frame;

/**
 * Interface for decoding frames from a video track in random-access mode. Note this might be slow and should mainly be
 * used for obtaining individual frames, e.g. for film-strip images or previews.
 * 
 * @author radar
 *
 */
public interface IRandomAccessVideoTrack extends IVideoTrack {

	Frame getFrame(long frame);

	Frame getFrame(double time);

	int loadFrame(long frame, int textureId);

	int loadFrame(double time, int textureId);

}
