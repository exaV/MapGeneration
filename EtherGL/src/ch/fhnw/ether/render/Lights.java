package ch.fhnw.ether.render;

import java.nio.FloatBuffer;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.gl.UniformBuffer;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.GenericLight.LightSource;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public final class Lights {
	public static final String LIGHT_UNIFORM_BLOCK = "builtin.light.light_uniform_block";

	private static final GenericLight DEFAULT_LIGHT = new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);

	private GenericLight light = DEFAULT_LIGHT;

	private UniformBuffer uniformBuffer = new UniformBuffer(0);

	public Lights(AbstractRenderer renderer) {
		renderer.addAttributeProvider(new IAttributeProvider() {
			@Override
			public void getAttributeSuppliers(ISuppliers suppliers) {
				suppliers.provide(LIGHT_UNIFORM_BLOCK, () -> 0);
			}
		});
	}

	public void addLight(ILight light) {
		if (light instanceof GenericLight) {
			this.light = (GenericLight) light;
		} else {
			throw new IllegalArgumentException("can only handle GenericLight");
		}
	}

	public void removeLight(ILight light) {
		light = DEFAULT_LIGHT;
	}

	public void update(GL3 gl, CameraMatrices cameraMatrices) {
		LightSource source = light.getLightSource();

		FloatBuffer buffer = FloatBuffer.allocate(22);
		buffer.put(cameraMatrices.getViewMatrix().transform(source.getPosition()).toArray());
		buffer.put(source.getAmbient().toArray());
		buffer.put(0);
		buffer.put(source.getColor().toArray());
		buffer.put(0);
		buffer.put(cameraMatrices.getNormalMatrix().transform(source.getSpotDirection()).toArray());
		buffer.put(0);
		buffer.put(source.getSpotCosCutoff());
		buffer.put(source.getSpotExponent());
		buffer.put(source.getConstantAttenuation());
		buffer.put(source.getLinearAttenuation());
		buffer.put(source.getQuadraticAttenuation());
		buffer.put(source.getType().ordinal());

		uniformBuffer.load(gl, buffer);
		uniformBuffer.bind(gl);
	}
}
