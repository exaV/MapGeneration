package ch.fhnw.ether.media;

public abstract class AbstractFX implements IFX {
	protected final FXParameter[] parameters;

	protected AbstractFX(FXParameter ... parameters) {
		this.parameters = new FXParameter[parameters.length];
		for(int i = 0; i < parameters.length; i++) {
			parameters[i].setIdx(i);
			this.parameters[i] = parameters[i].copy();
		}
	}
	
	@Override
	public FXParameter[] getParameters() {
		return parameters;
	}

	@Override
	public String getName(FXParameter p) {
		return parameters[p.getIdx()].getName();
	}

	@Override
	public String getDescription(FXParameter p) {
		return parameters[p.getIdx()].getDescription();
	}

	@Override
	public float getMin(FXParameter p) {
		return parameters[p.getIdx()].getMin();
	}

	@Override
	public float getMax(FXParameter p) {
		return parameters[p.getIdx()].getMax();
	}

	@Override
	public float getVal(FXParameter p) {
		return parameters[p.getIdx()].getVal();
	}

	@Override
	public void setVal(FXParameter p, float val) {
		parameters[p.getIdx()].setVal(val);
	}
}
