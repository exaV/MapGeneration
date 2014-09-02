package ch.fhnw.ether.video.jcodec;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jcodec.api.JCodecException;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.video.ISequentialVideoTrack;

public final class SequentialVideoTrack extends AbstractVideoTrack implements ISequentialVideoTrack {
	public SequentialVideoTrack(URL url) throws IOException, URISyntaxException, JCodecException {
		super(url);
	}

	@Override
	public void rewind() {
		try {
			grab.seekToFramePrecise(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Frame getNextFrame() {
		try {
			return Frame.newFrame(toBufferedImageNoCrop(grab.getNativeFrame()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int loadFrames(int numFrames, int textureId) {
		return -1;
	}
}
