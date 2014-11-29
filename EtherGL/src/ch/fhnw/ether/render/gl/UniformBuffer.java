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

package ch.fhnw.ether.render.gl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.concurrent.atomic.AtomicInteger;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import com.jogamp.common.nio.Buffers;

/**
 * Basic uniform buffer object wrapper.
 *
 * @author radar
 */
public class UniformBuffer {
	private static final AtomicInteger BINDING_POINT_COUNTER = new AtomicInteger();
	
	private static final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;

	private static final FloatBuffer EMPTY_BUFFER = Buffers.newDirectFloatBuffer(0);

	private final int bindingPoint;
	private int[] ubo;
	private int size;

	public UniformBuffer(int bindingPoint) {
		this.bindingPoint = bindingPoint;
	}

	public void dispose(GL3 gl) {
		if (ubo != null) {
			gl.glDeleteBuffers(1, ubo, 0);
			ubo = null;
		}
		size = 0;
	}

	public void load(GL3 gl, Buffer data) {
		if (ubo == null) {
			ubo = new int[1];
			gl.glGenBuffers(1, ubo, 0);
		}

		gl.glBindBuffer(GL3.GL_UNIFORM_BUFFER, ubo[0]);
		if (data != null && data.limit() != 0) {
			size = data.limit();
			data.rewind();

			// transfer data to VBO
			int numBytes = size * BYTES_PER_FLOAT;
			gl.glBufferData(GL3.GL_UNIFORM_BUFFER, numBytes, data, GL.GL_STATIC_DRAW);
		} else {
			size = 0;
			gl.glBufferData(GL3.GL_UNIFORM_BUFFER, 0, EMPTY_BUFFER, GL.GL_STATIC_DRAW);
		}
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}

	public void clear(GL3 gl) {
		load(gl, null);
	}

	public void bind(GL3 gl) {
		gl.glBindBufferBase(GL3.GL_UNIFORM_BUFFER, bindingPoint, ubo[0]);
	}

	public void bind(GL3 gl, int offset, int size) {
		gl.glBindBufferRange(GL3.GL_UNIFORM_BUFFER, bindingPoint, ubo[0], offset, size);
	}

	public int getBindingPoint() {
		return bindingPoint;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}
	
	public static int getNewBindingPoint() {
		return BINDING_POINT_COUNTER.getAndIncrement();
	}
}
