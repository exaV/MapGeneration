package ch.fhnw.ether.formats.mtl;

import ch.fhnw.ether.formats.obj.LineParser;
import ch.fhnw.ether.formats.obj.Material;
import ch.fhnw.ether.formats.obj.WavefrontObject;
import ch.fhnw.util.color.RGB;

public class KsParser extends LineParser {

	RGB ks = null;

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		Material currentMaterial = wavefrontObject.getCurrentMaterial();
		currentMaterial.setKs(ks);

	}

	@Override
	public void parse() {
		try {
			ks = new RGB(Float.parseFloat(words[1]), Float.parseFloat(words[2]), Float.parseFloat(words[3]));
		} catch (Exception e) {
			throw new RuntimeException("VertexParser Error");
		}
	}

}
