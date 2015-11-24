/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

import java.lang.ref.ReferenceQueue;

import com.jogamp.opengl.GL3;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.render.gl.GLObject;
import ch.fhnw.ether.render.gl.GLObject.Type;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.video.FrameAccess;
import ch.fhnw.ether.video.URLVideoSource;
import ch.fhnw.ether.view.gl.GLContextManager;
import ch.fhnw.ether.view.gl.GLContextManager.IGLContext;
import ch.fhnw.util.AutoDisposer;
import ch.fhnw.util.AutoDisposer.Reference;
import ch.fhnw.util.IDisposable;
import ch.fhnw.util.Log;

public final class AVAsset extends FrameAccess {
	private static final Log log   = Log.create();
	private static boolean   READY = true;

	public static class AVAssetRef extends Reference<AVAsset> {
		private final long nativeHandle;

		public AVAssetRef(AVAsset referent, ReferenceQueue<? super AVAsset> q) {
			super(referent, q);
			nativeHandle = referent.nativeHandle;
		}

		@Override
		public void dispose() {
			nativeDispose(nativeHandle);
		}
	}

	public static final AutoDisposer<AVAsset> autoDispose = new AutoDisposer<>(AVAssetRef.class);

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

	private long    nativeHandle;
	private double  duration;
	private float   frameRate;
	private long    frameCount;
	private int     width;
	private int     height;
	private long    frameNo;
	private boolean skippedFrames;

	public AVAsset(URLVideoSource src, int numPlays) {
		super(src, numPlays);		
		nativeHandle = nativeCreate(src.toString());
		if (nativeHandle == 0)
			throw new IllegalArgumentException("cannot create avasset from " + src.getURL());

		autoDispose.add(this);
		duration   = nativeGetDuration(nativeHandle);
		frameRate  = (float)nativeGetFrameRate(nativeHandle);
		frameCount = nativeGetFrameCount(nativeHandle);
		width      = nativeGetWidth(nativeHandle);
		height     = nativeGetHeight(nativeHandle);
	}

	@Override
	public double getDuration() {
		return duration;
	}

	@Override
	public float getFrameRate() {
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

	public void rewind() {
		frameNo = 0;
		numPlays--;
		nativeRewind(nativeHandle);
	}

	@Override
	protected boolean skipFrame() {
		frameNo++;
		boolean result = frameNo < frameCount;
		if(!(result))
			numPlays--;
		return result;
	}

	@Override
	protected Frame getNextFrame() {
		byte[] pixels;
		if(skippedFrames) {
			pixels        = nativeGetFrame(nativeHandle, frameNo / frameRate);
			skippedFrames = false;
		} else
			pixels = nativeGetNextFrame(nativeHandle);
		if(pixels == null) {
			rewind();
			if(numPlays <= 0)
				return null;
			pixels = nativeGetNextFrame(nativeHandle);
		}
		frameNo++;
		return new RGBA8Frame(getWidth(), getHeight(), pixels);
	}

	static class Data implements IDisposable {
		long[] data = new long[IDX_COUNT];

		public Data(long nativeHandle) {
			data[IDX_HANDLE] = nativeHandle;
		}

		@Override
		public void dispose() {
			nativeDisposeTexture(data[IDX_HANDLE], data);
		}
	}

	@Override
	public Texture getNextTexture() {
		try(IGLContext ctx = GLContextManager.acquireContext()) {
			Data data = new Data(nativeHandle);
			int result = nativeGetNextTexture(nativeHandle, data.data);
			if(result < 0) {
				rewind();
				if(numPlays <= 0)
					return null;
				result = nativeGetNextTexture(nativeHandle, data.data);
			}
			if(data.data[IDX_TARGET] != GL3.GL_TEXTURE_2D)
				throw new RenderCommandException("Wrong target. Expected " + GL3.GL_TEXTURE_2D + ", got " + data.data[IDX_TARGET]);
			return result == 0 ? new Texture(new GLObject(Type.TEXTURE, (int)data.data[IDX_NAME], data), (int)data.data[IDX_WIDTH], (int)data.data[IDX_HEIGHT]) : null;
		} catch(Throwable t) {
			log.warning(t);
			return null;
		}
	}

	@Override
	public String toString() {
		return src.getURL() + " (d=" + getDuration() + " fr=" + getFrameRate() + " fc=" + getFrameCount() + " w=" + getWidth() + " h=" + getHeight() + ")";
	}

	private static final int IDX_HANDLE  = 0;
	private static final int IDX_TARGET  = 1;
	private static final int IDX_NAME    = 2;
	private static final int IDX_WIDTH   = 3;
	private static final int IDX_HEIGHT  = 4;
	private static final int IDX_SAMPLE  = 5;
	private static final int IDX_TEXTURE = 6;
	private static final int IDX_COUNT   = 7;


	private static native long nativeCreate(String url);

	private static native void nativeDispose(long nativeHandle);

	private static native double nativeGetDuration(long nativeHandle);

	private static native double nativeGetFrameRate(long nativeHandle);

	private static native long nativeGetFrameCount(long nativeHandle);

	private static native int nativeGetWidth(long nativeHandle);

	private static native int nativeGetHeight(long nativeHandle);

	private static native void nativeRewind(long nativeHandle);

	private static native byte[] nativeGetFrame(long nativeHandle, double time);

	private static native byte[] nativeGetNextFrame(long nativeHandle);

	private static native int nativeGetNextTexture(long nativeHandle, long[] data);

	private static native int nativeDisposeTexture(long nativeHandle, long[] data);
}
