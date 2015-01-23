package ch.fhnw.ether.midi;

import javax.sound.midi.MidiMessage;

import ch.fhnw.ether.media.AbstractFrame;

public class MidiFrame extends AbstractFrame {
	public final MidiMessage[] messages;

	public MidiFrame(double playOutTime, MidiMessage ... messages) {
		super(playOutTime);
		this.messages = messages;
	}
}
