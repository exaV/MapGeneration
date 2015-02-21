package ch.fhnw.ether.controller.event;

import org.lwjgl.glfw.GLFW;

import ch.fhnw.ether.view.IView;

public class MouseEvent extends AbstractEvent {
	public static final int LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
	public static final int RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	public static final int MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

	private final int button;
	private final float x;
	private final float y;
	private final float lastX;
	private final float lastY;
	private final int modifiers;

	public MouseEvent(IView view, int button, float x, float y, float lastX, float lastY, int modifiers) {
		super(view);
		this.button = button;
		this.x = x;
		this.y = y;
		this.lastX = lastX;
		this.lastY = lastY;
		this.modifiers = modifiers;
	}

	public int getButton() {
		return button;
	}

	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getLastX() {
		return lastX;
	}
	
	public float getLastY() {
		return lastY;
	}
	
	public float getDeltaX() {
		return x - lastX;
	}

	public float getDeltaY() {
		return y - lastY;
	}

	public boolean hasModifiers() {
		return modifiers > 0;
	}
	
	public boolean hasShift() {
		return (modifiers & KeyEvent.MOD_SHIFT) > 0;
	}

	public boolean hasControl() {
		return (modifiers & KeyEvent.MOD_CONTROL) > 0;
	}

	public boolean hasAlt() {
		return (modifiers & KeyEvent.MOD_ALT) > 0;
	}

	public boolean hasSuper() {
		return (modifiers & KeyEvent.MOD_SUPER) > 0;
	}
}
