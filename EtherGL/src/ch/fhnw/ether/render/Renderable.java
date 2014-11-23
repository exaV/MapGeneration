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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.attribute.base.FloatArrayAttribute;
import ch.fhnw.ether.render.gl.FloatArrayBuffer;
import ch.fhnw.ether.render.gl.IArrayBuffer;
import ch.fhnw.ether.render.gl.Program;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.IMesh;

// FIXME: deal with max vbo size & multiple vbos, handle non-float arrays

public final class Renderable {
	private IMesh mesh;
	private IShader shader;

	private List<IUniformAttribute<?>> uniforms = new ArrayList<>();
	private List<IArrayAttribute<?>> arrays = new ArrayList<>();
	private FloatArrayBuffer buffer = new FloatArrayBuffer();

	private int[] sizes;
	private int stride;

	public Renderable(IMesh mesh, AttributeProviders providers) {
		this.mesh = mesh;

		shader = ShaderBuilder.buildShader(mesh, providers, uniforms, arrays);
		
		setupBuffers();

		// make sure update flag is set, so everything get initialized on the next render cycle
		mesh.requestUpdate(null);
	}

	public void dispose(GL3 gl) {
		shader.dispose(gl);
		uniforms.forEach((t) -> t.dispose(gl));
		arrays.forEach((t) -> t.dispose(gl));
		buffer.dispose(gl);

		mesh = null;
		shader = null;

		uniforms = null;

		arrays = null;
		buffer = null;
		stride = 0;
	}

	public void update(GL3 gl) {
		if (mesh.needsUpdate()) {
			shader.update(gl);
			loadBuffer(gl);
		}
	}

	public void render(GL3 gl) {
		// 1. enable program
		shader.enable(gl);
		Program program = shader.getProgram();

		// 2. for each uniform attribute
		// 2.1. set uniform (shader index, value), enable textures, set gl state
		for (IUniformAttribute<?> attr : uniforms) {
			attr.enable(gl, program);
		}

		// FIXME: use VAO here
		// 3. for each parallel buffer
		// 3.1. bind buffer
		// 3.2. for each array attribute associated to the buffer
		// 3.2.1 enable vertex attrib array (shader index)
		// 3.2.2 set vertex attrib pointer (shader index, size, stride, offset)
		buffer.bind(gl);
		for (IArrayAttribute<?> attr : arrays) {
			attr.enable(gl, program, buffer);
		}

		// 4. draw arrays (# elements = buffer size / stride)
		shader.render(gl, buffer.size() / stride);

		// 5. for each buffer
		// 5.1. bind buffer
		// 5.2. for each array attribute
		// 5.2.1 disable vertex attrib array (shader index)
		buffer.bind(gl);
		for (IArrayAttribute<?> attr : arrays) {
			attr.disable(gl, program, buffer);
		}

		// 6. for each uniform attribute
		// 6.1. disable textures, restore gl state
		for (IUniformAttribute<?> attr : uniforms) {
			attr.disable(gl, program);
		}

		// 7. disable program and clear buffer binding
		shader.disable(gl);

		IArrayBuffer.unbind(gl);
	}

	public IMesh.Pass getPass() {
		return mesh.getPass();
	}

	public boolean containsFlag(IMesh.Flags flag) {
		return mesh.getFlags().contains(flag);
	}

	@Override
	public String toString() {
		return "renderable[pass=" + mesh.getPass() + " shader=" + shader + " stride=" + stride + "]";
	}
	
	private void setupBuffers() {
		stride = 0;
		sizes = new int[arrays.size()];
		int i = 0;
		for (IArrayAttribute<?> attr : arrays) {
			stride += sizes[i++] = attr.getNumComponents().get();
		}
		i = 0;
		int offset = 0;
		for (IArrayAttribute<?> attr : arrays) {
			attr.setup(stride, offset);
			offset += sizes[i++];
		}
	}

	private void loadBuffer(GL3 gl) {
		// FIXME: fix memory management/allocation throughout (+ thread safe)
		int size = 0;
		FloatArrayAttribute attr = (FloatArrayAttribute) arrays.get(0);
		for (Supplier<float[]> supplier : attr.getSuppliers()) {
			size += supplier.get().length;
		}
		size = size / attr.getNumComponents().get() * stride;

		final float[] interleavedData = new float[size];
		final float[][] data = new float[arrays.size()][];

		int index = 0;
		for (int supplierIndex = 0; supplierIndex < attr.getSuppliers().size(); ++supplierIndex) {
			for (int attributeIndex = 0; attributeIndex < arrays.size(); ++attributeIndex) {
				data[attributeIndex] = ((FloatArrayAttribute) (arrays.get(attributeIndex))).getSuppliers().get(supplierIndex).get();
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
