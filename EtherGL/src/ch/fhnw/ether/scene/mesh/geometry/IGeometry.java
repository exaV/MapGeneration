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

import ch.fhnw.ether.scene.mesh.IAttributeProvider;
import ch.fhnw.util.IUpdateRequester;
import ch.fhnw.util.math.ITransformable;
import ch.fhnw.util.math.geometry.BoundingBox;

public interface IGeometry extends IAttributeProvider, ITransformable, IUpdateRequester {
	enum PrimitiveType {
		POINTS, LINES, TRIANGLES;
	}

	@FunctionalInterface
	public interface IAttributeVisitor {
		/**
		 * @return true if the visitor has changed the attribute data
		 */
		boolean visit(PrimitiveType type, String attribute, float[] data);
	}

	@FunctionalInterface
	public interface IAttributesVisitor {
		/**
		 * @return true if the visitor has changed the attribute data. Note that in the current implementation, the
		 *         attributes must not be changed, otherwise the mesh will result in an undefined state. It is however
		 *         ok, to replace all attribute data arrays with new arrays, e.g. of different size.
		 */
		boolean visit(PrimitiveType type, String[] attributes, float[][] data);
	}

	/**
	 * @return primitive type of this geometry
	 */
	PrimitiveType getPrimitiveType();

	/**
	 * @return axis-aligned bounding box of this geometry
	 */
	BoundingBox getBounds();

	/**
	 * Inspect specific attribute of this geometry through visitor
	 */
	void accept(int index, IAttributeVisitor visitor);

	/**
	 * Inspect all attributes of this geometry through visitor
	 */
	void accept(IAttributesVisitor visitor);
}
