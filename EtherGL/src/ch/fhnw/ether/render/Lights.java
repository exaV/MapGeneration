package ch.fhnw.ether.render;

import javax.media.opengl.GL3;

import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public final class Lights {
	private static final GenericLight DEFAULT_LIGHT = new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);

	private GenericLight light = DEFAULT_LIGHT;

	public Lights(AbstractRenderer renderer) {
		renderer.addAttributeProvider(new IAttributeProvider() {
			@Override
			public void getAttributeSuppliers(ISuppliers suppliers) {
				suppliers.provide(GenericLight.GENERIC_LIGHT, () -> light.getLightSource());
			}
		});
	}

	public void addLight(ILight light) {
		if (light instanceof GenericLight) {
			this.light = (GenericLight)light;
		} else {
			throw new IllegalArgumentException("can only handle GenericLight");
		}
	}
	
	public void removeLight(ILight light) {
		light = DEFAULT_LIGHT;		
	}

	public void update(GL3 gl, CameraMatrices cameraMatrices) {
	}	
}
