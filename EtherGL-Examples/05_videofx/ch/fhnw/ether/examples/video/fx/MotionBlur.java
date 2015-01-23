package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class MotionBlur extends AbstractVideoFX<MotionBlur.State> {
	private static final Parameter DECAY = new Parameter("decay", "Decay", 0.01f, 1f, 1f);

	protected MotionBlur() {
		super(DECAY);
	}

	class State extends PerTargetState<IVideoRenderTarget> {
		private float[][] buffer  = new float[1][1];
		
		public State(IVideoRenderTarget target) {
			super(target);
		}

		protected void processFrame(double playOutTime, Frame frame) {
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
		}
	}
	
	@Override
	protected State createState(IVideoRenderTarget target) throws RenderCommandException {
		return new State(target);
	}
	
	@Override
	protected void processFrame(double playOutTime, State state, Frame frame) {
		state.processFrame(playOutTime, frame);
	}
}
