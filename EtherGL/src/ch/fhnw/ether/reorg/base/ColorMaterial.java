package ch.fhnw.ether.reorg.base;

import java.util.Arrays;

import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.reorg.api.IMaterial;
import ch.fhnw.util.color.RGBA;

public class ColorMaterial implements IMaterial {
	
	private final static ColorMaterialAttribute COLOR_ATTRIBUTE = new ColorMaterialAttribute();

	private float[] color;
	
	public ColorMaterial(RGBA color) {
		this.color = color.toArray();
	}
	
	public ColorMaterial(float r, float g, float b, float a) {
		this.color = new float[]{r,g,b,a};
	}
	
	public ColorMaterial(float[] rgba) {
		this.color = Arrays.copyOf(rgba, 4);
	}

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
		dst.add(COLOR_ATTRIBUTE.id(), () -> { return color; });
	}

}
