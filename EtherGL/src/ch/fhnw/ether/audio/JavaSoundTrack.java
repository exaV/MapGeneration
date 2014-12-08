package ch.fhnw.ether.audio;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.fhnw.ether.media.FrameReq;

public class JavaSoundTrack implements ISequentialFrameSource {
	private static final float S2F  = Short.MAX_VALUE;

	private float[]                data = new float[64 * 1024];
	private final URL              url;
	private final AudioInputStream in;
	private final AudioFormat      fmt;
	private       int              rdptr;
	private       int              size = 0;

	public JavaSoundTrack(URL url) throws IOException {
		try {
			this.url = url;
			this.in  = AudioSystem.getAudioInputStream(url);
			this.fmt = in.getFormat();

			checkFormat();

			Thread t = new Thread("AudioReader:" + url.toExternalForm()) {
				@Override
				public void run() {
					try {
						byte[] buffer = new byte[16 * 1024];
						for(;;) {
							int read = in.read(buffer);
							if(read < 0) break;								
							if(fmt.isBigEndian()) {
								for(int i = 0; i < read; i += 2) {
									int s        = buffer[i] << 8 | (buffer[i+1] & 0xFF);
									data[size++] = s / S2F;
									synchronized(data) {
										if(size >= data.length)
											data = Arrays.copyOf(data, data.length * 2);
									}
								}
							} else {
								for(int i = 0; i < read; i += 2) {
									int s        = buffer[i+1] << 8 | (buffer[i] & 0xFF);
									data[size++] = s / S2F;
									synchronized(data) {
										if(size >= data.length)
											data = Arrays.copyOf(data, data.length * 2);
									}
								}
							}
						}
						in.close();
					} catch(Throwable t) {
						t.printStackTrace();
					}
				}
			};
			t.setDaemon(true);
			t.start();
		} catch(UnsupportedAudioFileException e) {
			throw new IOException(e);
		}
	}

	private void checkFormat() throws IOException {
		if(fmt.getSampleSizeInBits() != 16)
			throw new IOException("Only 16 bit audio supported");
		if(fmt.getEncoding() != Encoding.PCM_SIGNED) 
			throw new IOException("Only signed PCM audio supported");
	}

	@Override
	public void dispose() {
	}

	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public double getDuration() {
		return in.getFrameLength() * fmt.getFrameRate();
	}

	@Override
	public double getFrameRate() {
		return fmt.getFrameRate();
	}

	@Override
	public long getFrameCount() {
		return in.getFrameLength();
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		req.processFrames((float[] buffer)->{
			synchronized (data) {
				for(int i = 0; i < buffer.length; i++)
					buffer[+i] = data[rdptr++ % size];
			}
		});
		return req;
	}

	@Override
	public int getChannelCount() {
		return fmt.getChannels();
	}
}
