package ch.fhnw.ether.audio;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ch.fhnw.ether.media.AbstractMediaTarget;

public class AudioBufferTarget extends AbstractMediaTarget<AudioFrame,IAudioRenderTarget> implements IAudioRenderTarget {
	public enum Window {RECTANGLE, HANN, HAMMING}

	private static final double PI2 = Math.PI * 2;
	
	float[]                      block;
	int                          iwptr;
	int                          irptr;
	int                          owptr;
	int                          orptr;
	final float[]                in;
	float[]                      out;
	final int                    hopSize;
	final float                  gain;
	final int                    blockSize;
	final BlockingQueue<float[]> blocks = new LinkedBlockingQueue<>();
	final Window                 windowType;

	public AudioBufferTarget(int blockSize, int hopSize, Window windowType) {
		super(Thread.MAX_PRIORITY);
		this.blockSize  = blockSize;
		this.block      = new float[blockSize];
		this.in         = new float[blockSize * 2];
		this.out        = new float[blockSize * 2];
		this.hopSize    = hopSize;
		this.owptr      = blockSize;
		this.gain       = hopSize / (float)blockSize;
		this.windowType = windowType;
	}

	@Override
	public void render() {
		final float[] samples     = getFrame().samples;
		final int     nChannels = getFrame().channels;
		
		if(samples.length >= out.length)
			out = Arrays.copyOf(out, samples.length);
		for(int i = 0; i < samples.length; i++) {
			in[iwptr++ % in.length] = samples[i];
			if(iwptr - irptr == block.length) {
				int nrptr = irptr + hopSize;
				for(int j = 0; j < block.length; j++)
					block[j] = in[irptr++ % in.length];
				irptr = nrptr;

				applyWindow(nChannels, block);
				
				blocks.add(block);
				block = new float[blockSize];

				int nwptr = owptr + hopSize;
				for(int j = 0; j < block.length; j++)
					out[owptr++ % out.length] += block[j] * gain;
				owptr = nwptr;
			}
		}
	}

	private void applyWindow(int nChannels, float[] data) {
		int nSamples1 = (data.length / nChannels) - 1;
		switch(windowType){
		case HANN:{
			for(int i = 0; i < data.length; i++) {
				int x = i / nChannels;
				data[i] = data[i] * (float)(1.0 - Math.cos(x*PI2/nSamples1));
			}
			break;
		}
		case HAMMING:{
			for(int i = 0; i < data.length; i++) {
				int x = i / nChannels;
				data[i] = data[i] * (float)(0.53836 - 0.46164 * Math.cos(x*PI2/nSamples1));
			}
			break;
		}
		case RECTANGLE: 
			break;
		}
	}

	@Override
	public boolean isRendering() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
