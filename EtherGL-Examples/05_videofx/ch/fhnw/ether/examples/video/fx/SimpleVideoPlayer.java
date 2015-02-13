package ch.fhnw.ether.examples.video.fx;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JComboBox;

import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.RenderProgram;
import ch.fhnw.ether.ui.ParameterWindow;
import ch.fhnw.ether.video.AWTFrameTarget;
import ch.fhnw.ether.video.IVideoRenderTarget;
import ch.fhnw.ether.video.URLVideoSource;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class SimpleVideoPlayer {
	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException, RenderCommandException {
		URLVideoSource                    track    = new URLVideoSource(new File(args[0]).toURI().toURL());
		AWTFrameTarget                    videoOut = new AWTFrameTarget();

		AbstractVideoFX<?>[] fxs = {
				new AnalogTVFX(),
				new BandPass(),
				// new ChromaKey(mask, backdrop);
				new Convolution(),
				new FadeToColor(),
				new FakeThermoCam(),
				new MotionBlur(),
				new Posterize(),
				new RGBGain(),
		};
		AtomicInteger current = new AtomicInteger(0);

		final RenderProgram<IVideoRenderTarget> program = new RenderProgram<>(track, fxs[current.get()]);

		final JComboBox<AbstractVideoFX<?>> fxsUI = new JComboBox<>();
		for(AbstractVideoFX<?> fx : fxs)
			fxsUI.addItem(fx);
		fxsUI.addActionListener((ActionEvent e)->{
			int newIdx = fxsUI.getSelectedIndex();
			program.replace(fxs[current.get()], fxs[newIdx]);
			current.set(newIdx);
		});
		new ParameterWindow(fxsUI, program);

		videoOut.useProgram(program);
		videoOut.start();
		Thread.sleep(5 * 60 * 1000);
		videoOut.stop();
	}

}
