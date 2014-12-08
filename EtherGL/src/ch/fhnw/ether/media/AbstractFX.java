package ch.fhnw.ether.media;

public abstract class AbstractFX implements IFX {
	protected final FXParameter[] parameters;

	public AbstractFX(FXParameter ... parameters) {
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

	
	protected String getName(FXParameter p) {
		return parameters[p.getIdx()].getName();
	}

	protected String getDescription(FXParameter p) {
		return parameters[p.getIdx()].getDescription();
	}

	protected float getMin(FXParameter p) {
		return parameters[p.getIdx()].getMin();
	}

	protected float getMax(FXParameter p) {
		return parameters[p.getIdx()].getMax();
	}

	protected float getVal(FXParameter p) {
		return parameters[p.getIdx()].getVal();
	}

	protected void setVal(FXParameter p, float val) {
		parameters[p.getIdx()].setVal(val);
	}
}
