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
import java.util.function.Supplier;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.gl.FloatArrayBuffer;
import ch.fhnw.ether.render.gl.IArrayBuffer;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.variable.IShaderArray;
import ch.fhnw.ether.render.variable.base.FloatArray;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.mesh.IMesh;

// FIXME: deal with max vbo size & multiple vbos, memory optimization, handle non-float arrays

public final class Renderable {
	private IShader shader;

	private IMesh mesh;

	private FloatArrayBuffer buffer = new FloatArrayBuffer();
	private int[] sizes;
	private int stride;

	// FIXME: let's get rid of this providers list somehow (an unmodifiable map of provided arrays would be fine)
	public Renderable(IMesh mesh, List<IAttributeProvider> providers) {
		this(null, mesh, providers);
	}
	
	public Renderable(IShader shader, IMesh mesh, List<IAttributeProvider> providers) {
		this.shader = ShaderBuilder.create(shader, mesh, providers);
		this.mesh = mesh;

		// setup buffers
		List<IShaderArray<?>> arrays = this.shader.getArrays();
		stride = 0;
		sizes = new int[arrays.size()];
		int i = 0;
		for (IShaderArray<?> attr : arrays) {
			stride += sizes[i++] = attr.getNumComponents().get();
		}
		i = 0;
		int offset = 0;
		for (IShaderArray<?> attr : arrays) {
			attr.setup(stride, offset);
			offset += sizes[i++];
		}

		// make sure update flag is set, so everything get initialized on the next render cycle
		mesh.requestUpdate(null);
		
	}

	public void dispose(GL3 gl) {
		shader.dispose(gl);
		buffer.dispose(gl);

		mesh = null;

		shader = null;

		buffer = null;
		sizes = null;
		stride = 0;
	}

	public void update(GL3 gl) {
		if (mesh.needsUpdate()) {
			shader.update(gl);
			loadBuffer(gl);
		}
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

	// FIXME: thread safety
	// FIXME: fix memory management/allocation
	private void loadBuffer(GL3 gl) {
		List<IShaderArray<?>> arrays = shader.getArrays();
		int length = 0;
		FloatArray attr = (FloatArray) arrays.get(0);
		for (Supplier<float[]> supplier : attr.getSuppliers()) {
			length += supplier.get().length;
		}
		length = length / attr.getNumComponents().get() * stride;

		final float[] interleavedData = new float[length];
		final float[][] data = new float[arrays.size()][];

		int index = 0;
		for (int supplierIndex = 0; supplierIndex < attr.getSuppliers().size(); ++supplierIndex) {
			for (int attributeIndex = 0; attributeIndex < arrays.size(); ++attributeIndex) {
				data[attributeIndex] = ((FloatArray) (arrays.get(attributeIndex))).getSuppliers().get(supplierIndex).get();
			}
			index = interleave(interleavedData, index, data, sizes);
		}

		buffer.load(gl, FloatBuffer.wrap(interleavedData));
	}

	private static int interleave(final float[] interleavedData, int index, final float[][] data, final int[] sizes) {
		for (int i = 0; i < data[0].length / sizes[0]; ++i) {
			for (int j = 0; j < data.length; ++j) {
				int k = (i * sizes[j]) % data[j].length;
				for (int l = 0; l < sizes[j]; ++l) {
					interleavedData[index++] = data[j][k + l];
				}
			}
		}
		return index;
	}
}
