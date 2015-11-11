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

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.util.TextUtilities;

public class URLVideoSource extends AbstractVideoSource {
	private int         width;
	private int         height;
	private double      frameRate  = FRAMERATE_UNKNOWN;
	private long        frameCount = FRAMECOUNT_UNKNOWN;
	protected final URL url;
	protected double    playOutTime;
	protected int       numPlays;
	protected double    startTime = -1;
	private final Frame frame;

	public URLVideoSource(URL url) throws IOException {
		this(url, Integer.MAX_VALUE);
	}

	public URLVideoSource(URL url, int numPlays) throws IOException {
		this.url      = url;
		this.numPlays = numPlays;
		this.frame    = Frame.create(url);
	}

	@Override
	protected void run(IVideoRenderTarget target) throws RenderCommandException {
		if(startTime <= 0)
			startTime = target.getTime();
		target.setFrame(new VideoFrame(startTime + getPlayOutTime(), getNextFrame()));
	}

	public double getPlayOutTime() {
		return playOutTime;
	}

	protected Frame getNextFrame() {
		return frame;
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
	public double getFrameRate() {
		return frameRate;
	}

	@Override
	public long getFrameCount() {
		return frameCount;
	}
	
	public static class Track {
		protected final URL url;
		protected double    playOutTime;
		protected int       numPlays;
		protected double    startTime = -1;
		private final Frame frame;

		Track(URL url) throws IOException {
			this.frame    = Frame.create(url);
			this.url      = url;
			this.numPlays = 0;
		}

		protected Track(URL url, int numPlays) {
			this.frame    = null;
			this.url      = url;
			this.numPlays = numPlays;
		}

		public double getPlayOutTime() {
			return playOutTime;
		}

		protected Frame getNextFrame() {
			return frame;
		}
		protected int getWidth() {
			return frame.dimI;
		}
		protected int getHeight() {
			return frame.dimJ;
		}
		protected double getFrameRate() {
			return FRAMERATE_UNKNOWN;
		}
		protected long getFrameCount() {
			return 1;
		}
	}
}
