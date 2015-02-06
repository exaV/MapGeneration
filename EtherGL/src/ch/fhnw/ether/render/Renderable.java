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

package ch.fhnw.ether.render;

import java.nio.FloatBuffer;
import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.gl.FloatArrayBuffer;
import ch.fhnw.ether.render.gl.IArrayBuffer;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.variable.IShaderArray;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.util.BufferUtilities;
import ch.fhnw.util.math.Mat3;
import ch.fhnw.util.math.Mat4;

// TODO: deal with max vbo size & multiple vbos, memory optimization, handle non-float arrays

public final class Renderable {
	private final IShader shader;

	private final IMesh mesh;

	private final FloatArrayBuffer buffer = new FloatArrayBuffer();
	private final int[] sizes;
	private final int stride;

	// FIXME: let's get rid of this providers list somehow (an unmodifiable map of provided arrays would be fine)
	public Renderable(IMesh mesh, List<IAttributeProvider> providers) {
		this(null, mesh, providers);
	}

	public Renderable(IShader shader, IMesh mesh, List<IAttributeProvider> providers) {
		this.shader = ShaderBuilder.create(shader, mesh, providers);
		this.mesh = mesh;

		// setup buffers
		List<IShaderArray<?>> arrays = this.shader.getArrays();
		int stride = 0;
		sizes = new int[arrays.size()];
		int i = 0;
		for (IShaderArray<?> attr : arrays) {
			stride += sizes[i++] = attr.getNumComponents().get();
		}
		this.stride = stride;
		
		i = 0;
		int offset = 0;
		for (IShaderArray<?> attr : arrays) {
			attr.setup(stride, offset);
			offset += sizes[i++];
		}

		// make sure update flag is set, so everything get initialized on the next render cycle
		mesh.requestUpdate(null);
	}

	public void update(GL3 gl) {
		if (mesh.needsMaterialUpdate())
			shader.update(gl);

		if (mesh.needsGeometryUpdate())
			loadBuffer(gl);
	}

	public void render(GL3 gl) {
		shader.enable(gl);
		shader.render(gl, getCount(), getBuffer());
		shader.disable(gl);
	}

	public IMesh.Queue getQueue() {
		return mesh.getQueue();
	}

	public boolean containsFlag(IMesh.Flags flag) {
		return mesh.getFlags().contains(flag);
	}

	public int getCount() {
		return buffer.size() / stride;
	}

	public int getStride() {
		return stride;
	}

	public IArrayBuffer getBuffer() {
		return buffer;
	}

	@Override
	public String toString() {
		return "renderable[queue=" + mesh.getQueue() + " shader=" + shader + " stride=" + stride + "]";
	}

	private static FloatBuffer target = BufferUtilities.createDirectFloatBuffer(1024 * 1024);

	private void loadBuffer(GL3 gl) {
		Mat4 modelMatrix = Mat4.multiply(Mat4.translate(mesh.getPosition()), mesh.getTransform());
		Mat3 normalMatrix = new Mat3(modelMatrix).inverse().transpose();
		
		List<IShaderArray<?>> arrays = shader.getArrays();
		float[][] sources = new float[arrays.size()][];
		mesh.getGeometry().inspect((attributes, data) -> {
			int size = 0;
			for (int i = 0; i < arrays.size(); ++i) {
				IShaderArray<?> array = arrays.get(i);
				float[] source = data[array.getAttributeIndex()];
				if (array.id().equals(IGeometry.POSITION_ARRAY.id()))
					sources[i] = modelMatrix.transform(source);
				else if (array.id().equals(IGeometry.NORMAL_ARRAY.id()))
					sources[i] = normalMatrix.transform(source);
				else
					sources[i] = source;
				size += source.length;
			}
			if (target.capacity() < size)
				target = BufferUtilities.createDirectFloatBuffer(2 * size);
			target.clear();
			target.limit(size);
			interleave(target, sources, sizes);
		});
		buffer.load(gl, target);
	}

	private static void interleave(FloatBuffer target, float[][] data, int[] sizes) {
		for (int i = 0; i < data[0].length / sizes[0]; ++i) {
			for (int j = 0; j < data.length; ++j) {
				int k = (i * sizes[j]) % data[j].length;
				target.put(data[j], k, sizes[j]);
			}
		}
	}
}
