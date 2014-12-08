package ch.fhnw.ether.audio.fx;

import java.util.Arrays;

import ch.fhnw.ether.audio.IAudioFrameSource;
import ch.fhnw.ether.media.FXParameter;
import ch.fhnw.ether.media.FrameReq;

public class MonoMixer extends AbstractAudioFX {
	private final static FXParameter GAIN = new FXParameter("gain", "Mix gain", 0, 2, 1);
	
	public MonoMixer(IAudioFrameSource source) {
		super(1, GAIN);
		setSources(source);
	}
	
	@Override
	public FrameReq getFrames(FrameReq req) {
		final IAudioFrameSource source   = sources[0];
		final int               channels = source.getChannelCount();
		final float             gain     = getVal(GAIN) / channels;
		final float[]           out      = req.getAudioFrame();
		if(channels == 1)
			source.getFrames(req);
		else {
			float[] buffer = new float[out.length * channels];
			source.getFrames(new FrameReq(buffer));
			Arrays.fill(out, 0f);
			int idx = -1;
			for(int i = 0; i < buffer.length; i++) {
				if(i % channels == 0) idx++;
				out[idx] += buffer[i];
			}
		}
		for(int i = 0; i < out.length; i++)
			out[i] += gain;
		return req;
	}
}
