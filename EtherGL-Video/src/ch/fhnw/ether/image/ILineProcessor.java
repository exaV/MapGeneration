package ch.fhnw.ether.image;

import java.nio.ByteBuffer;

public interface ILineProcessor {
	/**
	 * Called sequentially per line.
	 * 
	 * @param pixels Pixel buffer to operate on. Position is already set for the line.
	 * @param line The line index to operate on.
	 */
	void process(ByteBuffer pixels, int line);
}
