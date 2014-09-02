package ch.fhnw.ether.video.jcodec;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jcodec.api.JCodecException;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.video.IRandomAccessVideoTrack;

public final class RandomAccessVideoTrack extends AbstractVideoTrack implements IRandomAccessVideoTrack {
	public RandomAccessVideoTrack(URL url) throws IOException, URISyntaxException, JCodecException {
		super(url);
	}

	@Override
	public Frame getFrame(long frame) {
		try {
			grab.seekToFramePrecise((int) frame);
			return Frame.newFrame(toBufferedImageNoCrop(grab.getNativeFrame()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Frame getFrame(double time) {
		try {
			grab.seekToSecondPrecise(time);
			return Frame.newFrame(toBufferedImageNoCrop(grab.getNativeFrame()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int loadFrame(long frame, int textureId) {
		return -1;
	}

	@Override
	public int loadFrame(double time, int textureId) {
		return -1;
	}
}
