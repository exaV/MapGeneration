package ch.fhnw.ether.media;

public interface IFX {
	FXParameter[] getParameters();
	
	String getName(FXParameter p);
	String getDescription(FXParameter p);
	float  getMin(FXParameter p);
	float  getMax(FXParameter p);
	float  getVal(FXParameter p);
	void   setVal(FXParameter p, float val);
}
