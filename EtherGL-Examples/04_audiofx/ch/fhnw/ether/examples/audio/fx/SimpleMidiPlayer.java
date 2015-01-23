package ch.fhnw.ether.examples.audio.fx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.midi.JavaMidiSynthesizerTarget;
import ch.fhnw.ether.midi.URLMidiSource;


public class SimpleMidiPlayer {
	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException, InvalidMidiDataException, MidiUnavailableException {
		URLMidiSource             track     = new URLMidiSource(new File(args[0]).toURI().toURL());
		JavaMidiSynthesizerTarget synthOut  = new JavaMidiSynthesizerTarget();
		synthOut.useProgram(new RenderProgram<>(track));
		synthOut.start();
		Thread.sleep(5 * 60 * 1000);
		synthOut.stop();
	}
}
