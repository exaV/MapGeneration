package ch.fhnw.ether.audio;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ch.fhnw.ether.media.FrameException;
import ch.fhnw.ether.media.FrameReq;

public class SimplePlayer {
	private static final float S2F = Short.MAX_VALUE;

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, LineUnavailableException {
		IAudioFrameSource track = null;
		try {
			track = AudioTrackFactory.createSequentialTrack(new URL(args[0]));
		} catch(MalformedURLException e) {
			track = AudioTrackFactory.createSequentialTrack(new File(args[0]).toURI().toURL());
		}

		AudioFormat    FMT = new AudioFormat((float)track.getFrameRate(), 16, track.getChannelCount(), true, true); 
		SourceDataLine out = AudioSystem.getSourceDataLine(FMT);
		out.open(FMT);
		out.start();

		float[] fbuffer = new float[out.getBufferSize() / 2];
		byte[]  buffer  = new byte[fbuffer.length * 2];
		for(;;) {
			try {
				track.getFrames(new FrameReq(fbuffer));

				for(int i = 0; i < fbuffer.length; i++) {
					int s       = (int) (fbuffer[i] * S2F);
					if(s > Short.MAX_VALUE) s = Short.MAX_VALUE;
					if(s < Short.MIN_VALUE) s = Short.MIN_VALUE;
					buffer[i*2]   = (byte) (s >> 8);
					buffer[i*2+1] = (byte) s;
				}

				out.write(buffer, 0, buffer.length);
			} catch(FrameException e) {
				break;
			}
		}

		out.close();
		System.exit(0);
	}
}
