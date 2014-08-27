package ch.fhnw.ether.formats.obj;


public class MaterialParser extends LineParser 
{
		String materialName = "";

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