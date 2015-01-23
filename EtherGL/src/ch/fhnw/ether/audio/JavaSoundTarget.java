package ch.fhnw.ether.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ch.fhnw.ether.media.AbstractMediaTarget;

public class JavaSoundTarget extends AbstractMediaTarget<AudioFrame,IAudioRenderTarget> implements IAudioRenderTarget {
	private static final float S2F = Short.MAX_VALUE;

	private final AudioFormat    fmt;
	private final SourceDataLine out;
	private final int            outChannels;
	private final int            bytesPerSample;
	private       double         time;
	
	public JavaSoundTarget(float sampleRate) throws LineUnavailableException {
		this(AudioSystem.getSourceDataLine(new AudioFormat(sampleRate, 16, 2, true, true)));
	}

	public JavaSoundTarget(SourceDataLine out) throws LineUnavailableException {
		super(Thread.MAX_PRIORITY);
		this.fmt            = out.getFormat();
		this.outChannels    = fmt.getChannels();
		this.bytesPerSample = fmt.getSampleSizeInBits() / 8;
		this.out            = out;
		this.out.open(fmt, 2048);
	}

	@Override
	public void render() {
		if(!out.isRunning())
			out.start();
		
		final float[] samples  = getFrame().samples;
		final int     channels = getFrame().channels;
		
		final byte[] outBuffer   = new byte[(samples.length / channels) * outChannels * bytesPerSample];   
		int          outIdx      = 0;
		
		for(int i = 0; i < samples.length; i += channels) {
			for(int j = 0; j < outChannels; j++) {
				if(j < channels) {
					int s  = (int) (samples[i+j] * S2F);
					if(s > Short.MAX_VALUE) s = Short.MAX_VALUE;
					if(s < Short.MIN_VALUE) s = Short.MIN_VALUE;
					outBuffer[outIdx++] = (byte) (s >> 8);
					outBuffer[outIdx++] = (byte) s;
				} else {
					outBuffer[outIdx++] = 0;
					outBuffer[outIdx++] = 0;
				}
			}
		}
				
		time += outBuffer.length / (2 * channels);
		out.write(outBuffer, 0, outBuffer.length);
	}

	@Override
	public void stop() {
		super.stop();
		out.drain();
		out.flush();
		out.close();
	}
	
	@Override
	public double getTime() {
		return time / fmt.getSampleRate();
	}
}
