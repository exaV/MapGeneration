package ch.fhnw.ether.formats.obj;

import ch.fhnw.ether.geom.Vec3;

public class VertexParser extends LineParser {
	private Vec3 vertex = null;

	public VertexParser() {
	}

	@Override
	public void parse() {
		try {
			vertex = new Vec3(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3]));
		} catch (Exception e) {
			throw new RuntimeException("VertexParser Error");
		}
	}

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		wavefrontObject.getVertices().add(vertex);
	}

}
