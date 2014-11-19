package ch.fhnw.ether.video;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

import ch.fhnw.ether.image.Frame;

public class SimplePlayer extends Canvas implements Runnable {
	private static final long serialVersionUID = 2155924568412744207L;

	private static final double SEC2MS = 1000.0;
	
	private final ISequentialVideoTrack track;
	private final Queue<Frame>          frames = new ConcurrentLinkedQueue<>();
	private long                        frameCount;
	private long                        startTime;
	
	public SimplePlayer(ISequentialVideoTrack track) {
		this.track = track;
	}

	private static void sleep(long ms) {
		try {Thread.sleep(ms);} catch(InterruptedException e) {}
	}

	@Override
	public void run() {
		if(SwingUtilities.isEventDispatchThread()) {
			java.awt.Frame frame = new java.awt.Frame(track.getURL().toString());
			frame.add(this);
			frame.setSize(track.getWidth(), track.getHeight());
			frame.setVisible(true);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					System.exit(0);
				}
			});
			new Thread(this).start();;
		} else {
			for(;;) {
				while(frames.size() > 5)
					sleep((long) (1000 / track.getFrameRate()));
				frames.add(track.getNextFrame());
				repaint();
			}
		}
	}

	private Frame getFrame(long frameNo) {
		while(frameNo > frameCount) {
			if(frames.isEmpty()) {
				frameCount = frameNo;
				return null;
			}
			frames.remove();
			frameCount++;
		}
		return frames.peek();
	}
	
	@Override
	public void update(Graphics g) {
		if(startTime == 0)
			startTime = System.currentTimeMillis();
		long elapsed = System.currentTimeMillis() - startTime;
		Frame frame  = getFrame((long)(elapsed * track.getFrameRate() / SEC2MS));
		if(frame != null)
			g.drawImage(frame.toBufferedImage(), 0, 0, getWidth(), getHeight(), 0, frame.dimJ, frame.dimI, 0, this);
		repaint((long) (SEC2MS / (track.getFrameRate() * 2)));
	}

	public static void main(String[] args) throws IOException {
		if(args.length != 1) {
			System.out.println("Usage: " + SimplePlayer.class.getName() + " <video url or file>");
			System.exit(1);
		}

		ISequentialVideoTrack track = null;
		try {
			track = VideoTrackFactory.createSequentialTrack(new URL(args[0]));
		} catch(MalformedURLException e) {
			track = VideoTrackFactory.createSequentialTrack(new File(args[0]).toURI().toURL());
		}

		SwingUtilities.invokeLater(new SimplePlayer(track));
	}
}
