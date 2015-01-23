package ch.fhnw.ether.video;

import java.awt.Dimension;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.fhnw.ether.image.RGB8Frame;
import ch.fhnw.ether.media.AbstractFrame;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.media.Stateless;

import com.github.sarxos.webcam.Webcam;

public class CameraSource extends AbstractVideoSource<Stateless<IVideoRenderTarget>> {
	private static final Method getFPS = getMethod("getFPS");

	private final Webcam                cam;
	private AtomicBoolean               disposed = new AtomicBoolean(false);
	private final CameraInfo            info;

	private CameraSource(CameraInfo info) {
		this.info     = info;
		this.cam      = info.getNativeCamera();
		this.cam.open(true);
		Runtime.getRuntime().addShutdownHook(new Thread(()->{dispose();}));
		Dimension max = cam.getViewSize();
		for(Dimension dim : this.cam.getViewSizes())
			if(dim.width > max.width && dim.height > max.height)
				max = dim;
		setSize(max.width, max.height);
	}

	public void dispose() {
		if(!(disposed.getAndSet(true))) {
			cam.close();
		}
	}

	@Override
	protected void run(Stateless<IVideoRenderTarget> state) throws RenderCommandException {
		if(!(cam.isOpen())) return;
		Dimension size  = cam.getViewSize();
		RGB8Frame frame = new RGB8Frame(size.width, size.height);
		final ByteBuffer src = cam.getImageBytes();
		src.clear();
		final ByteBuffer dst = frame.pixels;
		for(int j = frame.dimJ; --j >= 0;) {
			dst.position(j * frame.dimI * 3);
			for(int i = frame.dimI; --i >= 0;) {
				dst.put(src.get());
				dst.put(src.get());
				dst.put(src.get());
			}
		}
		state.getTarget().setFrame(new VideoFrame(AbstractFrame.ASAP, frame));
	}

	public void setSize(int width, int height) {
		cam.close();
		cam.setViewSize(new Dimension(width, height));
		cam.open(true);
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
	public int getWidth() {
		return cam.getViewSize().width;
	}
	
	@Override
	public int getHeight() {
		return cam.getViewSize().height;
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
