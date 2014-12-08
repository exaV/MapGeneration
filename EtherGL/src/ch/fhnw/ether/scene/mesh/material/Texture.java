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

package ch.fhnw.ether.scene.mesh.material;

import java.net.URL;

import javax.media.opengl.GL;

import ch.fhnw.ether.media.FrameReq;
import ch.fhnw.ether.media.IFrameSource;
import ch.fhnw.ether.video.IRandomAccessFrameSource;
import ch.fhnw.ether.video.ISequentialFrameSource;
import ch.fhnw.ether.video.IVideoFrameSource;
import ch.fhnw.ether.video.VideoTrackFactory;
import ch.fhnw.util.UpdateRequest;

/**
 * Texture data encapsulation (FIXME: needs extension/generalization, array tex, 3d tex etc)
 *
 * @author radar
 */
public class Texture {
	private final UpdateRequest updater = new UpdateRequest();

	private IVideoFrameSource track;
	private long              frame = 0;
	private double            time  = -1;

	public Texture() {
	}

	public Texture(IVideoFrameSource track) {
		this.track = track;
	}

	public Texture(URL url) {
		setData(url);
	}

	public void setData(URL url) {
		try {
			setData(VideoTrackFactory.createSequentialTrack(url));
		} catch (Throwable e) {
			throw new IllegalArgumentException("can't load image " + url);
		}
	}

	public void setData(IVideoFrameSource track) {
		this.track = track;
		updater.requestUpdate();
	}

	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	public int getWidth() {
		return track.getWidth();
	}

	public int getHeight() {
		return track.getHeight();
	}

	@Override
	public String toString() {
		return "texture[w=" + getWidth() + " h=" + getHeight() + "]";
	}

	public IFrameSource getTrack() {
		return track;
	}

	public void setTime(double time) {
		this.time  = time;
		this.frame = -1;
		this.updater.requestUpdate();
	}

	public void setFrame(long frame) {
		this.time  = -1;
		this.frame = frame;
		this.updater.requestUpdate();
	}

	public void update() {
		updater.requestUpdate();
	}
	
	public void load(GL gl, int target, int textureId) {
		if(track instanceof ISequentialFrameSource)
			((ISequentialFrameSource)track).getFrames(new FrameReq(gl, 1, textureId));
		else if(track instanceof IRandomAccessFrameSource) {
			if(time >= 0)
				((IRandomAccessFrameSource)track).getFrames(new FrameReq(gl, time, textureId));
			else
				((IRandomAccessFrameSource)track).getFrames(new FrameReq(gl, frame, textureId));
		}
	}	
}
