package ch.fhnw.ether.media;

public class FXParameter {
	private final String name;
	private final String description;
	private final float  min;
	private final float  max;
	private       float  val;
	
	public FXParameter(String name, String description, float min, float max, float val) {
		this.name        = name;
		this.description = description;
		this.min         = min;
		this.max         = max;
		this.val         = val;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public float getMin() {
		return min;
	}
	
	public float getMax() {
		return max;
	}
	
	public float getVal() {
		return val;
	}
	
	public void setVal(float val) {
		this.val = val;
	}
}
