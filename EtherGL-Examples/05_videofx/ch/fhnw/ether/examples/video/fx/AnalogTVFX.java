package ch.fhnw.ether.examples.video.fx;

import java.nio.ByteBuffer;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;
import ch.fhnw.util.color.ColorUtils;

public class AnalogTVFX extends AbstractVideoFX {
	private static final int VBLANK = 32;
	
	private static final FXParameter Y  = new FXParameter("y",  "Y Gain",        0, 4, 1);
	private static final FXParameter A  = new FXParameter("a",  "Chroma Gain",   0, 4, 1);
	private static final FXParameter P  = new FXParameter("p",  "Chroma Phase",  0, (float)(2 * Math.PI), 0);
	private static final FXParameter C  = new FXParameter("c",  "Chroma Shift",  0, 32, 0);
	private static final FXParameter HA = new FXParameter("h",  "H-Amplitude",   0, 32, 0);
	private static final FXParameter HF = new FXParameter("hf", "H-Frequency",   1, 100, 1);
	private static final FXParameter HP = new FXParameter("hf", "H-Phase",       0, 2,   0);
	private static final FXParameter HD = new FXParameter("hf", "H-Decay",       0, 1,   0);
	private static final FXParameter V  = new FXParameter("v",  "V-Roll",        0, 64,  0);

	private long      lineCount;
	private float[][] yuvFrame = new float[1][1];
	private int       vOff;

	public AnalogTVFX(IVideoFrameSource source) {
		super(Y, A, P, C, HA, HF, HP, HD, V);
		init(FTS_RGBA8_RGB8, source);
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		processFrames(req, (Frame frame, int frameIdx)->{
			getNextFrame(sources[0], frame);

			final  float  y  = getVal(Y);
			final  float  a  = getVal(A);
			final  float  p  = getVal(P);
			final  int    c  = ((int)getVal(C)) * 3;
			final  double ha = getVal(HA);
			final  double hf = getVal(HF);
			final  float  hd = getVal(HD);
			final  double hp = getVal(HP);
			if(vOff < 0) vOff = 0;
			vOff            += (int)getVal(V);

			if(yuvFrame.length != frame.dimJ + VBLANK || yuvFrame[0].length != frame.dimI * 3)
				yuvFrame = new float[frame.dimJ + VBLANK][frame.dimI * 3];

			frame.processLines((final ByteBuffer pixels, final int j)->{
				final float[] yuv  = yuvFrame[j];
				final int     hoff = 3 * (int)((Math.sin(lineCount++ / hf) + 1.0) * ha + hp * j);      
				ColorUtils.getYUVfromRGB(pixels, yuv, frame.pixelSize);
				for(int i = 3; i < yuv.length; i += 3) {
					final int    idx   = (i + hoff) % yuv.length;
					final int    idxC  = (idx + c) % yuv.length;
					final double ampl  = Math.sqrt((yuv[idxC+1] * yuv[idxC+1] + yuv[idxC+2] * yuv[idxC+2])) * a;
					final double angle = Math.atan2(yuv[idxC+1], yuv[idxC+2]) + p;
					yuv[i+0] = yuv[idx] * y + hd * yuv[i - 3];
					yuv[i+1] = (float) (Math.sin(angle) * ampl);
					yuv[i+2] = (float) (Math.cos(angle) * ampl);
				}
			});

			frame.processLines((final ByteBuffer pixels, final int j)->{
				final float[] yuv  = yuvFrame[(j+vOff) % yuvFrame.length];
				ColorUtils.putRGBfromYUV(pixels, yuv, frame.pixelSize);
			});
		});
		return req;
	}
}
