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

package ch.fhnw.ether.video.jcodec;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jcodec.api.JCodecException;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;

import ch.fhnw.ether.video.IVideoTrack;

abstract class AbstractVideoTrack implements IVideoTrack {

	private URL url;
	private SeekableByteChannel channel;
	protected FrameGrab grab;

	public AbstractVideoTrack(URL url) throws IOException, URISyntaxException, JCodecException {
		this.url = url;
		this.channel = NIOUtils.readableFileChannel(new File(url.toURI()));
		this.grab = new FrameGrab(channel);
	}

	@Override
	public void dispose() {
		try {
			channel.close();
		} catch (IOException e) {
		}
		this.url = null;
		this.channel = null;
		this.grab = null;
	}

	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public double getDuration() {
		return grab.getVideoTrack().getMeta().getTotalDuration();
	}

	@Override
	public double getFrameRate() {
		return getFrameCount() / getDuration();
	}

	@Override
	public long getFrameCount() {
		return grab.getVideoTrack().getMeta().getTotalFrames();
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
		return getURL() + " (d=" + getDuration() + " fr=" + getFrameRate() + " fc=" + getFrameCount() + " w=" + getWidth() + " h=" + getHeight() + ")";
	}
}
