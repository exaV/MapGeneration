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

import ch.fhnw.util.math.Vec3;

public class FaceParser extends LineParser {

	private Face face;
	public int[] vindices;
	public int[] nindices;
	public int[] tindices;
	private Vec3[] vertices;
	private Vec3[] normals;
	private TexCoord[] textures;
	private WavefrontObject object = null;

	public FaceParser(WavefrontObject object) {
		this.object = object;
	}

	@Override
	public void parse() {
		face = new Face();
		parseLine(words.length - 1);
	}

	private void parseLine(int vertexCount) {
		String[] rawFaces = null;
		int currentValue;

		vindices = new int[vertexCount];
		nindices = new int[vertexCount];
		tindices = new int[vertexCount];
		vertices = new Vec3[vertexCount];
		normals = new Vec3[vertexCount];
		textures = new TexCoord[vertexCount];

		for (int i = 1; i <= vertexCount; i++) {
			rawFaces = words[i].split("/");

			// v
			currentValue = Integer.parseInt(rawFaces[0]);
			vindices[i - 1] = currentValue - 1;
			// save vertex
			vertices[i - 1] = object.getVertices().get(currentValue - 1); // -1 because references starts at 1

			if (rawFaces.length == 1) {
				continue;
			}

			// save texcoords
			if (!"".equals(rawFaces[1])) {
				currentValue = Integer.parseInt(rawFaces[1]);
				// System.out.println( currentValue+" at line: " + lineCounter);
				// This is to compensate the fact that if no texture is
				// in the obj file, sometimes '1' is put instead of
				// 'blank' (we find coord1/1/coord3 instead of
				// coord1//coord3 or coord1/coord3)
				if (currentValue <= object.getTextures().size()) {
					tindices[i - 1] = currentValue - 1;
					textures[i - 1] = object.getTextures().get(currentValue - 1); // -1 because references starts at 1
				}
				// System.out.print("indice="+currentValue+" ="+textures[i-1].getU() + ","+textures[i-1].getV());
				// System.out.println(textures[i-1].getU()+"-"+textures[i-1].getV());
			}

			// save normal
			currentValue = Integer.parseInt(rawFaces[2]);

			nindices[i - 1] = currentValue - 1;
			normals[i - 1] = object.getNormals().get(currentValue - 1); // -1 because references starts at 1
		}
		// System.out.println("");
	}

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {

		// wavefrontObject.getFaces().add(face);
		Group group = wavefrontObject.getCurrentGroup();

		if (group == null) {
			group = new Group("Default created by loader");
			wavefrontObject.getGroups().add(group);
			wavefrontObject.getGroupsDirectAccess().put(group.getName(), group);
			wavefrontObject.setCurrentGroup(group);
		}

		if (this.vertices.length == 3) {
			// Add list of vertex/normal/texcoord to current group
			// Each object keeps a list of its own data, apart from the global list
			group.vertices.add(this.vertices[0]);
			group.vertices.add(this.vertices[1]);
			group.vertices.add(this.vertices[2]);
			group.normals.add(this.normals[0]);
			group.normals.add(this.normals[1]);
			group.normals.add(this.normals[2]);
			group.texcoords.add(this.textures[0]);
			group.texcoords.add(this.textures[1]);
			group.texcoords.add(this.textures[2]);
			group.indices.add(group.indexCount++);
			group.indices.add(group.indexCount++);
			group.indices.add(group.indexCount++); // create index list for current object

			face.vertIndices = vindices;
			face.normIndices = nindices;
			face.texIndices = tindices;
			face.setNormals(this.normals);
			face.setNormals(this.normals);
			face.setVertices(this.vertices);
			face.setTextures(this.textures);

			wavefrontObject.getCurrentGroup().addFace(face);
		} else {
			// Add list of vertex/normal/texcoord to current group
			// Each object keeps a list of its own data, apart from the global list
			group.vertices.add(this.vertices[0]);
			group.vertices.add(this.vertices[1]);
			group.vertices.add(this.vertices[2]);
			group.vertices.add(this.vertices[3]);
			group.normals.add(this.normals[0]);
			group.normals.add(this.normals[1]);
			group.normals.add(this.normals[2]);
			group.normals.add(this.normals[3]);
			group.texcoords.add(this.textures[0]);
			group.texcoords.add(this.textures[1]);
			group.texcoords.add(this.textures[2]);
			group.texcoords.add(this.textures[3]);
			group.indices.add(group.indexCount++);
			group.indices.add(group.indexCount++);
			group.indices.add(group.indexCount++);
			group.indices.add(group.indexCount++); // create index list for current object

			face.vertIndices = vindices;
			face.normIndices = nindices;
			face.texIndices = tindices;
			face.setNormals(this.normals);
			face.setNormals(this.normals);
			face.setVertices(this.vertices);
			face.setTextures(this.textures);

			wavefrontObject.getCurrentGroup().addFace(face);
		}
	}

	static int faceC = 0;
}
