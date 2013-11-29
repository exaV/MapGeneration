/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.gl;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * Simple, even primitive VBO wrapper.
 * 
 * @author radar
 * 
 */
// XXX work in progress (will change with GLcore 3/4+)
public class VBO {
	private static final FloatBuffer EMPTY_BUFFER = Buffers.newDirectFloatBuffer(0);
	
	private int vbos[] = new int[4];
	private boolean hasNormals = false;
	private boolean hasColors = false;
	private boolean hasTexCoords = false;
	private int numVertices;

	public VBO(GL gl) {
		// generate a VBO pointer / handle
		gl.glGenBuffers(4, vbos, 0);
	}

	public void dispose(GL gl) {
		gl.glDeleteBuffers(4, vbos, 0);
		vbos = null;
	}
	
	public void load(GL gl, FloatBuffer vertices) {
		load(gl, vertices.limit() / 3, vertices, null, null, null);
	}

	public void load(GL gl, int numVertices, FloatBuffer vertices, FloatBuffer normals, FloatBuffer colors, FloatBuffer texCoords) {
		this.numVertices = numVertices;
		hasNormals = hasColors = hasTexCoords = false;

		int bytesPerFloat = Float.SIZE / Byte.SIZE;
		
		if (vertices != null && vertices.limit() != 0) {
			vertices.rewind();

			// transfer data to VBO
			int numBytes = vertices.limit() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, vertices, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}

		if (normals != null && normals.limit() != 0) {
			hasNormals = true;
			normals.rewind();

			// transfer data to VBO
			int numBytes = normals.limit() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[1]);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, normals, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}

		if (colors != null && colors.limit() != 0) {
			hasColors = true;
			colors.rewind();

			// transfer data to VBO
			int numBytes = colors.limit() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[2]);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, colors, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}

		if (texCoords != null && texCoords.limit() != 0) {
			hasTexCoords = true;
			texCoords.rewind();

			// transfer data to VBO
			int numBytes = texCoords.limit() * bytesPerFloat;
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[3]);
			gl.glBufferData(GL.GL_ARRAY_BUFFER, numBytes, texCoords, GL.GL_STATIC_DRAW);
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void clear(GL gl) {
		load(gl, 0, EMPTY_BUFFER, EMPTY_BUFFER, EMPTY_BUFFER, EMPTY_BUFFER);
	}

	public void render(GL2 gl, int mode) {
		if (numVertices == 0)
			return;
		
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[0]);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

		if (hasNormals) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[1]);
			gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL.GL_FLOAT, 0, 0);
		}
		if (hasColors) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[2]);
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL.GL_FLOAT, 0, 0);
		}
		if (hasTexCoords) {
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbos[3]);
			gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);	
		}
		
		gl.glDrawArrays(mode, 0, numVertices);

		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		
		if (hasNormals) {
			gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		}
		if (hasColors) {
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		}
		if (hasTexCoords) {
			gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
		}

		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}
}
