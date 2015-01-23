package ch.fhnw.ether.media;

public class Parameter {
	public static enum Type {RANGE, ITEMS};
	
	private final String   name;
	private final String   description;
	private final float    min;
	private final float    max;
	private final String[] items;
	private       float    val;
	private       int      idx = -1;

	public Parameter(String name, String description, float min, float max, float val) {
		this(name, description, min, max, val, null);
	}
	
	public Parameter(String name, String description, int val, String ... items) {
		this(name, description, Float.MIN_VALUE, Float.MAX_VALUE, val, items);
	}

	public Parameter(String name, String description, float min, float max, float val, String[] items) {
		this.name        = name;
		this.description = description;
		this.min         = min;
		this.max         = max;
		this.val         = val;
		this.items       = items;
	}

	private Parameter(Parameter p) {
		this(p.name, p.description, p.min, p.max, p.val, p.items);
		this.idx = p.idx;
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
	
	protected Parameter copy() {
		return new Parameter(this);
	}

	public Type getType() {
		return items == null ? Type.RANGE : Type.ITEMS; 
	}

	public String[] getItems() {
		return items;
	}
}
