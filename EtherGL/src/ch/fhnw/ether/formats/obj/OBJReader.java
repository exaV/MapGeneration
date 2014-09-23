package ch.fhnw.ether.formats.obj;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ether.formats.AbstractModelReader;
import ch.fhnw.ether.geom.GeometryUtil;
import ch.fhnw.ether.geom.RGB;
import ch.fhnw.ether.geom.RGBA;
import ch.fhnw.ether.geom.Vec3;
import ch.fhnw.ether.model.GenericMesh;
import ch.fhnw.ether.model.IGeometry;
import ch.fhnw.util.FloatList;
import ch.fhnw.util.IntList;

public class OBJReader extends AbstractModelReader {

	public OBJReader(URL resource) throws IOException {
		super(resource);
	}

	@Override
	protected List<IGeometry> decode(String path, InputStream in) throws IOException {
		List<IGeometry> meshes = new ArrayList<IGeometry>();
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

			GenericMesh mesh = new GenericMesh();
			mesh.setName(path + '/' + g.getName());
			Material mat = g.getMaterial();
			RGB diffuse = mat.getKd();
			mesh.setLines(edgVertices.toArray(), RGBA.WHITE);
			float[] triv = triVertices.toArray();
			mesh.setTriangles(triv, triNormals.size() == triv.length ? triNormals.toArray() : GeometryUtil.calculateNormals(triv), diffuse);
			meshes.add(mesh);
		}
		return meshes;
	}
}
