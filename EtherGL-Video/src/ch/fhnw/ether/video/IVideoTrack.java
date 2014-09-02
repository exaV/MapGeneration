package ch.fhnw.ether.video;

import java.net.URL;

public interface IVideoTrack {

	void dispose();
	
	URL getURL();

	double getDuration();

	double getFrameRate();

	long getFrameCount();

	int getWidth();

	int getHeight();
}
