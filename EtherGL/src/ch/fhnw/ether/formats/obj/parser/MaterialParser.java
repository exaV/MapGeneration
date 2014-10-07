package ch.fhnw.ether.formats.obj.parser;

import ch.fhnw.ether.formats.obj.Material;


public class MaterialParser extends LineParser {
	private String materialName = "";

	@Override
	public void parse() {
		materialName = words[1];
	}

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		Material newMaterial = wavefrontObject.getMaterials().get(materialName);
		wavefrontObject.getCurrentGroup().setMaterial(newMaterial);
	}
}
