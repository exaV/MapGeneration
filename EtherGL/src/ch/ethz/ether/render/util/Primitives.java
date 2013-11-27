package ch.ethz.ether.render.util;

public final class Primitives {
	public static void addRectangle(IAddOnlyFloatList dst, float x0, float y0, float x1, float y1) {
		addRectangle(dst, x0, y0, x1, y1, 0);
	}
	
	public static void addRectangle(IAddOnlyFloatList dst, float x0, float y0, float x1, float y1, float z) {
		dst.add(x0, y0, z);
		dst.add(x1, y0, z);
		dst.add(x1, y1, z);
		
		dst.add(x0, y0, z);
		dst.add(x1, y1, z);
		dst.add(x0, y1, z);
	}
	
	public static void addLine(IAddOnlyFloatList dst, float x0, float y0, float x1, float y1) {
		dst.add(x0, y0, 0);
		dst.add(x1, y1, 0);
	}	

	public static void addLine(IAddOnlyFloatList dst, float x0, float y0, float z0, float x1, float y1, float z1) {
		dst.add(x0, y0, z0);
		dst.add(x1, y1, z1);
	}	
}
