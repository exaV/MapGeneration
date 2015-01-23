package ch.fhnw.ether.midi;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

import ch.fhnw.ether.media.AbstractMediaTarget;
import ch.fhnw.ether.media.RenderCommandException;

public class JavaMidiSynthesizerTarget extends AbstractMediaTarget<MidiFrame,IMidiRenderTarget> implements IMidiRenderTarget {
	private final Synthesizer synth;
	private final MidiChannel ch;

	public JavaMidiSynthesizerTarget() throws MidiUnavailableException {
		super(Thread.MAX_PRIORITY);
		synth = MidiSystem.getSynthesizer();
		ch    = synth.getChannels()[0];
		synth.open();
	}

	@Override
	public void render() throws RenderCommandException {
		sleepUntil(getFrame().playOutTime);

		for(MidiMessage msg : getFrame().messages) {
			if(msg instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage)msg;
				switch(sm.getCommand()) {
				case ShortMessage.NOTE_ON:
					ch.noteOn(sm.getData1(), sm.getData2());
					continue;
				case ShortMessage.NOTE_OFF:
					ch.noteOff(sm.getData1(), sm.getData2());
					continue;
				case ShortMessage.PROGRAM_CHANGE:
					ch.programChange(sm.getData1());
					continue;
				case ShortMessage.CONTROL_CHANGE:
					ch.controlChange(sm.getData1(), sm.getData2());
					continue;
				}
			}
			throw new RenderCommandException("Unknown MIDI Command:" + MidiToString.toString(msg));
		}
	}
}
