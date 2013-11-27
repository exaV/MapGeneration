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
package ch.ethz.ether.geom;

import java.util.Collection;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Axis aligned bounding box.
 * 
 * @author radar
 * 
 */
public final class BoundingBox {
	boolean valid;
	float minX;
	float maxX;
	float minY;
	float maxY;

	public BoundingBox() {
		reset();
	}

	public void reset() {
		valid = false;
		minX = Float.POSITIVE_INFINITY;
		maxX = Float.NEGATIVE_INFINITY;
		minY = Float.POSITIVE_INFINITY;
		maxY = Float.NEGATIVE_INFINITY;
	}

	public boolean isValid() {
		return valid;
	}

	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxY() {
		return maxY;
	}

	public float getExtentX() {
		return maxX - minX;
	}

	public float getExtentY() {
		return maxY - minY;
	}

	public void add(float x, float y) {
		minX = Math.min(minX, x);
		maxX = Math.max(maxX, x);
		minY = Math.min(minY, y);
		maxY = Math.max(maxY, y);
		valid = true;
	}

	public void add(double x, double y) {
		add((float) x, (float) y);
	}

	public void add(Vector2D vertex) {
		add(vertex.getX(), vertex.getY());
	}

	public void add(Collection<Vector2D> vertices) {
		for (Vector2D point : vertices)
			add(point);
	}

	public void add(float[] vertices) {
		for (int i = 0; i < vertices.length; i += 2) {
			add(vertices[i], vertices[i + 1]);
		}
	}

	public void add(double[] vertices) {
		for (int i = 0; i < vertices.length; i += 2) {
			add(vertices[i], vertices[i + 1]);
		}
	}

	public void add(BoundingBox b) {
		add(b.minX, b.minY);
		add(b.maxX, b.maxY);
	}

	@Override
	public String toString() {
		return valid ? "[" + minX + "," + maxX + "][" + minY + "," + maxY + "]" : "invalid";
	}
}
