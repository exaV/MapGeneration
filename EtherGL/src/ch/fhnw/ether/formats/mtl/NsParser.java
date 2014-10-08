package ch.fhnw.ether.formats.mtl;

import ch.fhnw.ether.formats.obj.LineParser;
import ch.fhnw.ether.formats.obj.Material;
import ch.fhnw.ether.formats.obj.WavefrontObject;

public class NsParser extends LineParser {

	float ns;

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		Material currentMaterial = wavefrontObject.getCurrentMaterial();
		currentMaterial.setShininess(ns);

	}

	@Override
	public void parse() {
		try {
			ns = Float.parseFloat(words[1]);
		} catch (Exception e) {
			throw new RuntimeException("VertexParser Error");
		}
	}

}
