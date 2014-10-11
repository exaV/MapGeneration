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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ether.formats.AbstractModelReader;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.scene.mesh.GenericMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.util.FloatList;
import ch.fhnw.util.IntList;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.GeometryUtil;

public class OBJReader extends AbstractModelReader {

	public OBJReader(URL resource) throws IOException {
		super(resource);
	}

	@Override
	protected List<IMesh> decode(String path, InputStream in) throws IOException {
		List<IMesh> meshes = new ArrayList<IMesh>();
		WavefrontObject obj = new WavefrontObject(path, in);
		for (Group g : obj.getGroups()) {
			FloatList edgVertices = new FloatList();
			FloatList triVertices = new FloatList();
			FloatList triNormals = new FloatList();
			FloatList polyVertices = new FloatList();

			for (Face f : g.getFaces()) {
				polyVertices.clear();

				final Vec3[] vs = f.getVertices();
				final Vec3[] ns = f.getNormals();

				for (int i = 1; i < vs.length; i++) {
					polyVertices.add(vs[i - 1].x, vs[i - 1].y, vs[i - 1].z);
					edgVertices.add(vs[i - 1].x, vs[i - 1].y, vs[i - 1].z);
					edgVertices.add(vs[i].x, vs[i].y, vs[i].z);
				}
				polyVertices.add(vs[vs.length - 1].x, vs[vs.length - 1].y, vs[vs.length - 1].z);

				edgVertices.add(vs[vs.length - 1].x, vs[vs.length - 1].y, vs[vs.length - 1].z);
				edgVertices.add(vs[0].x, vs[0].y, vs[0].z);

				IntList triangulation = GeometryUtil.triangualte(polyVertices.toArray());

				for (int i = 0; i < triangulation.size(); i++) {
					int idx = triangulation.get(i);
					triVertices.add(vs[idx].x, vs[idx].y, vs[idx].z);
					if (ns[idx] != null)
						triNormals.add(ns[idx].x, ns[idx].y, ns[idx].z);
				}
			}

			GenericMesh mesh = new GenericMesh(PrimitiveType.TRIANGLE);
			mesh.setName(path + '/' + g.getName());
			Material mat = g.getMaterial();
			RGB diffuse = mat.getKd();
			float[] triv = triVertices.toArray();
			mesh.setMaterial(new ColorMaterial(new RGBA(diffuse.x, diffuse.y, diffuse.z, 1)));
			mesh.setGeometry(triv, GeometryUtil.calculateNormals(triv), diffuse.generateColorArray(triv.length / 3));
			meshes.add(mesh);
		}
		return meshes;
	}
}
