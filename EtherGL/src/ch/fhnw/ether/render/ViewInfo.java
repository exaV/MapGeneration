package ch.fhnw.ether.render;

import com.jogamp.opengl.GL3;

import ch.fhnw.ether.render.gl.FloatUniformBuffer;
import ch.fhnw.ether.render.variable.builtin.ViewUniformBlock;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.ViewMatrices;
import ch.fhnw.util.ViewPort;
import ch.fhnw.ether.view.IView.ViewType;

public class ViewInfo {
	private final FloatUniformBuffer uniforms = new FloatUniformBuffer(ViewUniformBlock.BLOCK_SIZE, 3);
	private ViewMatrices matrices;
	private ViewPort viewPort;
	private ViewType viewType;

	public void update(GL3 gl, ViewMatrices matrices, ViewPort viewPort, ViewType viewType) {
		ViewUniformBlock.loadUniforms(gl, uniforms, matrices, viewPort);
		this.matrices = matrices;
		this.viewPort = viewPort;
		this.viewType = viewType;
	}
	
	public ViewMatrices getMatrices() {
		return matrices;
	}
	
	public ViewPort getViewPort() {
		return viewPort;
	}
	
	public ViewType getViewType() {
		return viewType;
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
