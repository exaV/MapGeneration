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

package ch.fhnw.ether.geom.spatialindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.geom.BoundingBox;
import ch.fhnw.ether.reorg.api.IGeometry;

/**
 * A simple implementation of spatial index using array list
 */
class ArrayListSpatialIndex implements ISpatialIndex {
	private final List<IGeometry> geometries = new ArrayList<>();
	private BoundingBox bb;

	@Override
	public void addGeometry(IGeometry object) {
		geometries.add(object);
		bb = null;
	}

	@Override
	public boolean removeGeometry(IGeometry object) {
		boolean removed = geometries.remove(object);
		if (removed)
			bb = null;
		return removed;
	}

	@Override
	public List<IGeometry> getIntersectingGeometries(BoundingBox bb) {
		List<IGeometry> result = new ArrayList<>();
		for (IGeometry geometry : geometries) {
			if (bb.intersects(geometry.getBoundings()))
				result.add(geometry);
		}
		return result;
	}

	@Override
	public List<IGeometry> getContainingGeometries(BoundingBox bb) {
		List<IGeometry> result = new ArrayList<>();
		for (IGeometry geometry : geometries) {
			if (bb.contains(geometry.getBoundings()))
				result.add(geometry);
		}
		return result;
	}
	
	@Override
	public List<IGeometry> getGeometries() {
		return Collections.unmodifiableList(geometries);
	}

	@Override
	public int size() {
		return geometries.size();
	}

	@Override
	public BoundingBox getBounds() {
		return bb;
	}
}
