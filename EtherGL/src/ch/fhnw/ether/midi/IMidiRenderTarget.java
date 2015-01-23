package ch.fhnw.ether.midi;

import ch.fhnw.ether.media.IRenderTarget;

public interface IMidiRenderTarget extends IRenderTarget {
	void      setFrame(MidiFrame frame);
	MidiFrame getFrame();
}
