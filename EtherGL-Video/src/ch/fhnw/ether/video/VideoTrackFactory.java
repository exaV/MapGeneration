/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
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

package ch.fhnw.ether.video;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.ImageTrack;
import ch.fhnw.ether.video.avfoundation.AVAsset;
import ch.fhnw.ether.video.jcodec.RandomAccessVideoTrack;
import ch.fhnw.ether.video.jcodec.SequentialVideoTrack;

public final class VideoTrackFactory {
	private static final boolean USE_AV_FOUNDATION = true;

	public static IRandomAccessVideoTrack createRandomAccessTrack(URL url) throws IOException {
		if (isImage(url))
			return new ImageTrack(url);
		else if (USE_AV_FOUNDATION && AVAsset.isReady())
			return new AVAsset(url);
		try {
			return new RandomAccessVideoTrack(url);
		} catch (Throwable t) {
			throw new IOException("cannot create video track from " + url, t);
		}
	}

	public static ISequentialVideoTrack createSequentialTrack(URL url) throws IOException {
		if (isImage(url))
			return new ImageTrack(url);
		else if (USE_AV_FOUNDATION && AVAsset.isReady())
			return new AVAsset(url);
		try {
			return new SequentialVideoTrack(url);
		} catch (Throwable t) {
			throw new IOException("cannot create video track from " + url, t);
		}
	}

	// TODO: move tests elsewhere
	public static void main(String[] args) {
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

			long numFrames = (long) (time * track.getFrameRate());
			System.out.println("decoding " + numFrames + " frames");

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

	private static boolean isImage(URL url) {
		String path = url.getPath();
		return ImageIO.getImageReadersBySuffix(path.substring(path.lastIndexOf('.') + 1, path.length())).hasNext();
	}

}
