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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.attribute.base.FloatArrayAttribute;
import ch.fhnw.ether.render.gl.FloatArrayBuffer;
import ch.fhnw.ether.render.gl.Program;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.IShader.Attributes;
import ch.fhnw.ether.render.shader.builtin.LineShader;
import ch.fhnw.ether.render.shader.builtin.PointShader;
import ch.fhnw.ether.render.shader.builtin.UnshadedTriangleShader;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.material.CustomMaterial;
import ch.fhnw.util.FloatList;

// FIXME: deal with max vbo size & multiple vbos, handle non-float arrays

public final class Renderable {
	private IMesh mesh;
	private IShader shader;

	private List<IUniformAttribute> uniformAttributes = new ArrayList<>();
	private List<IArrayAttribute> arrayAttributes = new ArrayList<>();
	private FloatArrayBuffer buffer = new FloatArrayBuffer();

	private int[] sizes;
	private int stride;

	public Renderable(IMesh mesh, AttributeProviders providers) {
		this.mesh = mesh;

		createAttributes(providers);

		// make sure update flag is set, so everything get initialized on the next render cycle
		mesh.requestUpdate(null);
	}

	public void dispose(GL3 gl) {
		shader.dispose(gl);
		uniformAttributes.forEach((t) -> t.dispose(gl));
		arrayAttributes.forEach((t) -> t.dispose(gl));
		buffer.dispose(gl);

		mesh = null;
		shader = null;

		uniformAttributes = null;

		arrayAttributes = null;
		buffer = null;
		stride = 0;
	}

	public void update(GL3 gl, FloatList dst) {
		if (mesh.needsUpdate()) {
			dst.clear();
			shader.update(gl);
			loadBuffer(gl, dst);
		}
	}

	public void render(GL3 gl) {
		// 1. enable program
		shader.enable(gl);
		Program program = shader.getProgram();

		// 2. for each uniform attribute
		// 2.1. set uniform (shader index, value), enable textures, set gl state
		for (IUniformAttribute attr : uniformAttributes) {
			attr.enable(gl, program);
		}

		// FIXME: use VAO here
		// 3. for each parallel buffer
		// 3.1. bind buffer
		// 3.2. for each array attribute associated to the buffer
		// 3.2.1 enable vertex attrib array (shader index)
		// 3.2.2 set vertex attrib pointer (shader index, size, stride, offset)
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

		// 6. for each uniform attribute
		// 6.1. disable textures, restore gl state
		for (IUniformAttribute attr : uniformAttributes) {
			attr.disable(gl, program);
		}

		// 7. disable program and clear buffer binding
		shader.disable(gl);

		FloatArrayBuffer.unbind(gl);
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

	private void createAttributes(AttributeProviders providers) {
		class Suppliers implements IAttributeProvider.ISuppliers {
			private final Map<String, Supplier<?>> providedAttributes = new HashMap<>();
			private final Set<String> requiredAttibutes = new HashSet<>();

			@Override
			public void provide(IAttribute attribute, Supplier<?> supplier) {
				provide(attribute.id(), supplier);
			}

			@Override
			public void provide(String id, Supplier<?> supplier) {
				if (providedAttributes.put(id, supplier) != null)
					throw new IllegalArgumentException("duplicate attribute: " + id);
			}

			@Override
			public void require(IAttribute attribute) {
				require(attribute.id());
			}

			@Override
			public void require(String id) {
				requiredAttibutes.add(id);
			}

			Supplier<?> get(String id) {
				Supplier<?> supplier = providedAttributes.get(id);
				if (supplier == null)
					throw new IllegalArgumentException("attribute not provided: " + id);
				return supplier;
			}

			void clear() {
				providedAttributes.clear();
			}

			@Override
			public String toString() {
				String s = "";
				for (Entry<String, Supplier<?>> e : providedAttributes.entrySet()) {
					s += "[" + e.getKey() + ", " + e.getValue() + "] ";
				}
				return s;
			}
		}

		Suppliers suppliers = new Suppliers();

		// FIXME: here's a bug - only those arrays required by material should be used, but currently all arrays that a
		// geometry provides are used

		// 0. get all attributes, and check if all required attributes are present

		providers.getAttributeSuppliers(suppliers);
		mesh.getMaterial().getAttributeSuppliers(suppliers);
		mesh.getGeometry().getAttributeSuppliers(suppliers);

		for (String id : suppliers.requiredAttibutes) {
			if (!suppliers.providedAttributes.containsKey(id))
				throw new IllegalStateException("geometry does not provide required attribute " + id);
		}

		// 1. create shader and get all attributes this shader requires

		createShader(new Attributes(suppliers.providedAttributes.keySet()));

		shader.getAttributes(uniformAttributes, arrayAttributes);

		// 2. bind shader attributes to provided global attributes (matrices, lights), material and geometry

		// 2.1. handle uniform attributes

		for (IUniformAttribute attr : uniformAttributes) {
			if (!attr.hasSupplier()) {
				attr.setSupplier(suppliers.get(attr.id()));
			}
		}

		// 2.2. handle array attributes

		// FIXME: currently mesh only contains one geometry
		List<? extends IAttributeProvider> arrayAttributeProviders = Collections.singletonList(mesh.getGeometry());
		if (arrayAttributeProviders != null) {

			// 2.1 initialize stride, sizes, offsets
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
			for (IAttributeProvider provider : arrayAttributeProviders) {
				suppliers.clear();
				provider.getAttributeSuppliers(suppliers);
				for (IArrayAttribute attr : arrayAttributes) {
					attr.addSupplier(suppliers.get(attr.id()));
				}
			}
		}
	}

	private void loadBuffer(GL3 gl, FloatList dst) {
		// FIXME: fix memory management/allocation throughout (+ thread safe)
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

	// FIXME: make more flexible/dynamic (as soon as we have more builtin shaders): derive shader from attributes
	private void createShader(Attributes attributes) {
		if (mesh.getMaterial() instanceof CustomMaterial) {
			shader = ((CustomMaterial) mesh.getMaterial()).getShader();
			return;
		}

		switch (mesh.getGeometry().getType()) {
		case POINTS:
			shader = new PointShader(attributes);
			break;
		case LINES:
			shader = new LineShader(attributes);
			break;
		case TRIANGLES:
			shader = new UnshadedTriangleShader(attributes);
			break;
		}
	}

}
