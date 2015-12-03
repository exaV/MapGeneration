package ch.fhnw.ether.video;

public interface IVideoSource {
	long   getLengthInFrames();	
	double getLengthInSeconds();
	float  getFrameRate();

	int getWidth();
	int getHeight();	
}
