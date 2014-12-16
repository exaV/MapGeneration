/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
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

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.FrameException;
import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.video.IRandomAccessFrameSource;
import ch.fhnw.ether.video.ISequentialFrameSource;

public final class AVAsset implements ISequentialFrameSource, IRandomAccessFrameSource {
	private static boolean READY = true;
	
	static {
		try {
			System.loadLibrary("etherglvideo");
		} catch (Exception e) {
			READY = false;
		}
	}

	public static boolean isReady() {
		return READY;
	}

	private URL url;

	private long nativeHandle;

	private double duration;
	private double frameRate;
	private long frameCount;
	private int width;
	private int height;
	Set<Class<? extends Frame>> preferredTypes;
	
	public AVAsset(URL url) {
		this.url = url;
		nativeHandle = nativeCreate(url.toString());
		if (nativeHandle == 0)
			throw new IllegalArgumentException("cannot create avasset from " + url);
		duration       = nativeGetDuration(nativeHandle);
		frameRate      = nativeGetFrameRate(nativeHandle);
		frameCount     = nativeGetFrameCount(nativeHandle);
		width          = nativeGetWidth(nativeHandle);
		height         = nativeGetHeight(nativeHandle);
		preferredTypes = new HashSet<>(Arrays.asList(getFrameTypes()));
	}

	@Override
	public void dispose() {
		nativeDispose(nativeHandle);
	}

	@Override
	public URL getURL() {
		return url;
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
	public long getFrameCount() {
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
	public FrameReq getFrames(FrameReq req) {
		req.processFrames(RGBA8Frame.class, getWidth(), getHeight(), (Frame frame, int i)->{
			if(frame.dimI != getWidth() || frame.dimJ != getHeight())
				throw new FrameException("Size mismatch, use a scaler");
			byte[] pixels = nativeGetNextFrame(nativeHandle);
			if(pixels == null) 
				throw new FrameException("End of trackreached.");
			frame.pixels.clear();
			if(frame.pixelSize == 3) {
				byte[] tmp = new byte[3 * pixels.length / 4];
				int dsti = 0;
				int srci = 0;
				for(int p = 0; p < pixels.length; p += 4) {
					tmp[dsti++] = pixels[srci++];
					tmp[dsti++] = pixels[srci++];
					tmp[dsti++] = pixels[srci++];
					srci++;
				}
				pixels = tmp;
			} else {
				byte[] tmp = new byte[pixels.length];
				int dsti = 0;
				int srci = 0;
				for(int p = 0; p < pixels.length; p += 4) {
					tmp[dsti++] = pixels[srci++];
					tmp[dsti++] = pixels[srci++];
					tmp[dsti++] = pixels[srci++];
					tmp[dsti++] = pixels[srci++];
				}
				pixels = tmp;
			}
			frame.pixels.put(pixels);
		});
		return req;
	}

	@Override
	public Class<? extends Frame>[] getFrameTypes() {
		return FTS_RGBA8_RGB8;
	}
	
	/*
	@Override
	public Frame getFrame(long frame) {
		return getFrame(frameToTime(frame));
	}

	@Override
	public Frame getFrame(double time) {
		byte[] pixels = nativeGetFrame(nativeHandle, time);
		if(pixels == null) return null;
		return new RGBA8Frame(getWidth(), getHeight(), pixels);
	}

	@Override
	public Frame getNextFrame() {
	}
	 */

	@Override
	public void setPreferredFrameTypes(Set<Class<? extends Frame>> frameTypes) {
		preferredTypes.retainAll(frameTypes);
	}
	
	@Override
	public String toString() {
		return getURL() + " (d=" + getDuration() + " fr=" + getFrameRate() + " fc=" + getFrameCount() + " w=" + getWidth() + " h=" + getHeight() + ")";
	}

	private double frameToTime(long frame) {
		return getDuration() * getFrameRate() / frame;
	}

	private native long nativeCreate(String url);

	private native void nativeDispose(long nativeHandle);

	private native double nativeGetDuration(long nativeHandle);

	private native double nativeGetFrameRate(long nativeHandle);

	private native long nativeGetFrameCount(long nativeHandle);

	private native int nativeGetWidth(long nativeHandle);

	private native int nativeGetHeight(long nativeHandle);

	private native void nativeRewind(long nativeHandle);

	private native byte[] nativeGetFrame(long nativeHandle, double time);

	private native byte[] nativeGetNextFrame(long nativeHandle);

	private native int nativeLoadFrame(long nativeHandle, double time, int textureId);

	private native int nativeLoadFrames(long nativeHandle, int numFrames, int textureId);
}
