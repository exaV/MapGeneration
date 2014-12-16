package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class MotionBlur extends AbstractVideoFX {
	private static final FXParameter DECAY = new FXParameter("decay", "Decay", 0.01f, 1f, 1f);
	
	private float[][] buffer  = new float[1][1];
	
	protected MotionBlur(IVideoFrameSource source) {
		super(DECAY);
		init(FTS_RGBA8_RGB8, source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);
			
			if(buffer[0].length != frame.dimI *3 || buffer.length != frame.dimJ)
				buffer  = new float[frame.dimJ][frame.dimI * 3];
			
			float decay = getVal(DECAY);
			
			frame.processLines((ByteBuffer pixels, int j) -> {
				int           idx     = 0;
				final float[] bufferJ = buffer[j];
				for(int i = frame.dimI; --i >= 0;) {
					frame.position(pixels, i, j);
					
					float r = toFloat(pixels.get());
					float g = toFloat(pixels.get());
					float b = toFloat(pixels.get());
															
					bufferJ[idx] = mix(r, bufferJ[idx], decay); idx++;
					bufferJ[idx] = mix(g, bufferJ[idx], decay); idx++;
					bufferJ[idx] = mix(b, bufferJ[idx], decay);

					idx -= 2;

					frame.position(pixels, i, j);					
					pixels.put(toByte(bufferJ[idx++]));
					pixels.put(toByte(bufferJ[idx++]));
					pixels.put(toByte(bufferJ[idx++]));
										
				}
			});
		});
		return req;
	}

}
