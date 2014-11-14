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
import java.nio.Buffer;

import javax.media.opengl.GL3;

import ch.fhnw.ether.image.FloatFrame;
import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.Grey16Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.image.RGBA8Frame;
import ch.fhnw.ether.video.VideoTrackFactory;
import ch.fhnw.util.UpdateRequest;

/**
 * Texture data encapsulation (FIXME: needs extension/generalization, array tex, 3d tex etc)
 *
 * @author radar
 */
public class Texture {
	private final UpdateRequest updater = new UpdateRequest();

	private Frame frame;
	private int   format;

	public Texture() {
	}

	public Texture(URL url) {
		setData(url);
	}

	public void setData(URL url) {
		try {
			frame = VideoTrackFactory.createSequentialTrack(url).getNextFrame();
		} catch (Throwable e) {
			throw new IllegalArgumentException("can't load image " + url);
		}
	}

	public void setData(Frame frame) {
		this.frame  = frame;
		if(frame instanceof RGB8Frame)
			this.format = GL3.GL_RGB;
		else if(frame instanceof RGBA8Frame)
			this.format = GL3.GL_RGBA;
		else if(frame instanceof FloatFrame)
			this.format = GL3.GL_RED;
		else if(frame instanceof Grey16Frame)
			this.format = GL3.GL_RED;
		updater.requestUpdate();
	}

	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	public int getWidth() {
		return frame.dimI;
	}

	public int getHeight() {
		return frame.dimJ;
	}

	public Buffer getBuffer() {
		return frame.pixels;
	}

	public int getFormat() {
		return format;
	}

	@Override
	public String toString() {
		return "texture[w=" + frame.dimI + " h=" + frame.dimJ + "]";
	}
}
