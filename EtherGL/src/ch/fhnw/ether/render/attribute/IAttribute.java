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

package ch.fhnw.ether.render.attribute;

import java.util.function.Supplier;

import javax.media.opengl.GL;

public interface IAttribute {
	class IdSupplierPair {
		public final String id;
		public final Supplier<?> supplier;

		public IdSupplierPair(String id, Supplier<?> supplier) {
			this.id = id;
			this.supplier = supplier;
		}
	}

	interface ISuppliers {
		void add(String id, Supplier<?> supplier);

		void add(IdSupplierPair entry);

		Supplier<?> get(String id);
	}

	enum PrimitiveType {
		POINT(GL.GL_POINTS), LINE(GL.GL_LINES), TRIANGLE(GL.GL_TRIANGLES);

		private int mode;

		private PrimitiveType(int mode) {
			this.mode = mode;
		}

		public int getMode() {
			return mode;
		}
	}

	/*
	 * 
	 * enum BuiltinArray { // geometry array attributes (per vertex) // @formatter:off
	 * VERTEX_ARRAY("builtin.vertex_array", "vertexPosition", NumComponents.THREE), NORMAL_ARRAY("builtin.normal_array",
	 * "vertexNormal", NumComponents.THREE), TEX_COORD_ARRAY("builtin.tex_coord_array", "vertexTexCoord",
	 * NumComponents.TWO), COLOR_ARRAY("builtin.color_array", "vertexColor", NumComponents.FOUR),
	 * POINT_SIZE_ARRAY("builtin.point_size_array", "vertexPointSize", NumComponents.ONE), // @formatter:on ;
	 * 
	 * private final String name; private final String defaultShaderName; private final NumComponents numComponents;
	 * 
	 * private BuiltinArray(String name, String defaultShaderName, NumComponents numComponents) { this.name = name;
	 * this.defaultShaderName = defaultShaderName; this.numComponents = numComponents; }
	 * 
	 * public String getName() { return name; }
	 * 
	 * public String getDefaultShaderName() { return defaultShaderName; }
	 * 
	 * public NumComponents getNumComponents() { return numComponents; }
	 * 
	 * public IArrayAttribute create() { return new FloatArrayAttribute(this, defaultShaderName, numComponents); }
	 * 
	 * public IArrayAttribute create(String shaderName) { return new FloatArrayAttribute(this, shaderName,
	 * numComponents); }
	 * 
	 * @Override public String toString() { return getName(); } }
	 * 
	 * enum BuiltinUniform { // shader uniforms (environment etc) // @formatter:off PROJ_MATRIX("builtin.proj_matrix",
	 * "projMatrix", Mat4FloatUniformAttribute.class), VIEW_MATRIX("builtin.view_matrix", "viewMatrix",
	 * Mat4FloatUniformAttribute.class),
	 * 
	 * // geometry instance attributes (per geometry instance) TEXTURE("builtin.texture", "tex",
	 * SamplerUniformAttribute.class), // @formatter:on ;
	 * 
	 * private final String name; private final String defaultShaderName; private final Constructor<? extends
	 * IUniformAttribute> attributeConstructor;
	 * 
	 * private BuiltinUniform(String name, String defaultShaderName, Class<? extends IUniformAttribute> attributeClass)
	 * { this.name = name; this.defaultShaderName = defaultShaderName; try { this.attributeConstructor =
	 * attributeClass.getConstructor(Object.class, String.class, Supplier.class); } catch (Exception e) { throw new
	 * IllegalArgumentException(e.getMessage()); } }
	 * 
	 * public String getName() { return name; }
	 * 
	 * public String getDefaultShaderName() { return defaultShaderName; }
	 * 
	 * public IUniformAttribute create() { return create(defaultShaderName, null); }
	 * 
	 * public IUniformAttribute create(Supplier<?> supplier) { return create(defaultShaderName, supplier); }
	 * 
	 * public IUniformAttribute create(String shaderName, Supplier<?> supplier) { try { return
	 * attributeConstructor.newInstance(this, shaderName, supplier); } catch (Exception e) { throw new
	 * IllegalArgumentException("cannot create attribute: " + this); } }
	 * 
	 * @Override public String toString() { return getName(); } }
	 */

	String id();
}
