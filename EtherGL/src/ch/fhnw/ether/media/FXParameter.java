package ch.fhnw.ether.media;

public class FXParameter {
	private final String name;
	private final String description;
	private final float  min;
	private final float  max;
	private       float  val;
	private       int    idx = -1;

	public FXParameter(String name, String description, float min, float max, float val) {
		this.name        = name;
		this.description = description;
		this.min         = min;
		this.max         = max;
		this.val         = val;
	}

	private FXParameter(FXParameter p) {
		this(p.name, p.description, p.min, p.max, p.val);
	}

	@Override
	final public String toString() {
		return name;
	}

	final public String getName() {
		return name;
	}

	final public String getDescription() {
		return description;
	}

	final public float getMin() {
		return min;
	}

	final public float getMax() {
		return max;
	}

	final float getVal() {
		return val;
	}

	final void setVal(float val) {
		this.val = val;
	}

	final void setIdx(int idx) {
		if(this.idx == idx) return;
		if(this.idx != -1)
			throw new IllegalArgumentException(name + ": parameter already in use");
		this.idx = idx;
	}

	final int getIdx() {
		return idx;
	}
	
	protected FXParameter copy() {
		return new FXParameter(this);
	}
}
