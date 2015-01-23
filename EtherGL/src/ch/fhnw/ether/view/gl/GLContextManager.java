package ch.fhnw.ether.view.gl;

import java.util.concurrent.locks.ReentrantLock;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;

import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IView.Config;

public class GLContextManager {
	public enum Context {
		LOCK_AND_MAKE_CURRENT
	}

	private static GLAutoDrawable theSharedDrawable;
	private static ReentrantLock  lock = new ReentrantLock();
	private static GLContext      curr;

	public static GLContext getTemp(Context contextAction) {
		switch(contextAction) {
		case LOCK_AND_MAKE_CURRENT:
			lock.lock();
			curr = GLContext.getCurrent();
			GLContext result = getSharedDrawable(null).getContext();
			result.makeCurrent();
			return result;
		}
		throw new IllegalStateException("Unknown context action:" + contextAction);
	}

	public static void releaseTemp(GLContext tmpCtx) {
		GLContext.getCurrent().release();
		if(curr != null)
			curr.makeCurrent();
		lock.unlock();
	}

	public synchronized static GLAutoDrawable getSharedDrawable(GLCapabilities capabilities) {
		if(theSharedDrawable == null) {
			if(capabilities == null)
				capabilities = getCapabilities(IView.INTERACTIVE_VIEW);
			theSharedDrawable = GLDrawableFactory.getFactory(capabilities.getGLProfile()).createDummyAutoDrawable(null, true, capabilities, null);
		}
		return theSharedDrawable;
	}

	public static GLCapabilities getCapabilities(Config config) {
		// FIXME: make this configurable
		GLProfile profile = GLProfile.get(GLProfile.GL3);
		GLCapabilities caps = new GLCapabilities(profile);
		caps.setAlphaBits(8);
		caps.setStencilBits(16);
		if(config.getFSAASamples() > 0) {
			caps.setSampleBuffers(true);
			caps.setNumSamples(config.getFSAASamples());
		} else {
			caps.setSampleBuffers(false);
			caps.setNumSamples(1);
		}
		return caps;
	}
}
