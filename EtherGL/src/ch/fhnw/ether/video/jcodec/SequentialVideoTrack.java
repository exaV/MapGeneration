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

package ch.fhnw.ether.video.jcodec;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.jcodec.api.JCodecException;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.media.AbstractFrameSource;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.video.FrameAccess;
import ch.fhnw.ether.video.URLVideoSource;
import ch.fhnw.util.Log;

public final class SequentialVideoTrack extends FrameAccess {
	private static final Log log = Log.create();

	private   SeekableByteChannel channel;
	protected FrameGrab           grab;

	public SequentialVideoTrack(URLVideoSource src, int numPlays) throws IOException, URISyntaxException {
		super(src, numPlays);
		this.channel = NIOUtils.readableFileChannel(new File(src.getURL().toURI()));
		try {
			this.grab    = new FrameGrab(channel);
		} catch(JCodecException e) {
			throw new IOException(e);
		}
	}

	public void dispose() {
		try {
			channel.close();
		} catch (IOException e) {
		}
		this.channel = null;
		this.grab    = null;
	}

	@Override
	public double getDuration() {
		try {
			return grab.sdt().getMeta().getTotalDuration();
		} catch (JCodecException e) {
			log.warning(e);
			return AbstractFrameSource.LENGTH_UNKNOWN;
		}
	}

	@Override
	public float getFrameRate() {
		double result = getFrameCount() / getDuration();
		return (float)result;
	}

	@Override
	public long getFrameCount() {
		try {
			return grab.sdt().getMeta().getTotalFrames();
		} catch (JCodecException e) {
			log.warning(e);
			return AbstractFrameSource.FRAMECOUNT_UNKNOWN;
		}
	}

	@Override
	public int getWidth() {
		return grab.getMediaInfo().getDim().getWidth();
	}

	@Override
	public int getHeight() {
		return grab.getMediaInfo().getDim().getHeight();
	}

	@Override
	public String toString() {
		return src.getURL() + " (d=" + getDuration() + " fr=" + getFrameRate() + " fc=" + getFrameCount() + " w=" + getWidth() + " h=" + getHeight() + ")";
	}

	public void rewind() {
		try {
			numPlays--;
			grab.seekToFramePrecise(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected boolean skipFrame() {
		boolean result = false;
		try {
			result = grab.skipFrame();
		} catch(Throwable t) {
			rewind();
			try {
				result = grab.skipFrame();
			} catch(Throwable t0) {}
		}
		if(!(result))
			numPlays--;
		return result;
	}

	@Override
	protected Frame getNextFrame() {
		RGB8Frame frame = new RGB8Frame(getWidth(), getHeight());
		try {
			grab.grabAndSet(frame);
		} catch(Throwable t) {}
		return frame;
	}

	@Override
	public Texture getNextTexture() {
		return getNextFrame().getTexture();
	}
}
