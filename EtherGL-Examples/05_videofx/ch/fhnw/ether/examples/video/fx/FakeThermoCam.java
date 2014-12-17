package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;
import ch.fhnw.util.color.ColorUtilities;


public class FakeThermoCam extends AbstractVideoFX {
	public FakeThermoCam(IVideoFrameSource source) {
		init(FTS_RGBA8_RGB8, source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

			frame.processLines((final ByteBuffer pixels, final int j)->{
				float[] hsb = new float[frame.dimI * 3];
				int pos = pixels.position();
				for(int i = 0; i < frame.dimI; i++) {
					float v = toFloat(pixels.get()) + toFloat(pixels.get()) + toFloat(pixels.get());
					hsb[i*3+0] = v / 3f;
					hsb[i*3+1] = 1f;
					hsb[i*3+2] = 1f;
					if(frame.pixelSize == 4) 
						pixels.get();
				}
				pixels.position(pos);
				ColorUtilities.putRGBfromHSB(pixels, hsb, frame.pixelSize);
			});
		});
		return req;
	}
}
