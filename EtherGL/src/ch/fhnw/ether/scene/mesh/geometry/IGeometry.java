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

package ch.fhnw.ether.scene.mesh.geometry;

import ch.fhnw.ether.scene.attribute.AbstractAttribute;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.attribute.ITypedAttribute;
import ch.fhnw.util.IUpdateRequester;
import ch.fhnw.util.math.ITransformable;
import ch.fhnw.util.math.geometry.BoundingBox;

public interface IGeometry extends IAttributeProvider, ITransformable, IUpdateRequester {
	interface IGeometryAttribute<T> extends ITypedAttribute<T> {
	}

	final class GeometryAttribute<T> extends AbstractAttribute<T> implements IGeometryAttribute<T> {
		public GeometryAttribute(String id) {
			super(id);
		}
	}

	@FunctionalInterface
	interface IAttributeVisitor {
		/**
		 * Inspect or modify a specific attribute of a geometry.
		 */
		void visit(String attribute, float[] data);
	}

	@FunctionalInterface
	interface IAttributesVisitor {
		/**
		 * Inspect or modify attributes of a geometry through visitor. Note that in the current implementation, the
		 * attributes must not be changed, otherwise the mesh will result in an undefined state. It is however ok, to
		 * replace all attribute data arrays with new arrays, e.g. of different size.
		 */
		void visit(String[] attributes, float[][] data);
	}

	enum Primitive {
		POINTS, LINES, TRIANGLES;
	}
	
	// default geometry attributes
	
	// position array (note that this attribute is mandatory)
	GeometryAttribute<float[]> POSITION_ARRAY = new GeometryAttribute<>("builtin.material.position_array");

	// non-shaded objects
	GeometryAttribute<float[]> COLOR_ARRAY = new GeometryAttribute<>("builtin.material.color_array");

	// texture maps
	GeometryAttribute<float[]> COLOR_MAP_ARRAY = new GeometryAttribute<>("builtin.material.color_map_array");

	// triangles only: normals & shading
	GeometryAttribute<float[]> NORMAL_ARRAY = new GeometryAttribute<>("builtin.material.normal_array");

	// lines only: line width
	GeometryAttribute<float[]> LINE_WIDTH_ARRAY = new GeometryAttribute<>("builtin.material.line_width_array");

	// points only: point size
	GeometryAttribute<float[]> POINT_SIZE_ARRAY = new GeometryAttribute<>("builtin.material.point_size_array");
	

	/**
	 * @return primitive type of this geometry
	 */
	Primitive getType();

	/**
	 * @return axis-aligned bounding box of this geometry
	 */
	BoundingBox getBounds();

	/**
	 * Inspect specific attribute of this geometry through visitor. Note that this will inspect the transformed vertices
	 * and normals. Must not modify geometry.
	 */
	void inspect(int index, IAttributeVisitor visitor);

	/**
	 * Inspect all attributes of this geometry through visitor. Note that this will inspect the transformed vertices and
	 * normals. Must not modify geometry.
	 */
	void inspect(IAttributesVisitor visitor);

	/**
	 * Modify specific attribute of this geometry through visitor. Note that this will modify the original (i.e.
	 * untransformed) vertices and normals.
	 */
	void modify(int index, IAttributeVisitor visitor);

	/**
	 * Modify any attribute of this geometry through visitor. Note that this will modify the original (i.e.
	 * untransformed) vertices and normals.
	 */
	void modify(IAttributesVisitor visitor);

}
