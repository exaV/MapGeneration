package ch.fhnw.ether.audio;

public class NullAudioTarget extends AbstractAudioTarget {
	private final int   numChannels;
	private final float sRate;
	private double      sTime;

	public NullAudioTarget(int numChannels, float sampleRate) {
		super(Thread.NORM_PRIORITY, false);
		this.numChannels = numChannels;
		this.sRate       = sampleRate;
	}

	@Override
	public void render() {
		sTime += getFrame().samples.length;
	}

	@Override
	public double getTime() {
		if(timebase != null) return timebase.getTime();
		return sTime / (getSampleRate() * getNumChannels());
	}

	@Override
	public int getNumChannels() {
		return numChannels;
	}

	@Override
	public float getSampleRate() {
		return sRate;
	}	
}
