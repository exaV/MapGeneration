package ch.fhnw.ether.audio;

import ch.fhnw.util.math.MathUtilities;

public class Smooth {
	private final float[] values;
	private final double  decay;
	private double        lastUpdate = -1;
	private double        max;
	
	public Smooth(int nChannels, float decay) {
		this.values = new float[nChannels];
		this.decay  = decay;
	}

	public void update(double time, float ... values) {
		max *= 0.99;
		for(float v : values)
			max = Math.max(max, v);
		
		if(lastUpdate > 0) {
			float gain = (float)Math.pow(decay, time - lastUpdate);
			for(int band = 0; band < values.length; band++)
				this.values[band] = (float) MathUtilities.clamp(Math.max(this.values[band] * gain, values[band]) / max, 0, 1);
			lastUpdate = time;
		} else {
			for(int band = 0; band < values.length; band++)
				this.values[band] = (float) MathUtilities.clamp(this.values[band] / max, 0, 1);
			lastUpdate = time;
		}
	}

	public float get(int band) {
		return values[band];
	}

	public int size() {
		return values.length;
	}
}
