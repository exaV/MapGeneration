package ch.fhnw.ether.formats.mtl;

import ch.fhnw.ether.formats.obj.LineParser;
import ch.fhnw.ether.formats.obj.Material;
import ch.fhnw.ether.formats.obj.WavefrontObject;

class MaterialParser extends LineParser {

	private String materialName = "";

	@Override
	public void incoporateResults(WavefrontObject wavefrontObject) {
		Material newMaterial = new Material(materialName);

		wavefrontObject.getMaterials().put(materialName, newMaterial);
		wavefrontObject.setCurrentMaterial(newMaterial);
	}

	@Override
	public void parse() {
		materialName = words[1];
	}

}
