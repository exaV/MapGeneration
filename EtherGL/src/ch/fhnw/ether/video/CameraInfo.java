package ch.fhnw.ether.video;

import java.awt.Dimension;
import java.util.List;

import ch.fhnw.util.math.Vec2;

import com.github.sarxos.webcam.Webcam;

public class CameraInfo {
	private final Webcam cam;
	
	private CameraInfo(Webcam cam) {
		this.cam = cam;
	}
	
	public Vec2[] getSupportedSizes() {
		Dimension[] sizes = cam.getViewSizes();
		Vec2[] result = new Vec2[sizes.length];
		for(int i = 0; i < sizes.length; i++)
			result[i] = new Vec2(sizes[i].width, sizes[i].height);
		return result;
	}
	
	public static CameraInfo[] getInfos() {
		List<Webcam> cams = Webcam.getWebcams();
		CameraInfo[] result = new CameraInfo[cams.size()];
		int idx = 0;
		for(Webcam cam : cams)
			result[idx++] = new CameraInfo(cam);
		return result;
	}

	public Webcam getNativeCamera() {
		return cam;
	}
	
	@Override
	public String toString() {
		return cam.getName();
	}
}
