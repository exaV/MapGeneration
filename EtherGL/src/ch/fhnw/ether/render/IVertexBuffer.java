package ch.fhnw.ether.render;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.IMesh;

public interface IVertexBuffer {
	
	public int getNumVertices();

	void load(GL3 gl, IShader shader, IMesh mesh);

	public void bind(GL3 gl);

	public void unbind(GL3 gl);

	public void enableAttribute(GL3 gl, int bufferIndex, int shaderIndex);

	public void disableAttribute(GL3 gl, int bufferIndex, int shaderIndex);

}
