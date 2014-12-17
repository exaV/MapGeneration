package ch.fhnw.ether.video.fx;

import ch.fhnw.ether.render.shader.base.AbstractShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;

public class VideoShader extends AbstractShader {

	protected VideoShader(String name) {
		super(VideoShader.class, name, name, Primitive.TRIANGLES);
	}
}
