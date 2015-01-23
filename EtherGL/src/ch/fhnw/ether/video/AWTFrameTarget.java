package ch.fhnw.ether.video;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import ch.fhnw.ether.media.AbstractMediaTarget;

public class AWTFrameTarget extends AbstractMediaTarget<VideoFrame,IVideoRenderTarget> implements IVideoRenderTarget, Runnable {
	private Canvas                         canvas;
	private AtomicReference<BufferedImage> image  = new AtomicReference<>();
	private boolean                        resize = true;

	public AWTFrameTarget() {
		super(Thread.MIN_PRIORITY);
		SwingUtilities.invokeLater(this);
	}

	@Override
	public void run() {
		java.awt.Frame frame = new java.awt.Frame();
		canvas = new Canvas() {
			private static final long serialVersionUID = -6659278265970264752L;

			@Override
			public void update(Graphics g) {
				if(image.get() == null) return;

				int w = image.get().getWidth();
				int h = image.get().getHeight();

				if(resize) {
					frame.setSize(w, h);
					resize = false;
				}
				g.drawImage(image.get(), 0, 0, getWidth(), getHeight(), 0, 0, w, h, this);
			}
		};
		frame.add(canvas);
		frame.setSize(64,64);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	@Override
	public void render() {
		image.set(getFrame().frame.toBufferedImage());
		sleepUntil(getFrame().playOutTime);
		if(canvas == null) return;
		canvas.repaint();
	}
}
