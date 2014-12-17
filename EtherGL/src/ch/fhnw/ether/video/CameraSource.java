package ch.fhnw.ether.video;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.media.FrameReq;

import com.github.sarxos.webcam.Webcam;

public class CameraSource extends Thread implements ISequentialFrameSource, IScalingFrameSource {
	private static final Method getFPS = getMethod("getFPS");
	
	private final Webcam                cam;
	private AtomicBoolean               disposed = new AtomicBoolean(false);
	private Set<Class<? extends Frame>> preferredTypes;
	private final CameraInfo            info;

	private CameraSource(CameraInfo info) {
		this.info     = info;
		this.cam      = info.getNativeCamera();
		this.cam.open(true);
		this.preferredTypes = new HashSet<>(Arrays.asList(getFrameTypes()));
		Runtime.getRuntime().addShutdownHook(this);
		Dimension max = cam.getViewSize();
		for(Dimension dim : this.cam.getViewSizes())
			if(dim.width > max.width && dim.height > max.height)
				max = dim;
		setSize(max.width, max.height);
	}

	@Override
	public int getWidth() {
		return cam.getViewSize().width;
	}

	@Override
	public int getHeight() {
		return cam.getViewSize().height;
	}

	@Override
	public void dispose() {
		if(!(disposed.getAndSet(true))) {
			cam.close();
		}
	}

	@Override
	public URL getURL() {
		return null;
	}

	@Override
	public double getDuration() {
		return DURATION_UNKNOWN;
	}

	@Override
	public double getFrameRate() {
		try {
			return Math.max(10.0, ((Double)getFPS.invoke(cam)).doubleValue());
		} catch(Throwable t) {
			return FRAMERATE_UNKNOWN;
		}
	}

	@Override
	public long getFrameCount() {
		return FRAMECOUNT_UNKNOWN;
	}

	@Override
	public FrameReq getFrames(FrameReq req) {
		req.processFrames(RGB8Frame.class, getWidth(), getHeight(), (Frame frame, int frameIdx)->{
			final ByteBuffer src = cam.getImageBytes();
			src.clear();
			final ByteBuffer dst = frame.pixels;
			if(frame.pixelSize == 4) {
				for(int j = frame.dimJ; --j >= 0;){
					dst.position(j * frame.dimI * 4);
					for(int i = frame.dimI; --i >= 0;) {
						dst.put(src.get());
						dst.put(src.get());
						dst.put(src.get());
						dst.put(Frame.B255);
					}
				}
			} else {
				for(int j = frame.dimJ; --j >= 0;) {
					dst.position(j * frame.dimI * 3);
					for(int i = frame.dimI; --i >= 0;) {
						dst.put(src.get());
						dst.put(src.get());
						dst.put(src.get());
					}
				}
			}
		});
		return req;
	}

	@Override
	public void setSize(int width, int height) {
		cam.close();
		cam.setViewSize(new Dimension(width, height));
		cam.open(true);
	}

	@Override
	public Class<? extends Frame>[] getFrameTypes() {
		return FTS_RGB8_RGBA8;
	}

	@Override
	public void rewind() {
	}

	@Override
	public void setPreferredFrameTypes(Set<Class<? extends Frame>> frameTypes) {
		IVideoFrameSource.updatePreferredFrameTypes(preferredTypes, frameTypes);
	}

	//--- utilities
	
	private static Method getMethod(String name) {
		try {
			Method result = Webcam.class.getDeclaredMethod(name);
			result.setAccessible(true);
			return result;
		} catch(Throwable t) {
			return null;
		}
	}

	@Override
	public void run() {
		dispose();
	}

	@Override
	public String toString() {
		return info.toString();
	}
	
	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	public static CameraSource create(CameraInfo cameraInfo) {
		return new CameraSource(cameraInfo);
	}

}
