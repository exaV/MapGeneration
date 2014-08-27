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
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ch.fhnw.ether.video.Frame;
import ch.fhnw.ether.video.IVideoTrack;
import ch.fhnw.ether.video.RGBA8Frame;

public class AVAsset implements IVideoTrack {
	static {
		System.loadLibrary("etherglvideo");
	}

	private long nativeHandle;

	private double duration;
	private double frameRate;
	private int frameCount;
	private int width;
	private int height;

	public AVAsset(URL url) {
		nativeHandle = nativeCreate(url.toString());
		if (nativeHandle == 0)
			throw new IllegalArgumentException("cannot create avasset from " + url);
		duration = nativeGetDuration(nativeHandle);
		frameRate = nativeGetFrameRate(nativeHandle);
		frameCount = nativeGetFrameCount(nativeHandle);
		width = nativeGetWidth(nativeHandle);
		height = nativeGetHeight(nativeHandle);
	}

	@Override
	public void dispose() {
		nativeDispose(nativeHandle);
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public double getFrameRate() {
		return frameRate;
	}

	@Override
	public int getFrameCount() {
		return frameCount;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void rewind() {
		nativeRewind(nativeHandle);
	}

	@Override
	public Frame getFrame(double time) {
		return new RGBA8Frame(getWidth(), getHeight(), 1, nativeGetFrame(nativeHandle, time));
	}

	@Override
	public Frame getNextFrame() {
		return new RGBA8Frame(getWidth(), getHeight(), 1, nativeGetNextFrame(nativeHandle));
	}

	@Override
	public void loadFrame(double time, int textureId) {
		nativeLoadFrame(nativeHandle, time, textureId);
	}

	@Override
	public void loadFrames(int numFrames, int textureId) {
		nativeLoadFrames(nativeHandle, numFrames, textureId);
	}

	private native long nativeCreate(String url);

	private native void nativeDispose(long nativeHandle);

	private native double nativeGetDuration(long nativeHandle);

	private native double nativeGetFrameRate(long nativeHandle);

	private native int nativeGetFrameCount(long nativeHandle);

	private native int nativeGetWidth(long nativeHandle);

	private native int nativeGetHeight(long nativeHandle);

	private native void nativeRewind(long nativeHandle);

	private native byte[] nativeGetFrame(long nativeHandle, double time);

	private native byte[] nativeGetNextFrame(long nativeHandle);

	private native int nativeLoadFrame(long nativeHandle, double time, int textureId);

	private native int nativeLoadFrames(long nativeHandle, int numFrames, int textureId);

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
			AVAsset asset = new AVAsset(new URL("file:///Users/radar/Desktop/movies/hot_chip-flutes_(sacha_remix).mp4"));
			System.out.println("Asset: " + asset.getWidth() + " " + asset.getHeight());
			Frame frame = asset.getFrame(2.0);

			BufferedImage image = frame.toBufferedImage();

			System.out.println("Image: " + image.getWidth() + " " + image.getHeight());

			for (int i = 0; i < 600; ++i) {
				frame = asset.getNextFrame();
			}
			
			image = frame.toBufferedImage();

			System.out.println("Image: " + image.getWidth() + " " + image.getHeight());

			try {
				File outputfile = new File("/Users/radar/Desktop/saved.png");
				ImageIO.write(image, "png", outputfile);
			} catch (IOException e) {
			}
			asset.dispose();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
