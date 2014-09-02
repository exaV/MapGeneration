package ch.fhnw.ether.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.video.avfoundation.AVAsset;
import ch.fhnw.ether.video.jcodec.RandomAccessVideoTrack;
import ch.fhnw.ether.video.jcodec.SequentialVideoTrack;

public final class VideoTrackFactory {
	private static final boolean USE_AV_FOUNDATION = true;

	public static IRandomAccessVideoTrack createRandomAccessTrack(URL url) {
		if (USE_AV_FOUNDATION && AVAsset.isReady())
			return new AVAsset(url);

		try {
			return new RandomAccessVideoTrack(url);
		} catch (Exception e) {
			throw new IllegalArgumentException("cannot create video track from " + url);
		}
	}

	public static ISequentialVideoTrack createSequentialTrack(URL url) {
		if (USE_AV_FOUNDATION && AVAsset.isReady())
			return new AVAsset(url);

		try {
			return new SequentialVideoTrack(url);
		} catch (Exception e) {
			throw new IllegalArgumentException("cannot create video track from " + url);
		}
	}

	public static void main(String[] args) {
		// file:///Users/radar/Desktop/movies/hot_chip-flutes_(sacha_remix).mp4
		SwingUtilities.invokeLater(() -> {
			try {
				test(new URL(args[0]), 32.0);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		});
	}

	public static void test(URL url, double time) {
		testRandomAccess(url, time);
		testSequential(url, time);
	}

	public static void testRandomAccess(URL url, double time) {
		try {
			IRandomAccessVideoTrack track = createRandomAccessTrack(url);
			System.out.println("random access track: " + track);

			Frame frame = track.getFrame(time);

			BufferedImage image = frame.toBufferedImage();
			System.out.println("decoded image: " + image.getWidth() + " " + image.getHeight());

			try {
				File outputfile = new File("/tmp/saved_random_access.png");
				ImageIO.write(image, "png", outputfile);
			} catch (IOException e) {
			}
			track.dispose();
			System.out.println("done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testSequential(URL url, double time) {
		try {
			ISequentialVideoTrack track = createSequentialTrack(url);
			System.out.println("sequential track: " + track);
			
			long numFrames = (long)(time * track.getFrameRate());
			System.out.println("decoding "+ numFrames + " frames");

			long millis = System.currentTimeMillis();
			Frame frame = null;
			for (int i = 0; i < time * track.getFrameRate(); ++i) {
				frame = track.getNextFrame();
			}
			double fps = numFrames / ((System.currentTimeMillis() - millis) / 1000.0);
			System.out.println("decoded " + numFrames + " @ " + fps + " fps");

			BufferedImage image = frame.toBufferedImage();
			System.out.println("decoded image: " + image.getWidth() + " " + image.getHeight() + " @ " + fps + " fps");

			try {
				File outputfile = new File("/tmp/saved_sequential.png");
				ImageIO.write(image, "png", outputfile);
			} catch (IOException e) {
			}
			track.dispose();
			System.out.println("done.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
