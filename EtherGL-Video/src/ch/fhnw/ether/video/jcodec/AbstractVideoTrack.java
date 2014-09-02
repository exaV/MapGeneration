package ch.fhnw.ether.video.jcodec;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jcodec.api.JCodecException;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform;

import ch.fhnw.ether.video.IVideoTrack;

abstract class AbstractVideoTrack implements IVideoTrack {

	private URL url;
	private SeekableByteChannel channel;
	protected FrameGrab grab;
	
	public AbstractVideoTrack(URL url) throws IOException, URISyntaxException, JCodecException {
		this.url = url;
		this.channel = NIOUtils.readableFileChannel(new File(url.toURI()));
		this.grab = new FrameGrab(channel);
	}

	@Override
	public void dispose() {
		try {
			channel.close();
		} catch (IOException e) {
		}
		this.url = null;
		this.channel = null;
		this.grab = null;
	}
	
	@Override
	public URL getURL() {
		return url;
	}

	@Override
	public double getDuration() {
		return grab.getVideoTrack().getMeta().getTotalDuration();
	}

	@Override
	public double getFrameRate() {
		return getFrameCount() / getDuration();
	}

	@Override
	public long getFrameCount() {
		return grab.getVideoTrack().getMeta().getTotalFrames();
	}

	@Override
	public int getWidth() {
		return grab.getMediaInfo().getDim().getWidth();
	}

	@Override
	public int getHeight() {
		return grab.getMediaInfo().getDim().getHeight();
	}

	protected static BufferedImage toBufferedImageNoCrop(Picture src) {
		if (src.getColor() != ColorSpace.RGB) {
			Transform transform = ColorUtil.getTransform(src.getColor(), ColorSpace.RGB);
			Picture rgb = Picture.create(src.getWidth(), src.getHeight(), ColorSpace.RGB, src.getCrop());
			transform.transform(src, rgb);
			src = rgb;
		}

		BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_3BYTE_BGR);

		byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
		int[] srcData = src.getPlaneData(0);
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) srcData[i];
		}

		return dst;
	}
	
	@Override
	public String toString() {
		return getURL() + " (d=" + getDuration() + " fr=" + getFrameRate() + " fc=" + getFrameCount() + " w=" + getWidth() + " h=" + getHeight() + ")"; 
	}
}
