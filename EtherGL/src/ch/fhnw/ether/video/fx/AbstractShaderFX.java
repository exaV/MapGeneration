package ch.fhnw.ether.video.fx;

import javax.media.opengl.GL;

import ch.fhnw.ether.media.FXParameter;

public abstract class AbstractShaderFX extends AbstractVideoFX {
	protected final GL            gl = null;
	
	protected AbstractShaderFX(FXParameter ... parameters) {
		super(parameters);
	}
}
