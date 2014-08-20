package ch.fhnw.ether.geom;

public interface IColor {
	float   red();
	float   green();
	float   blue();
	float   alpha();
	
	default float[] toArray() {
		return new float[] {red(), green(), blue(), alpha()};
	}
}
