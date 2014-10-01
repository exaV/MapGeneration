/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IArrayAttributeProvider;
import ch.fhnw.ether.render.attribute.IAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.ether.render.attribute.base.FloatArrayAttribute;
import ch.fhnw.ether.render.gl.FloatArrayBuffer;
import ch.fhnw.ether.render.gl.Program;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.FloatList;
import ch.fhnw.util.UpdateRequest;

// TODO: we currently support float arrays only
public final class Renderable implements IRenderable {
	private IRenderer.Pass pass;
	private EnumSet<IRenderer.Flag> flags;
	private IShader shader;

	private List<? extends IArrayAttributeProvider> arrayAttributeProviders;

	private List<IUniformAttribute> uniformAttributes = new ArrayList<>();

	private List<IArrayAttribute> arrayAttributes = new ArrayList<>();
	private FloatArrayBuffer buffer = new FloatArrayBuffer();

	private int[] sizes;
	private int stride;

	private UpdateRequest updater = new UpdateRequest();

	public Renderable(IRenderer.Pass pass, EnumSet<IRenderer.Flag> flags, IShader shader, IUniformAttributeProvider uniformAttributeProvider,
			List<? extends IArrayAttributeProvider> arrayAttributeProviders) {
		this.pass = pass;
		this.flags = flags;
		this.shader = shader;
		this.arrayAttributeProviders = arrayAttributeProviders;

		createAttributes(uniformAttributeProvider, arrayAttributeProviders);
	}

	@Override
	public void dispose(GL3 gl) {
		shader.dispose(gl);
		buffer.dispose(gl);

		pass = null;
		flags = null;
		shader = null;

		uniformAttributes = null;

		arrayAttributes = null;
		buffer = null;
		stride = 0;

		updater = null;
	}

	@Override
	public void update(GL3 gl, IView view, FloatList dst) {
		if (updater.needsUpdate()) {
			dst.clear();
			shader.update(gl);
			loadBuffer(gl, dst);
		}
	}

	@Override
	public void render(GL3 gl, IView view, IRenderer.RenderState state) {
		// TODO: generally, sorting of any kind is not implemented yet...
		// e.g. sort by program, shader params, instance params, ... (we should come up with some flexible mechanism for
		// implementing sorting strategies)

		// 1. enable program
		shader.enable(gl);
		Program program = shader.getProgram();

		// 2. for each uniform attribute
		// 2.1. set uniform (shader index, value), enable textures, set gl state
		for (IUniformAttribute attr : uniformAttributes) {
			attr.enable(gl, program);
		}

		// //// TODO: repeat for multiple sequential buffers (limited vbo size)

		// 3. for each parallel buffer (TODO: currently only one float buffer used)
		// 3.1. bind buffer
		// 3.2. for each array attribute associated to the buffer
		// 3.2.1 enable vertex attrib array (shader index)
		// 3.2.2 set vertex attrib pointer (shader index, size, stride, offset)
		// NOTE currently we only use one float buffer, and also don't limit the length
		buffer.bind(gl);
		for (IArrayAttribute attr : arrayAttributes) {
			attr.enable(gl, program, buffer);
		}

		// 4. draw arrays (# elements = buffer size / stride)
		shader.render(gl, buffer.size() / stride);

		// 5. for each buffer
		// 5.1. bind buffer
		// 5.2. for each array attribute
		// 5.2.1 disable vertex attrib array (shader index)
		buffer.bind(gl);
		for (IArrayAttribute attr : arrayAttributes) {
			attr.disable(gl, program, buffer);
		}

		// //// TODO: repeat for multiple sequential buffers (limited vbo size)

		// 6. for each uniform attribute
		// 6.1. disable textures, restore gl state
		for (IUniformAttribute attr : uniformAttributes) {
			attr.disable(gl, program);
		}

		// 7. disable program and clear buffer binding
		shader.disable(gl);

		FloatArrayBuffer.unbind(gl);
	}

	@Override
	public void requestRefresh() {
		// NOTE: requestRefresh() is immediate. call it after you modify the array attribute provider list (e.g. add / remove)
		createAttributes(null, arrayAttributeProviders);
	}

	@Override
	public void requestUpdate() {
		// NOTE: requestUpdate() is deferred, i.e. update will be called during render/update cycle
		updater.requestUpdate();
	}

	@Override
	public IRenderer.Pass getPass() {
		return pass;
	}
	
	@Override
	public List<? extends IArrayAttributeProvider> getArrayAttributeProviders() {
		return arrayAttributeProviders;
	}

	@Override
	public boolean containsFlag(IRenderer.Flag flag) {
		return flags.contains(flag);
	}

	@Override
	public String toString() {
		// TODO: incomplete...
		return "renderable[pass=" + pass + " shader=" + shader + " stride=" + stride + "]";
	}

	private void createAttributes(IUniformAttributeProvider uniformAttributeProvider, List<? extends IArrayAttributeProvider> arrayAttributeProviders) {
		class Suppliers implements IAttribute.ISuppliers {
			private final Map<String, Supplier<?>> map = new HashMap<>();

			@Override
			public void add(String id, Supplier<?> supplier) {
				if (map.put(id, supplier) != null)
					throw new IllegalArgumentException("duplicate attribute: " + id);
			}

			@Override
			public Supplier<?> get(String id) {
				Supplier<?> supplier = map.get(id);
				if (supplier == null)
					throw new IllegalArgumentException("attribute not provided: " + id);
				return supplier;
			}

			void clear() {
				map.clear();
			}

			@Override
			public String toString() {
				String s = "";
				for (Entry<String, Supplier<?>> e : map.entrySet()) {
					s += "[" + e.getKey() + ", " + e.getValue() + "] ";
				}
				return s;
			}
		}

		Suppliers suppliers = new Suppliers();

		// 1. handle uniform attributes

		if (uniformAttributeProvider != null) {
			uniformAttributes.clear();
			shader.getUniformAttributes(uniformAttributes);
			uniformAttributeProvider.getAttributeSuppliers(suppliers);
			for (IUniformAttribute attr : uniformAttributes) {
				if (!attr.hasSupplier()) {
					attr.setSupplier(suppliers.get(attr.id()));
				}
			}
		}

		// 2. handle array attributes

		if (arrayAttributeProviders != null) {
			arrayAttributes.clear();

			// 2.1 initialize stride, sizes, offsets
			shader.getArrayAttributes(arrayAttributes);
			stride = 0;
			sizes = new int[arrayAttributes.size()];
			int i = 0;
			for (IArrayAttribute attr : arrayAttributes) {
				stride += sizes[i++] = attr.getNumComponents().get();
			}
			i = 0;
			int offset = 0;
			for (IArrayAttribute attr : arrayAttributes) {
				attr.setup(stride, offset);
				offset += sizes[i++];
			}

			// 2.2 setup suppliers for each provider
			for (IArrayAttributeProvider provider : arrayAttributeProviders) {
				suppliers.clear();
				provider.getAttributeSuppliers(shader.getPrimitiveType(), suppliers);
				for (IArrayAttribute attr : arrayAttributes) {
					attr.addSupplier(suppliers.get(attr.id()));
				}
			}
		}
	}

	private void loadBuffer(GL3 gl, FloatList dst) {
		int size = 0;
		FloatArrayAttribute attr = (FloatArrayAttribute) arrayAttributes.get(0);
		for (Supplier<float[]> supplier : attr.getSuppliers()) {
			size += supplier.get().length;
		}
		size = size / attr.getNumComponents().get() * stride;

		final float[] interleavedData = new float[size];
		final float[][] data = new float[arrayAttributes.size()][];
		

		int index = 0;
		for (int supplierIndex = 0; supplierIndex < attr.getSuppliers().size(); ++supplierIndex) {
			for (int attributeIndex = 0; attributeIndex < arrayAttributes.size(); ++attributeIndex) {
				data[attributeIndex] = ((FloatArrayAttribute) (arrayAttributes.get(attributeIndex))).getSuppliers().get(supplierIndex).get();
			}
			index = interleave(interleavedData, index, data, sizes);
		}
		
		dst.add(interleavedData);
		buffer.load(gl, FloatBuffer.wrap(dst.toArray()));
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
