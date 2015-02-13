package ch.fhnw.ether.audio;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;


public class URLAudioSource extends AbstractAudioSource<URLAudioSource.State> {
	private static final float S2F  = Short.MAX_VALUE;

	private final URL         url;
	private final AudioFormat fmt;       
	private final int         numPlays;
	private final long        frameCount;

	public URLAudioSource(URL url) throws IOException {
		this(url, Integer.MAX_VALUE);
	}

	public URLAudioSource(final URL url, final int numPlays) throws IOException {
		this.url      = url;
		this.numPlays = numPlays;

		try (AudioInputStream in = AudioSystem.getAudioInputStream(url)) {
			this.fmt   = in.getFormat();
			frameCount = in.getFrameLength();

			if(fmt.getSampleSizeInBits() != 16)
				throw new IOException("Only 16 bit audio supported");
			if(fmt.getEncoding() != Encoding.PCM_SIGNED) 
				throw new IOException("Only signed PCM audio supported");
		} catch (UnsupportedAudioFileException e) {
			throw new IOException(e);
		}
	}

	public URL getURL() {
		return url;
	}

	@Override
	protected void run(State state) throws RenderCommandException {
		state.runInternal();
	}

	@Override
	public float getSampleRate() {
		return fmt.getSampleRate();
	}

	class State extends PerTargetState<IAudioRenderTarget> implements Runnable {
		private final BlockingQueue<float[]> data = new LinkedBlockingQueue<>();
		private       double                 bufferSizeInSecs;
		private       int                    numPlays;
		private       double                 size2secs;
		private       long                   samples;

		public State(IAudioRenderTarget target, int numPlays) {
			super(target);

			this.numPlays = numPlays;
			Thread t      = new Thread(this, "AudioReader:" + url.toExternalForm());
			t.setDaemon(true);
			t.setPriority(Thread.MIN_PRIORITY);
			t.start();
		}

		@Override
		public void run() {
			try {
				byte[] buffer = new byte[16 * 1024];

				while(--numPlays >= 0) {
					try (AudioInputStream in = AudioSystem.getAudioInputStream(url)) {
						size2secs            = fmt.getSampleRate() * fmt.getChannels();
						int   bytesPerSample = fmt.getSampleSizeInBits() / 8;

						for(;;) {
							int read = in.read(buffer);
							if(read < 0) break;								
							float[] fbuffer = new float[read / bytesPerSample];
							int     idx     = 0;
							if(fmt.isBigEndian()) {
								for(int i = 0; i < read; i += 2) {
									int s = buffer[i] << 8 | (buffer[i+1] & 0xFF);
									fbuffer[idx++] = s / S2F;
								}
							} else {
								for(int i = 0; i < read; i += 2) {
									int s = buffer[i+1] << 8 | (buffer[i] & 0xFF);
									fbuffer[idx++] = s / S2F;
								}
							}
							data.add(fbuffer);
							bufferSizeInSecs += fbuffer.length / size2secs;
							while(bufferSizeInSecs > 5)
								Thread.sleep((long) (bufferSizeInSecs * 500));
						}
					}
				}
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}

		void runInternal() throws RenderCommandException {
			try {
				final float[] outData = data.take();
				bufferSizeInSecs -= outData.length / size2secs;
				getTarget().setFrame(createAudioFrame(samples, outData));
				samples += outData.length;
			} catch(Throwable t) {
				throw new RenderCommandException(t);
			}
		}
	}

	@Override
	protected State createState(IAudioRenderTarget target) {
		return new State(target, numPlays);
	}

	@Override
	public String toString() {
		return url.toString();
	}

	@Override
	public long getFrameCount() {
		return frameCount;
	}

	@Override
	public int getNumChannels() {
		return fmt.getChannels();
	}
}
