package ch.fhnw.ether.formats.obj;

import ch.fhnw.util.math.Vec3;

class NormalParser extends LineParser {

	private Vec3 vertex = null;

	public NormalParser() {
	}

	@Override
	public void parse() {
		try {
			vertex = new Vec3(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3]));
		} catch (Exception e) {
			throw new RuntimeException("NormalParser Error");
		}

	}

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		wavefrontObject.getNormals().add(vertex);

	}

}
