package ch.fhnw.ether.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import ch.fhnw.ether.media.AbstractMediaTarget;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.RenderProgram;

public final class JavaSoundTarget extends AbstractMediaTarget<AudioFrame,IAudioRenderTarget> implements IAudioRenderTarget {
	private static final float S2F = Short.MAX_VALUE;

	private AudioFormat    fmt;
	private SourceDataLine out;
	private int            outChannels;
	private int            bytesPerSample;
	private double         sTime;

	public JavaSoundTarget() {
		super(Thread.MAX_PRIORITY);
	}

	@Override
	public void useProgram(RenderProgram<IAudioRenderTarget> program) throws RenderCommandException {
		try {
			if(out != null && out.isOpen())
				stop();
			
			AbstractAudioSource<?> src = (AbstractAudioSource<?>)program.getFrameSource();
			out            = AudioSystem.getSourceDataLine(new AudioFormat(src.getSampleRate(), 16, src.getNumChannels(), true, true));
			fmt            = out.getFormat();
			outChannels    = fmt.getChannels();
			bytesPerSample = fmt.getSampleSizeInBits() / 8;
			out.open(fmt, 2048);
			super.useProgram(program);
		} catch(Throwable t) {
			throw new RenderCommandException(t);
		}
	}

	@Override
	public void render() {

		if(!out.isRunning())
			out.start();

		final float[] samples  = getFrame().samples;
		final int     channels = getFrame().nChannels;

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

		sTime += outBuffer.length / 2;
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
		return sTime / (getSampleRate() * getNumChannels());
	}

	@Override
	public int getNumChannels() {
		return fmt.getChannels();
	}

	@Override
	public float getSampleRate() {
		return fmt.getSampleRate();
	}
}
