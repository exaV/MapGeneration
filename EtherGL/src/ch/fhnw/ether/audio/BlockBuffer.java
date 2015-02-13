package ch.fhnw.ether.audio;

import java.util.Arrays;
import java.util.LinkedList;

import ch.fhnw.ether.audio.AudioUtilities.Window;

public final class BlockBuffer {
	private LinkedList<float[]> blocks = new LinkedList<>();
	private float[]             c0;
	private int                 s0;
	private float[]             c1;
	private int                 s1;
	private final Window        windowType;

	public BlockBuffer(int blockSize, boolean halfOverlap, Window windowType) {
		this.windowType = windowType;
		this.c0         = new float[blockSize];
		this.c1         = new float[blockSize];
		if(halfOverlap) {
			this.c1 = new float[blockSize];
			this.s1 = blockSize / 2;
		}
	}

	public void add(float[] data) {
		for(int i = 0; i < data.length; i++) {
			if(s0 == c0.length) {
				push(c0);
				c0 = new float[c0.length];
				s0 = 0;
			}
			c0[s0++] = data[i];
			if(s1 == c1.length) {
				push(c1);
				c1 = new float[c1.length];
				s1 = 0;
			}
			c1[s1++] = data[i];
		}
	}

	private void push(float[] block) {
		AudioUtilities.applyWindow(windowType, 1, block);
		blocks.add(block);
	}

	public float[] nextBlock() {
		return blocks.isEmpty() ? null : blocks.remove(); 
	}

	public boolean nextBlockComplex(float[] block) {
		float[] b = nextBlock();
		if(b == null) return false;

		Arrays.fill(block, 0f);
		for(int i = 0; i < b.length; i++)
			block[i*2] = b[i];
		return true;
	}
}
