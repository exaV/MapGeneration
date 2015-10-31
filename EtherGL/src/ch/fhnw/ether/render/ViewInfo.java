package ch.fhnw.ether.render;

import java.util.Map;
import java.util.function.Supplier;

import com.jogamp.opengl.GL3;

import ch.fhnw.ether.render.gl.FloatUniformBuffer;
import ch.fhnw.ether.render.variable.builtin.ViewUniformBlock;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.camera.IViewCameraState;

public final class ViewInfo {
	private final FloatUniformBuffer uniforms = new FloatUniformBuffer(ViewUniformBlock.BLOCK_SIZE, 3);
	private IViewCameraState vcs;

	public void update(GL3 gl, IViewCameraState vcs) {
		ViewUniformBlock.loadUniforms(gl, uniforms, vcs);
		this.vcs = vcs;
	}
	
	public IViewCameraState getViewCameraState() {
		return vcs;
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

	public void getAttributes(Map<IAttribute, Supplier<?>> globals) {
		globals.put(ViewUniformBlock.ATTRIBUTE, uniforms::getBindingPoint);
	}
}
