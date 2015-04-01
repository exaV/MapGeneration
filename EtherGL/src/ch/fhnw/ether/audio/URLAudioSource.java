/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.audio;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.fhnw.ether.media.PerTargetState;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.util.TextUtilities;


public class URLAudioSource extends AbstractAudioSource<URLAudioSource.State> {
	private static final float       S2F    = Short.MAX_VALUE;
	private static final double      SEC2US = 1000000;
	private static final MidiEvent[] EMPTY_MidiEventA = new MidiEvent[0];
	
	private final URL                url;
	private final AudioFormat        fmt;       
	private final int                numPlays;
	private final long               frameCount;
	private int                      noteOn;
	private final TreeSet<MidiEvent> notes = new TreeSet<>(new Comparator<MidiEvent>() {
		@Override
		public int compare(MidiEvent o1, MidiEvent o2) {
			int   result  = (int) (o1.getTick() - o2.getTick());
			return result == 0 ? o1.getMessage().getMessage()[1] - o2.getMessage().getMessage()[1] : result;
		}
	});

	public URLAudioSource(URL url) throws IOException {
		this(url, Integer.MAX_VALUE);
	}

	public URLAudioSource(final URL url, final int numPlays) throws IOException {
		this.url      = url;
		this.numPlays = numPlays;

		try {
			if(TextUtilities.hasFileExtension(url.getPath(), "mid")) {
				Sequence seq = MidiSystem.getSequence(url);
								
				send(seq, new Receiver() {
					@Override
					public void send(MidiMessage message, long timeStamp) {
						if(message instanceof ShortMessage && (message.getMessage()[0] & 0xFF) == ShortMessage.NOTE_ON && (message.getMessage()[2] > 0))
							noteOn++;
						if(message instanceof ShortMessage && (
								(message.getMessage()[0] & 0xFF) == ShortMessage.NOTE_ON ||
								(message.getMessage()[0] & 0xFF) == ShortMessage.NOTE_OFF))
							notes.add(new MidiEvent(message, timeStamp));
					}

					@Override
					public void close() {}
				});
			}
		} catch(InvalidMidiDataException e) {
			throw new IOException(e);
		}

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
				byte[] buffer = new byte[fmt.getChannels() * fmt.getSampleSizeInBits() / 8 * 1024];

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

	public MidiEvent[] getMidi(double time, double timeWindow) {
		long timeT   = (long) (time * SEC2US);
		long windowT = (long) (timeWindow * SEC2US);
		SortedSet<MidiEvent> result = notes.subSet(noteOn(0, 0, timeT - windowT / 2), noteOn(0, 0, timeT + windowT / 2));
		return result.isEmpty() ? EMPTY_MidiEventA : result.toArray(new MidiEvent[result.size()]);
	}

	private MidiEvent noteOn(int key, int velocity, long ticks) {
		try {
			return new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON,  0, key, velocity), ticks);
		} catch(InvalidMidiDataException e) {
			return null;
		}
	}

	private double send(Sequence seq, Receiver recv) {
		float divtype = seq.getDivisionType();
		assert (seq.getDivisionType() == Sequence.PPQ);
		Track[] tracks = seq.getTracks();
		int[] trackspos = new int[tracks.length];
		int mpq = (int)SEC2US / 2;
		int seqres = seq.getResolution();
		long lasttick = 0;
		long curtime = 0;
		while (true) {
			MidiEvent selevent = null;
			int seltrack = -1;
			for (int i = 0; i < tracks.length; i++) {
				int trackpos = trackspos[i];
				Track track = tracks[i];
				if (trackpos < track.size()) {
					MidiEvent event = track.get(trackpos);
					if (selevent == null
							|| event.getTick() < selevent.getTick()) {
						selevent = event;
						seltrack = i;
					}
				}
			}
			if (seltrack == -1)
				break;
			trackspos[seltrack]++;
			long tick = selevent.getTick();
			if (divtype == Sequence.PPQ)
				curtime += ((tick - lasttick) * mpq) / seqres;
			else
				curtime = tick;
			lasttick = tick;
			MidiMessage msg = selevent.getMessage();
			if (msg instanceof MetaMessage) {
				if (divtype == Sequence.PPQ)
					if (((MetaMessage) msg).getType() == 0x51) {
						byte[] data = ((MetaMessage) msg).getData();
						mpq = ((data[0] & 0xff) << 16)
								| ((data[1] & 0xff) << 8) | (data[2] & 0xff);
					}
			} else {
				if (recv != null)
					recv.send(msg, curtime);
			}
		}
		return curtime / SEC2US;
	}

	public int getNumNotes() {
		return noteOn;
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

	public double lengthInSeconds() {
		return frameCount / fmt.getFrameRate();
	}
}
