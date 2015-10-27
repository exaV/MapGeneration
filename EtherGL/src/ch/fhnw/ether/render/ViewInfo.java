package ch.fhnw.ether.render;

import com.jogamp.opengl.GL3;

import ch.fhnw.ether.render.gl.FloatUniformBuffer;
import ch.fhnw.ether.render.variable.builtin.ViewUniformBlock;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.ViewCameraState;
import ch.fhnw.util.Viewport;
import ch.fhnw.ether.view.IView.ViewType;

public class ViewInfo {
	private final FloatUniformBuffer uniforms = new FloatUniformBuffer(ViewUniformBlock.BLOCK_SIZE, 3);
	private ViewCameraState matrices;
	private Viewport viewport;
	private ViewType type;

	public void update(GL3 gl, ViewCameraState matrices, Viewport viewport, ViewType type) {
		ViewUniformBlock.loadUniforms(gl, uniforms, matrices, viewport);
		this.matrices = matrices;
		this.viewport = viewport;
		this.type = type;
	}
	
	public ViewCameraState getMatrices() {
		return matrices;
	}
	
	public Viewport getViewport() {
		return viewport;
	}
	
	public ViewType getType() {
		return type;
	}

	public void setCameraSpace(GL3 gl) {
		uniforms.bind(gl, 0);
	}

	public void setOrthoDeviceSpace(GL3 gl) {
		uniforms.bind(gl, 1);
	}

	public void setOrthoScreenSpace(GL3 gl) {
		uniforms.bind(gl, 2);
	}

	public IAttributeProvider getAttributeProvider() {
		return new IAttributeProvider() {
			@Override
			public void getAttributes(IAttributes attributes) {
				attributes.provide(ViewUniformBlock.ATTRIBUTE, () -> uniforms.getBindingPoint());
			}
		};
	}
}
