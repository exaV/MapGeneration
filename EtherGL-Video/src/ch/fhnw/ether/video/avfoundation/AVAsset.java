/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.video.avfoundation;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class AVAsset {
	static {
		// System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary("etherglvideo");
	}

	private long nativeHandle;

	public AVAsset(URL url) {
		nativeHandle = nativeCreate(url.toString());
		if (nativeHandle == 0)
			throw new IllegalArgumentException("cannot create avasset from " + url);
	}

	public void dispose() {
		nativeDispose(nativeHandle);
	}

	public void rewind() {
		nativeRewind(nativeHandle);
	}

	public int[] getFrame(double time) {
		return nativeGetFrame(nativeHandle, time);
	}

	public int[] getNextFrame() {
		return nativeGetNextFrame(nativeHandle);
	}

	public int getWidth() {
		return nativeGetWidth(nativeHandle);
	}

	public int getHeight() {
		return nativeGetHeight(nativeHandle);
	}

	public double getDuration() {
		return nativeGetDuration(nativeHandle);
	}

	public double getFrameRate() {
		return nativeGetFrameRate(nativeHandle);
	}

	public int getFrameCount() {
		return nativeGetFrameCount(nativeHandle);
	}

	private native long nativeCreate(String url);

	private native void nativeDispose(long nativeHandle);

	private native void nativeRewind(long nativeHandle);

	private native int[] nativeGetFrame(long nativeHandle, double time);

	private native int[] nativeGetNextFrame(long nativeHandle);

	private native int nativeGetWidth(long nativeHandle);

	private native int nativeGetHeight(long nativeHandle);

	private native double nativeGetDuration(long nativeHandle);

	private native double nativeGetFrameRate(long nativeHandle);

	private native int nativeGetFrameCount(long nativeHandle);

	public static void main(String[] args) {
		// Make sure everything runs on GUI thread...
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AVAsset(args);
			}
		});
	}

	public AVAsset(String[] args) {
		try {
			AVAsset asset = new AVAsset(new URL("file:///Users/radar/Desktop/movies/calvary_(2014).mp4"));
			int[] image = asset.getFrame(300.0);
			System.out.println(image.length + " " + asset.getWidth() + " " + asset.getHeight());

			/*
			 * for (int i = 0; i < 600; ++i) { image = asset.getNextFrame();
			 * System.out.println(i + " " + image.length); }
			 */

			BufferedImage bi = new BufferedImage(asset.getWidth(), asset.getHeight(), BufferedImage.TYPE_INT_ARGB);
			int[] imgData = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
			System.arraycopy(image, 0, imgData, 0, image.length);

			try {
				File outputfile = new File("/Users/radar/Desktop/saved.png");
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
			}
			asset.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
