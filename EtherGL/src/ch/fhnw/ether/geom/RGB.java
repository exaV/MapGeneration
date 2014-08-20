package ch.fhnw.ether.geom;

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
}
