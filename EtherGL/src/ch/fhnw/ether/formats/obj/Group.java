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

package ch.fhnw.ether.formats.obj;

import java.util.ArrayList;

import ch.fhnw.util.IntList;
import ch.fhnw.util.math.Vec3;

public class Group {

	private String name;
	private Vec3 min = null;
	private Material material;
	private ArrayList<Face> faces = new ArrayList<Face>();

	public IntList indices = new IntList();
	public ArrayList<Vec3> vertices = new ArrayList<Vec3>();
	public ArrayList<Vec3> normals = new ArrayList<Vec3>();
	public ArrayList<TexCoord> texcoords = new ArrayList<TexCoord>();
	public int indexCount;

	public Group(String name) {
		indexCount = 0;
		this.name = name;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void addFace(Face face) {
		faces.add(face);
	}

	public void pack() {
		float minX = 0;
		float minY = 0;
		float minZ = 0;
		Face currentFace = null;
		Vec3 currentVertex = null;
		for (int i = 0; i < faces.size(); i++) {
			currentFace = faces.get(i);
			for (int j = 0; j < currentFace.getVertices().length; j++) {
				currentVertex = currentFace.getVertices()[j];
				if (Math.abs(currentVertex.x) > minX)
					minX = Math.abs(currentVertex.x);
				if (Math.abs(currentVertex.y) > minY)
					minY = Math.abs(currentVertex.y);
				if (Math.abs(currentVertex.z) > minZ)
					minZ = Math.abs(currentVertex.z);
			}
		}

		min = new Vec3(minX, minY, minZ);
	}

	public String getName() {
		return name;
	}

	public Material getMaterial() {
		return material;
	}

	public ArrayList<Face> getFaces() {
		return faces;
	}

	public Vec3 getMin() {
		return min;
	}
}
