package ch.fhnw.ether.examples.audio.fx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import ch.fhnw.ether.audio.IAudioRenderTarget;
import ch.fhnw.ether.audio.JavaSoundTarget;
import ch.fhnw.ether.audio.URLAudioSource;
import ch.fhnw.ether.audio.fx.AudioGain;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.ui.ParameterWindow;


public class SimpleAudioPlayer {
	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException, RenderCommandException {
		URLAudioSource                    track   = new URLAudioSource(new File(args[0]).toURI().toURL());
		AudioGain                         gain    = new AudioGain();
		RenderProgram<IAudioRenderTarget> program = new RenderProgram<>(track, gain);
		
		new ParameterWindow(program);
		
		JavaSoundTarget audioOut = new JavaSoundTarget();
		audioOut.useProgram(program);
		audioOut.start();
		Thread.sleep(5 * 60 * 1000);
		audioOut.stop();
	}
}
