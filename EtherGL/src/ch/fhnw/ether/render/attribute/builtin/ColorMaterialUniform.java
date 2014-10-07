package ch.fhnw.ether.render.attribute.builtin;

import java.util.function.Supplier;

import ch.fhnw.ether.render.attribute.base.Vec4FloatUniformAttribute;

public class ColorMaterialUniform extends Vec4FloatUniformAttribute {
	
	private final static String ID = "material.color";
	private static final String DEFAULT_SHADER_NAME = "materialColor";
	
	public ColorMaterialUniform() {
		super(ID, DEFAULT_SHADER_NAME);
	}
	
	public ColorMaterialUniform(Supplier<float[]> supplier) {
		super(ID, DEFAULT_SHADER_NAME, supplier);
	}
}
