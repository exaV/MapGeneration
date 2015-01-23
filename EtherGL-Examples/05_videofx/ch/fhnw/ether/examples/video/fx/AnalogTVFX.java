package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.Parameter;
import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.fx.AbstractVideoFX;
import ch.fhnw.util.color.ColorUtilities;

public class AnalogTVFX extends AbstractVideoFX<AnalogTVFX.State> {
	private static final int VBLANK = 32;

	private static final Parameter Y  = new Parameter("y",  "Y Gain",        0, 4, 1);
	private static final Parameter A  = new Parameter("a",  "Chroma Gain",   0, 4, 1);
	private static final Parameter P  = new Parameter("p",  "Chroma Phase",  0, (float)(2 * Math.PI), 0);
	private static final Parameter C  = new Parameter("c",  "Chroma Shift",  0, 32, 0);
	private static final Parameter HA = new Parameter("h",  "H-Amplitude",   0, 32, 0);
	private static final Parameter HF = new Parameter("hf", "H-Frequency",   1, 100, 1);
	private static final Parameter HP = new Parameter("hf", "H-Phase",       0, 2,   0);
	private static final Parameter HD = new Parameter("hf", "H-Decay",       0, 1,   0);
	private static final Parameter V  = new Parameter("v",  "V-Roll",        0, 64,  0);

	public AnalogTVFX() {
		super(Y, A, P, C, HA, HF, HP, HD, V);
	}

	class State extends PerTargetState<IVideoRenderTarget> {
		long      lineCount;
		float[][] yuvFrame = new float[1][1];
		int       vOff;

		public State(IVideoRenderTarget target) {
			super(target);
		}

		protected void processFrame(final double playOutTime, final Frame frame) {
			final  float  y  = getVal(Y);
			final  float  a  = getVal(A);
			final  float  p  = getVal(P);
			final  int    c  = ((int)getVal(C)) * 3;
			final  double ha = getVal(HA);
			final  double hf = getVal(HF);
			final  float  hd = getVal(HD);
			final  double hp = getVal(HP);
			if(vOff < 0) vOff = 0;
			vOff             += (int)getVal(V);

			if(yuvFrame.length != frame.dimJ + VBLANK || yuvFrame[0].length != frame.dimI * 3)
				yuvFrame = new float[frame.dimJ + VBLANK][frame.dimI * 3];

			frame.processLines((final ByteBuffer pixels, final int j)->{
				final float[] yuv  = yuvFrame[j];
				final int     hoff = 3 * (int)((Math.sin(lineCount++ / hf) + 1.0) * ha + hp * j);      
				ColorUtilities.getYUVfromRGB(pixels, yuv, frame.pixelSize);
				for(int i = 3; i < yuv.length; i += 3) {
					final int    idxY   = (i + hoff) % yuv.length;
					final int    idxC   = (idxY + c) % yuv.length;
					final double amplC  = Math.sqrt((yuv[idxC+1] * yuv[idxC+1] + yuv[idxC+2] * yuv[idxC+2])) * a;
					final double phaseC = Math.atan2(yuv[idxC+1], yuv[idxC+2]) + p;
					yuv[i+0] = yuv[idxY] * y + hd * yuv[i - 3];
					yuv[i+1] = (float) (Math.sin(phaseC) * amplC);
					yuv[i+2] = (float) (Math.cos(phaseC) * amplC);
				}
			});

			frame.processLines((final ByteBuffer pixels, final int j)->{
				final float[] yuv  = yuvFrame[(j+vOff) % yuvFrame.length];
				ColorUtilities.putRGBfromYUV(pixels, yuv, frame.pixelSize);
			});
		}
	}

	@Override
	protected State createState(IVideoRenderTarget target) throws RenderCommandException {
		return new State(target);
	}

	@Override
	protected void processFrame(final double playOutTime, final State state, final Frame frame) {
		state.processFrame(playOutTime, frame);
	}
}
