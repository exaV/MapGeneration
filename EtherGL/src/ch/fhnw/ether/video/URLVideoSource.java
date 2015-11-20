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

package ch.fhnw.ether.video;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.video.avfoundation.AVAsset;
import ch.fhnw.ether.video.jcodec.SequentialVideoTrack;
import ch.fhnw.util.TextUtilities;

public class URLVideoSource extends AbstractVideoSource {
	private static final boolean USE_AV_FOUNDATION = true;

	private final int    width;
	private final int    height;
	private final float  frameRate;
	private final long   frameCount;
	private final double length;
	protected final URL  url;
	private final FrameAccess  asset;

	public URLVideoSource(URL url) throws IOException {
		this(url, Integer.MAX_VALUE);
	}

	public URLVideoSource(URL url, int numPlays) throws IOException {
		this.url      = url;
		try {
			asset      = isStillImage(url) ? new FrameAccess(this) : USE_AV_FOUNDATION ? new AVAsset(this, numPlays) : new SequentialVideoTrack(this, numPlays);
			width      = asset.getWidth();
			height     = asset.getHeight();
			frameRate  = asset.getFrameRate();
			frameCount = asset.getFrameCount();
			length     = asset.getDuration();
		} catch(Throwable t) {
			throw new IOException(t);
		}
	}

	@Override
	protected void run(IVideoRenderTarget target) throws RenderCommandException {
		VideoFrame frame = new VideoFrame(getTotalElapsedFrames() / getFrameRate(), asset);
		if(asset.numPlays <= 0)
			frame.setLast(true);
		setFrame(target, frame);
	}

	public static boolean isStillImage(URL url) {
		return ImageIO.getImageReadersBySuffix(TextUtilities.getFileExtensionWithoutDot(url.getPath())).hasNext();
	}

	@Override
	public String toString() {
		return url.toString();
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
	public float getFrameRate() {
		return frameRate;
	}

	@Override
	public long getLengthInFrames() {
		return frameCount;
	}

	@Override
	public double getLengthInSeconds() {
		return length;
	}	

	public URL getURL() {
		return url;
	}
}
