package ch.fhnw.util.color;

import ch.fhnw.util.math.Vec3;

public class RGB extends Vec3 implements IColor {
    public RGB(float red, float green, float blue) {
		super(red, green, blue);
	}

	@Override
	public float red() {
    	return x;
    }

    @Override
	public float green() {
    	return y;
    }

    @Override
	public float blue() {
    	return z;
    }

	@Override
	public float alpha() {
		return 1f;
	}
	
	@Override
	public float[] generateColorArray(int len) {
		float[] ret = new float[len*4];
		for(int i=0; i<ret.length; i+=3) {
			ret[i+0] = x;
			ret[i+1] = y;
			ret[i+2] = z;
		}
		return ret;
	}
}
