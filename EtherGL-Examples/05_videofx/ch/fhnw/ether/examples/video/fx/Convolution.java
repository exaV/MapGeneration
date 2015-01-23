package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;
import ch.fhnw.util.math.Mat3;

public class Convolution extends AbstractVideoFX<Convolution.State> {
	private static final Parameter KERNEL = new Parameter("kernel", "Effect", 0, 
			"Identity", 
			"Edge Detection1", 
			"Edge Detection2", 
			"Emboss",
			"Sharpen", 
			"Box Blur", 
			"Gaussian Blur");

	private static final Mat3[] KERNELS = {
		new Mat3( 0, 0, 0,   0, 1, 0,   0, 0, 0),
		new Mat3( 1, 0,-1,   0, 0, 0,  -1, 0, 1),
		new Mat3( 0, 1, 0,   1,-4, 1,   0, 1, 0),
		new Mat3( 4, 0, 0,   0, 0, 0,   0, 0,-4),
		new Mat3( 0,-1, 0,  -1, 5,-1,   0,-1, 0),
		normalize(new Mat3( 1, 1, 1,   1, 1, 1,   1, 1, 1)),
		normalize(new Mat3( 1, 2, 1,   2, 4, 2,   1, 2, 1)),
	};

	private static final boolean[] GREYSCALE = {
		false,
		true,
		true,
		false,
		false,
		false,
		false,
	};

	public Convolution() {
		super(KERNEL);
	}

	private static Mat3 normalize(Mat3 mat3) {
		float s = 0;
		s += Math.abs(mat3.m00);
		s += Math.abs(mat3.m10);
		s += Math.abs(mat3.m20);

		s += Math.abs(mat3.m01);
		s += Math.abs(mat3.m11);
		s += Math.abs(mat3.m22);

		s += Math.abs(mat3.m02);
		s += Math.abs(mat3.m12);
		s += Math.abs(mat3.m22);

		s = 1 / s;

		return new Mat3(
				s * mat3.m00,
				s * mat3.m10,
				s * mat3.m20,

				s * mat3.m01,
				s * mat3.m11,
				s * mat3.m21,

				s * mat3.m02,
				s * mat3.m12,
				s * mat3.m22
				);
	}

	class State extends PerTargetState<IVideoRenderTarget> {
		private float[][] outFrame = new float[1][1];

		public State(IVideoRenderTarget target) {
			super(target);
		}
		
		protected void processFrame(double playOutTime, Frame frame) {
			if(frame.dimJ != outFrame.length || frame.dimI != outFrame[0].length * 3)
				outFrame = new float[frame.dimJ][frame.dimI * 3];

			Mat3    kernel    = KERNELS[(int) getVal(KERNEL)];
			boolean greyscale = GREYSCALE[(int) getVal(KERNEL)]; 

			for(int j = frame.dimJ - 1; --j >= 1;) {
				int idx = 0;
				if(greyscale) {
					for(int i = 1; i< frame.dimI - 1; i++) {
						float val = convolute(frame, i, j, kernel, 0) + convolute(frame, i, j, kernel, 1) + convolute(frame, i, j, kernel, 2); 
						outFrame[j][idx++] = val; 
						outFrame[j][idx++] = val; 
						outFrame[j][idx++] = val; 
					}
				} else {
					for(int i = 1; i< frame.dimI - 1; i++) {
						outFrame[j][idx++] = convolute(frame, i, j, kernel, 0); 
						outFrame[j][idx++] = convolute(frame, i, j, kernel, 1); 
						outFrame[j][idx++] = convolute(frame, i, j, kernel, 2); 
					}
				}
			}

			if(frame.pixelSize == 4) {
				frame.processLines((ByteBuffer pixels, int j) -> {
					int idx = 0;
					for(int i = frame.dimI; --i >= 0;) {
						pixels.put(toByte(outFrame[j][idx++]));
						pixels.put(toByte(outFrame[j][idx++]));
						pixels.put(toByte(outFrame[j][idx++]));
						pixels.put(Frame.B255);
					}
				});
			} else {
				frame.processLines((ByteBuffer pixels, int j) -> {
					int idx = 0;
					for(int i = frame.dimI; --i >= 0;) {
						pixels.put(toByte(outFrame[j][idx++]));
						pixels.put(toByte(outFrame[j][idx++]));
						pixels.put(toByte(outFrame[j][idx++]));
					}
				});
			}
		}

		private float convolute(Frame frame, int i, int j, Mat3 kernel, int c) {
			return
					frame.getFloatComponent(i-1, j-1, c) * kernel.m00 +
					frame.getFloatComponent(i-1, j,   c) * kernel.m10 +
					frame.getFloatComponent(i-1, j+1, c) * kernel.m20 +

					frame.getFloatComponent(i,   j-1, c) * kernel.m01 +
					frame.getFloatComponent(i,   j,   c) * kernel.m11 +
					frame.getFloatComponent(i,   j+1, c) * kernel.m21 +

					frame.getFloatComponent(i+1, j-1, c) * kernel.m02 +
					frame.getFloatComponent(i+1, j,   c) * kernel.m12 +
					frame.getFloatComponent(i+1, j+1, c) * kernel.m22;
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
