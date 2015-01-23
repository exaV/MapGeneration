package ch.fhnw.ether.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.gl.FloatUniformBuffer;
import ch.fhnw.ether.render.variable.builtin.LightUniformBlock;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public final class Lights {
	private static final GenericLight DEFAULT_LIGHT = new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);

	private final List<GenericLight> lights = new ArrayList<>(Collections.singletonList(DEFAULT_LIGHT));
	private FloatUniformBuffer uniforms = new FloatUniformBuffer(LightUniformBlock.BLOCK_SIZE);

	public Lights() {
	}

	public void dispose(GL3 gl) {
		uniforms = null;
	}

	public List<GenericLight> getLights() {
		return lights;
	}

	public IAttributeProvider getAttributeProvider() {
		return new IAttributeProvider() {
			@Override
			public void getAttributes(IAttributes attributes) {
				attributes.provide(LightUniformBlock.ATTRIBUTE, () -> uniforms.getBindingPoint());
			}
		};
	}

	public synchronized void addLight(ILight light) {
		if (!(light instanceof GenericLight)) {
			throw new IllegalArgumentException("can only handle GenericLight");
		}
		if (lights.contains(light)) {
			throw new IllegalArgumentException("light already in renderer: " + light);
		}
		if (lights.size() == LightUniformBlock.MAX_LIGHTS) {
			throw new IllegalStateException("too many lights in renderer: " + LightUniformBlock.MAX_LIGHTS);
		}
		if (lights.get(0) == DEFAULT_LIGHT)
			lights.remove(0);
		lights.add((GenericLight) light);
	}

	public synchronized void removeLight(ILight light) {
		synchronized (lights) {
			if (!lights.contains(light)) {
				throw new IllegalArgumentException("light not in renderer: " + light);
			}
			lights.remove(light);
			if (lights.isEmpty())
				lights.add(DEFAULT_LIGHT);
		}
	}

	public synchronized void update(GL3 gl, CameraMatrices matrices) {
		LightUniformBlock.loadUniforms(gl, uniforms, lights, matrices);
		uniforms.bind(gl);
	}
}
