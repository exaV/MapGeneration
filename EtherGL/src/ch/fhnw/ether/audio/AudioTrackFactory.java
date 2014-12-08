package ch.fhnw.ether.audio;

import java.io.IOException;
import java.net.URL;

public class AudioTrackFactory {
	public static ISequentialFrameSource createSequentialTrack(URL url) throws IOException {
		return new JavaSoundTrack(url);
	}
}
