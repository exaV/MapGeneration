package ch.fhnw.ether.view.gl;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import ch.fhnw.ether.controller.event.IKeyListener;
import ch.fhnw.ether.controller.event.IMouseListener;
import ch.fhnw.ether.controller.event.IWindowListener;
import ch.fhnw.ether.controller.event.KeyEvent;
import ch.fhnw.ether.controller.event.ListenerList;
import ch.fhnw.ether.controller.event.MouseEvent;
import ch.fhnw.ether.controller.event.WindowEvent;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IView.Config;
import ch.fhnw.ether.view.IWindow;
import ch.fhnw.util.math.Vec2;

public final class GLFWWindow implements IWindow {
	private final long window;
	private final GLContext context;

	private final IView view;

	private final ListenerList<IWindowListener> windowListeners = new ListenerList<>();
	private final ListenerList<IKeyListener> keyListeners = new ListenerList<>();
	private final ListenerList<IMouseListener> mouseListeners = new ListenerList<>();

	private final GLFWWindowCloseCallback closeCallback = new GLFWWindowCloseCallback() {
		@Override
		public void invoke(long window) {
			System.out.println("window close");
			WindowEvent event = new WindowEvent(view);
			windowListeners.post((l) -> l.windowClosed(event));
		}
	};

	private final GLFWWindowFocusCallback focusCallback = new GLFWWindowFocusCallback() {
		@Override
		public void invoke(long window, int focused) {
			System.out.println("window focused " + focused);
			WindowEvent event = new WindowEvent(view);
			if (focused > 0)
				windowListeners.post((l) -> l.windowGainedFocus(event));
			else
				windowListeners.post((l) -> l.windowLostFocus(event));
		}
	};

	private Vec2 frameBufferSize = new Vec2(10, 10);

	private final GLFWFramebufferSizeCallback sizeCallback = new GLFWFramebufferSizeCallback() {
		@Override
		public void invoke(long window, int width, int height) {
			System.out.println("window resized " + width + " " + height);
			frameBufferSize = new Vec2(width, height);
			WindowEvent event = new WindowEvent(view);
			windowListeners.post((l) -> l.windowResized(event));
		}
	};

	private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
		@Override
		public void invoke(long window, double xoffset, double yoffset) {
			System.out.println("window scrolled " + xoffset + " " + yoffset);
			WindowEvent event = new WindowEvent(view);
			windowListeners.post((l) -> l.windowScrolled(event));
		}
	};

	private int keyModifiers;

	private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			System.out.println("key " + key + " " + action + " " + mods);
			keyModifiers = mods;
			KeyEvent event = new KeyEvent(view, key, keyModifiers);
			if (action == GLFW.GLFW_PRESS)
				keyListeners.post((l) -> l.keyPressed(event));
			else
				keyListeners.post((l) -> l.keyReleased(event));
		}
	};

	private float mouseX;
	private float mouseY;
	private float mouseLastX;
	private float mouseLastY;
	private int mouseDragging;

	private final GLFWCursorEnterCallback mouseEnterCallback = new GLFWCursorEnterCallback() {
		@Override
		public void invoke(long window, int entered) {
			MouseEvent event = new MouseEvent(view, 0, mouseX, mouseY, mouseLastX, mouseLastY, keyModifiers);
			if (entered > 0)
				mouseListeners.post((l) -> l.mouseEntered(event));
			else
				mouseListeners.post((l) -> l.mouseExited(event));
		}
	};

	private final GLFWCursorPosCallback mousePositionCallback = new GLFWCursorPosCallback() {
		@Override
		public void invoke(long window, double xpos, double ypos) {
			mouseLastX = mouseX;
			mouseLastY = mouseY;
			mouseX = (float) xpos;
			mouseY = (float) ypos;
			MouseEvent event = new MouseEvent(view, 0, mouseX, mouseY, mouseLastX, mouseLastY, keyModifiers);
			if (mouseDragging > 0)
				mouseListeners.post((l) -> l.mouseDragged(event));
			else
				mouseListeners.post((l) -> l.mouseMoved(event));
		}
	};

	private final GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
		@Override
		public void invoke(long window, int button, int action, int mods) {
			keyModifiers = mods;
			MouseEvent event = new MouseEvent(view, button, mouseX, mouseY, mouseLastX, mouseLastY, keyModifiers);
			if (action == GLFW.GLFW_PRESS) {
				mouseDragging++;
				mouseListeners.post((l) -> l.mouseButtonPressed(event));
			} else {
				mouseDragging--;
				mouseListeners.post((l) -> l.mouseButtonReleased(event));
			}
		}
	};

	/**
	 * Creates undecorated frame.
	 *
	 * @param width
	 *            the frame's width
	 * @param height
	 *            the frame's height
	 * @param config
	 *            The configuration.
	 */
	public GLFWWindow(IView view, int width, int height, Config config) {
		this(view, width, height, null, config);
	}

	/**
	 * Creates a decorated or undecorated frame with given dimensions
	 *
	 * @param width
	 *            the frame's width
	 * @param height
	 *            the frame's height
	 * @param title
	 *            the frame's title, nor null for an undecorated frame
	 * @param config
	 *            The configuration.
	 */
	public GLFWWindow(IView view, int width, int height, String title, Config config) {
		// NOTE: make sure view is not used here in any way
		GLFWWindow shared = GLContextManager.getSharedWindow();
		
		GLFWInit.init();

		GLFW.glfwDefaultWindowHints();

		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);

		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, title != null ? GL11.GL_TRUE : GL11.GL_FALSE);

		window = GLFW.glfwCreateWindow(width, height, title != null ? title : "", MemoryUtil.NULL, shared.window);
		if (window == 0) {
			throw new AssertionError("Failed to create new GLFW window");
		}

		GLFW.glfwSetWindowCloseCallback(window, closeCallback);
		GLFW.glfwSetWindowFocusCallback(window, focusCallback);
		GLFW.glfwSetFramebufferSizeCallback(window, sizeCallback);
		GLFW.glfwSetScrollCallback(window, scrollCallback);
		GLFW.glfwSetKeyCallback(window, keyCallback);
		GLFW.glfwSetCursorEnterCallback(window, mouseEnterCallback);
		GLFW.glfwSetCursorPosCallback(window, mousePositionCallback);
		GLFW.glfwSetMouseButtonCallback(window, mouseButtonCallback);


		makeContextCurrent(true);
		context = GLContext.createFromCurrent();
		makeContextCurrent(false);

		GLFW.glfwShowWindow(window);

		this.view = view;
	}

	private GLFWWindow(GLFWWindow shared) {
		GLFWInit.init();

		GLFW.glfwDefaultWindowHints();

		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);

		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, GL11.GL_FALSE);

		window = GLFW.glfwCreateWindow(16, 16, "", MemoryUtil.NULL, shared != null ? shared.window : 0);
		if (window == MemoryUtil.NULL) {
			throw new AssertionError("Failed to create new GLFW window");
		}
		
		makeContextCurrent(true);
		context = GLContext.createFromCurrent();
		makeContextCurrent(false);

		view = null;
	}

	public void dispose() {
		context.destroy();
		GLFW.glfwDestroyWindow(window);
	}

	public Vec2 getPosition() {
		IntBuffer xpos = BufferUtils.createIntBuffer(1);
		IntBuffer ypos = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowPos(window, xpos, ypos);
		return new Vec2(xpos.get(0), ypos.get(0));
	}

	public void setPosition(Vec2 position) {
		GLFW.glfwSetWindowPos(window, (int) position.x, (int) position.y);
	}

	public Vec2 getFrameBufferSize() {
		return frameBufferSize;
	}

	public synchronized void makeContextCurrent(boolean current) {
		GLFW.glfwMakeContextCurrent(current ? window : 0);
	}

	public static boolean hasContextCurrent() {
		return GLFW.glfwGetCurrentContext() != 0;
	}

	public static GLFWWindow createDummyWindow(GLFWWindow sharedWindow) {
		return new GLFWWindow(sharedWindow);
	}

}
