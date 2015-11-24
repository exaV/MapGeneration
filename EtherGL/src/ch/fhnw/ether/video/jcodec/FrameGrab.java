package ch.fhnw.ether.video.jcodec;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcodec.api.JCodecException;
import org.jcodec.api.MediaInfo;
import org.jcodec.api.UnsupportedFormatException;
import org.jcodec.api.specific.AVCMP4Adaptor;
import org.jcodec.api.specific.ContainerAdaptor;
import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.codecs.mpeg12.MPEGDecoder;
import org.jcodec.codecs.prores.ProresDecoder;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.JCodecUtil;
import org.jcodec.common.JCodecUtil.Format;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.SeekableDemuxerTrack;
import org.jcodec.common.VideoDecoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Packet;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.boxes.SampleEntry;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform8Bit;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * Extracts frames from a movie into uncompressed images suitable for
 * processing.
 * 
 * Supports going to random points inside of a movie ( seeking ) by frame number
 * of by second.
 * 
 * NOTE: Supports only AVC ( H.264 ) in MP4 ( ISO BMF, QuickTime ) at this
 * point.
 * 
 * EtherGL: some additions for our needs.
 * 
 * @author The JCodec project
 * 
 */
public class FrameGrab {
	private final DemuxerTrack          videoTrack;
	private       ContainerAdaptor      decoder;
	private final ThreadLocal<byte[][]> buffers = new ThreadLocal<>();
	private long                        seekPos = -1;

	public FrameGrab(SeekableByteChannel in) throws IOException, JCodecException {
		ByteBuffer header = ByteBuffer.allocate(65536);
		in.read(header);
		header.flip();
		Format detectFormat = JCodecUtil.detectFormat(header);

		switch (detectFormat) {
		case MOV:
			MP4Demuxer d1 = new MP4Demuxer(in);
			videoTrack = d1.getVideoTrack();
			break;
		case MPEG_PS:
			throw new UnsupportedFormatException("MPEG PS is temporarily unsupported.");
		case MPEG_TS:
			throw new UnsupportedFormatException("MPEG TS is temporarily unsupported.");
		default:
			throw new UnsupportedFormatException("Container format is not supported by JCodec");
		}
		decodeLeadingFrames();
	}

	public FrameGrab(SeekableDemuxerTrack videoTrack, ContainerAdaptor decoder) {
		this.videoTrack = videoTrack;
		this.decoder = decoder;
	}

	SeekableDemuxerTrack sdt() throws JCodecException {
		if (!(videoTrack instanceof SeekableDemuxerTrack))
			throw new JCodecException("Not a seekable track");

		return (SeekableDemuxerTrack) videoTrack;
	}

	/**
	 * Position frame grabber to a specific second in a movie. As a result the
	 * next decoded frame will be precisely at the requested second.
	 * 
	 * WARNING: potentially very slow. Use only when you absolutely need precise
	 * seek. Tries to seek to exactly the requested second and for this it might
	 * have to decode a sequence of frames from the closes key frame. Depending
	 * on GOP structure this may be as many as 500 frames.
	 * 
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public FrameGrab seekToSecondPrecise(double second) throws IOException, JCodecException {
		sdt().seek(second);
		decodeLeadingFrames();
		return this;
	}

	/**
	 * Position frame grabber to a specific frame in a movie. As a result the
	 * next decoded frame will be precisely the requested frame number.
	 * 
	 * WARNING: potentially very slow. Use only when you absolutely need precise
	 * seek. Tries to seek to exactly the requested frame and for this it might
	 * have to decode a sequence of frames from the closes key frame. Depending
	 * on GOP structure this may be as many as 500 frames.
	 * 
	 * @param frameNumber
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public FrameGrab seekToFramePrecise(int frameNumber) throws IOException, JCodecException {
		sdt().gotoFrame(frameNumber);
		decodeLeadingFrames();
		return this;
	}

	/**
	 * Position frame grabber to a specific second in a movie.
	 * 
	 * Performs a sloppy seek, meaning that it may actually not seek to exact
	 * second requested, instead it will seek to the closest key frame
	 * 
	 * NOTE: fast, as it just seeks to the closest previous key frame and
	 * doesn't try to decode frames in the middle
	 * 
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public FrameGrab seekToSecondSloppy(double second) throws IOException, JCodecException {
		sdt().seek(second);
		goToPrevKeyframe();
		return this;
	}

	/**
	 * Position frame grabber to a specific frame in a movie
	 * 
	 * Performs a sloppy seek, meaning that it may actually not seek to exact
	 * frame requested, instead it will seek to the closest key frame
	 * 
	 * NOTE: fast, as it just seeks to the closest previous key frame and
	 * doesn't try to decode frames in the middle
	 * 
	 * @param frameNumber
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public FrameGrab seekToFrameSloppy(int frameNumber) throws IOException, JCodecException {
		sdt().gotoFrame(frameNumber);
		goToPrevKeyframe();
		return this;
	}

	private void goToPrevKeyframe() throws IOException, JCodecException {
		sdt().gotoFrame(detectKeyFrame((int) sdt().getCurFrame()));
	}

	private void decodeLeadingFrames() throws IOException, JCodecException {
		SeekableDemuxerTrack sdt = sdt();

		int curFrame = (int) sdt.getCurFrame();
		int keyFrame = detectKeyFrame(curFrame);
		sdt.gotoFrame(keyFrame);

		Packet frame = sdt.nextFrame();
		decoder = detectDecoder(sdt, frame);

		while (frame.getFrameNo() < curFrame) {
			decoder.decodeFrame8Bit(frame, getBuffer());
			frame = sdt.nextFrame();
		}
		sdt.gotoFrame(curFrame);
	}

	private byte[][] getBuffer() {
		byte[][] buf = buffers.get();
		if (buf == null) {
			buf = decoder.allocatePicture8Bit();
			buffers.set(buf);
		}
		return buf;
	}

	private int detectKeyFrame(int start) {
		int[] seekFrames = videoTrack.getMeta().getSeekFrames();
		if (seekFrames == null)
			return start;
		int prev = seekFrames[0];
		for (int i = 1; i < seekFrames.length; i++) {
			if (seekFrames[i] > start)
				break;
			prev = seekFrames[i];
		}
		return prev;
	}

	private ContainerAdaptor detectDecoder(SeekableDemuxerTrack videoTrack, Packet frame) throws JCodecException {
		if (videoTrack instanceof AbstractMP4DemuxerTrack) {
			SampleEntry se = ((AbstractMP4DemuxerTrack) videoTrack).getSampleEntries()[((MP4Packet) frame).getEntryNo()];
			VideoDecoder byFourcc = byFourcc(se.getHeader().getFourcc());
			if (byFourcc instanceof H264Decoder)
				return new AVCMP4Adaptor(((AbstractMP4DemuxerTrack) videoTrack).getSampleEntries());
		}

		throw new UnsupportedFormatException("Codec is not supported");
	}

	private VideoDecoder byFourcc(String fourcc) {
		if (fourcc.equals("avc1")) {
			return new H264Decoder();
		} else if (fourcc.equals("m1v1") || fourcc.equals("m2v1")) {
			return new MPEGDecoder();
		} else if (fourcc.equals("apco") || fourcc.equals("apcs") || fourcc.equals("apcn") || fourcc.equals("apch")
				|| fourcc.equals("ap4h")) {
			return new ProresDecoder();
		}
		return null;
	}

	/**
	 * Get frame at current position in JCodec native image
	 * 
	 * @return
	 * @throws IOException
	 */
	public Picture8Bit getNativeFrame() throws IOException {
		Packet frame = videoTrack.nextFrame();
		if (frame == null)
			return null;

		return decoder.decodeFrame8Bit(frame, getBuffer());
	}

	/**
	 * Get frame at a specified second as JCodec image
	 * 
	 * @param file
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	@SuppressWarnings("resource")
	public static Picture8Bit getNativeFrame(File file, double second) throws IOException, JCodecException {
		FileChannelWrapper ch = null;
		try {
			ch = NIOUtils.readableFileChannel(file);
			return new FrameGrab(ch).seekToSecondPrecise(second).getNativeFrame();
		} finally {
			NIOUtils.closeQuietly(ch);
		}
	}

	/**
	 * Get frame at a specified second as JCodec image
	 * 
	 * @param file
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public static Picture8Bit getNativeFrame(SeekableByteChannel file, double second) throws JCodecException,
	IOException {
		return new FrameGrab(file).seekToSecondPrecise(second).getNativeFrame();
	}

	/**
	 * Get frame at a specified frame number as JCodec image
	 * 
	 * @param file
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	@SuppressWarnings("resource")
	public static Picture8Bit getNativeFrame(File file, int frameNumber) throws IOException, JCodecException {
		FileChannelWrapper ch = null;
		try {
			ch = NIOUtils.readableFileChannel(file);
			return new FrameGrab(ch).seekToFramePrecise(frameNumber).getNativeFrame();
		} finally {
			NIOUtils.closeQuietly(ch);
		}
	}

	/**
	 * Get frame at a specified frame number as JCodec image
	 * 
	 * @param file
	 * @param second
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public static Picture8Bit getNativeFrame(SeekableByteChannel file, int frameNumber) throws JCodecException,
	IOException {
		return new FrameGrab(file).seekToFramePrecise(frameNumber).getNativeFrame();
	}

	/**
	 * Get a specified frame by number from an already open demuxer track
	 * 
	 * @param vt
	 * @param decoder
	 * @param frameNumber
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public static Picture8Bit getNativeFrame(SeekableDemuxerTrack vt, ContainerAdaptor decoder, int frameNumber)
			throws IOException, JCodecException {
		return new FrameGrab(vt, decoder).seekToFramePrecise(frameNumber).getNativeFrame();
	}

	/**
	 * Get a specified frame by second from an already open demuxer track
	 * 
	 * @param vt
	 * @param decoder
	 * @param frameNumber
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public static Picture8Bit getNativeFrame(SeekableDemuxerTrack vt, ContainerAdaptor decoder, double second)
			throws IOException, JCodecException {
		return new FrameGrab(vt, decoder).seekToSecondPrecise(second).getNativeFrame();
	}

	/**
	 * Get a specified frame by number from an already open demuxer track (
	 * sloppy mode, i.e. nearest keyframe )
	 * 
	 * @param vt
	 * @param decoder
	 * @param frameNumber
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public static Picture8Bit getNativeFrameSloppy(SeekableDemuxerTrack vt, ContainerAdaptor decoder, int frameNumber)
			throws IOException, JCodecException {
		return new FrameGrab(vt, decoder).seekToFrameSloppy(frameNumber).getNativeFrame();
	}

	/**
	 * Get a specified frame by second from an already open demuxer track (
	 * sloppy mode, i.e. nearest keyframe )
	 * 
	 * @param vt
	 * @param decoder
	 * @param frameNumber
	 * @return
	 * @throws IOException
	 * @throws JCodecException
	 */
	public static Picture8Bit getNativeFrameSloppy(SeekableDemuxerTrack vt, ContainerAdaptor decoder, double second)
			throws IOException, JCodecException {
		return new FrameGrab(vt, decoder).seekToSecondSloppy(second).getNativeFrame();
	}

	/**
	 * Gets info about the media
	 * 
	 * @return
	 */
	public MediaInfo getMediaInfo() {
		return decoder.getMediaInfo();
	}

	public boolean skipFrame() throws JCodecException {
		if(seekPos < 0)
			seekPos = sdt().getCurFrame();
		seekPos++;
		return seekPos < sdt().getMeta().getTotalFrames();
	}

	public void grabAndSet(Frame frame) throws IOException {
		try {
			Packet pkt;
			SeekableDemuxerTrack sdt = sdt();
			if(seekPos > 0) {
				sdt.gotoFrame(seekPos);

				int curFrame = (int) sdt.getCurFrame();
				int keyFrame = detectKeyFrame(curFrame);
				sdt.gotoFrame(keyFrame);

				pkt = sdt.nextFrame();

				while (pkt.getFrameNo() <= curFrame) {
					decoder.decodeFrame8Bit(pkt, getBuffer());
					pkt = sdt.nextFrame();
				}
				sdt.gotoFrame(curFrame);
				seekPos = -1;
			} else
				pkt = sdt.nextFrame();
			Picture8Bit src = decoder.decodeFrame8Bit(pkt, getBuffer());
			if (src.getColor() != ColorSpace.RGB) {
				Picture8Bit   rgb       = Picture8Bit.create(src.getWidth(), src.getHeight(), ColorSpace.RGB, src.getCrop());
				Transform8Bit transform = ColorUtil.getTransform8Bit(src.getColor(), rgb.getColor());
				transform.transform(src, rgb);
				src = rgb;
			}
			if(!(frame instanceof RGB8Frame))
				throw new IOException("Unsupported frame type:" + frame.getClass().getName());

			final ByteBuffer pixels  = frame.pixels;
			final byte[]     srcData = src.getPlaneData(0);
			final int        line    = frame.dimI * frame.pixelSize;

			pixels.clear();
			if(frame.pixelSize == 4) {
				for(int j = frame.dimJ; --j >= 0;) {
					int idx = j * line;
					for (int i = frame.dimI; --i >= 0;) {
						pixels.put((byte) (srcData[idx+2] + 128));
						pixels.put((byte) (srcData[idx+1] + 128));
						pixels.put((byte) (srcData[idx+0] + 128));
						pixels.put((byte) 0xFF);
						idx += 3;
					}
				}
			} else {
				for(int j = frame.dimJ; --j >= 0;) {
					int idx = j * line;
					for (int i = frame.dimI; --i >= 0;) {
						pixels.put((byte) (srcData[idx+2] + 128));
						pixels.put((byte) (srcData[idx+1] + 128));
						pixels.put((byte) (srcData[idx+0] + 128));
						idx += 3;
					}
				}
			}
		} catch(Throwable t) {
			throw new IOException("Could not create frame", t);
		}
	}
}