package ch.fhnw.ether.reorg.base;

import java.util.function.Supplier;

import ch.fhnw.ether.render.attribute.base.Vec4FloatUniformAttribute;

public class ColorMaterialAttribute extends Vec4FloatUniformAttribute {
	
	public final static String ID = "material.color";
	private static final String DEFAULT_SHADER_NAME = "materialColor";
	
	public ColorMaterialAttribute() {
		super(ID, DEFAULT_SHADER_NAME);
	}
	
	public ColorMaterialAttribute(Supplier<float[]> supplier) {
		super(ID, DEFAULT_SHADER_NAME, supplier);
	}
}
