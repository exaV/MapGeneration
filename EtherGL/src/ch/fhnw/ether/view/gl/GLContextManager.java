package ch.fhnw.ether.view.gl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;

import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IView.Config;

import com.jogamp.newt.opengl.GLWindow;

public class GLContextManager {
	public interface IGLContext {
		GL3 getGL();
	}

	private static final class ExistingContext implements IGLContext {
		@Override
		public GL3 getGL() {
			return GLContext.getCurrentGL().getGL3();
		}
	}

	private static final class TemporaryContext implements IGLContext {
		private GLWindow window;

		TemporaryContext() {
			window = GLWindow.create(theSharedDrawable.getChosenGLCapabilities());
			window.setSharedAutoDrawable(theSharedDrawable);
			window.setSize(16, 16);
			window.setVisible(true);
			window.setVisible(false, false);
		}

		void makeCurrent() {
			window.getContext().makeCurrent();
		}

		void release() {
			window.getContext().release();
		}

		@Override
		public GL3 getGL() {
			return GLContext.getCurrentGL().getGL3();
		}
	}

	private static class ContextPool {
		private static final int MAX_CONTEXTS = 10;
		private final AtomicInteger numContexts = new AtomicInteger();
		private final BlockingQueue<TemporaryContext> contexts = new LinkedBlockingQueue<>();
		
		TemporaryContext acquireContext(boolean wait) {
			TemporaryContext context = null;
			context = contexts.poll();
			if (context == null) {
				if (numContexts.incrementAndGet() < MAX_CONTEXTS) {
					context = new TemporaryContext();
				} else if (wait) {
					try {
						context = contexts.take();
					} catch (InterruptedException e) {
						// XXX what to do in this case?
						e.printStackTrace();
					}
				}
			}
			if (context != null)
				context.makeCurrent();
			return context;
		}
		
		void releaseContext(TemporaryContext context) {
			context.release();
			contexts.add(context);			
		}
		
	}

	private static final IGLContext VOID_CONTEXT = new ExistingContext();

	private static ContextPool contexts = new ContextPool();
	
	private static GLAutoDrawable theSharedDrawable;

	public static IGLContext acquireContext() {
		return acquireContext(true);
	}

	public static IGLContext acquireContext(boolean wait) {
		if (GLContext.getCurrent() != null)
			return VOID_CONTEXT;

		return contexts.acquireContext(wait);
	}

	public static void releaseContext(IGLContext context) {
		if (context instanceof TemporaryContext)
			contexts.releaseContext((TemporaryContext)context);
	}

	public synchronized static GLAutoDrawable getSharedDrawable(GLCapabilities capabilities) {
		if (theSharedDrawable == null) {
			if (capabilities == null)
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
		if (config.getFSAASamples() > 0) {
			caps.setSampleBuffers(true);
			caps.setNumSamples(config.getFSAASamples());
		} else {
			caps.setSampleBuffers(false);
			caps.setNumSamples(1);
		}
		return caps;
	}
}
