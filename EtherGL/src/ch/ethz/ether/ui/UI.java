package ch.ethz.ether.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.ether.render.IRenderGroup.Flag;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.render.TextRenderGroup;
import ch.ethz.ether.scene.IScene;
import ch.ethz.util.UpdateRequest;

public final class UI {
	private static final Color TEXT_COLOR = Color.WHITE;

	private final IScene scene;
	private final TextRenderGroup group = new TextRenderGroup(Source.TOOL, 0, 0, 512, 512);
	private final UpdateRequest updater = new UpdateRequest();

	private final List<Button> buttons = new ArrayList<>();
	private String message;

	public UI(IScene scene) {
		this.scene = scene;
		enable();
		group.addFlag(Flag.INTERACTIVE_VIEW_ONLY);
	}

	public void enable() {
		IRenderer.GROUPS.add(group);
		requestUpdate();
	}

	public void disable() {
		IRenderer.GROUPS.remove(group);
	}

	public void update() {
		if (!updater.needsUpdate())
			return;

		group.clear();

		int bw = Button.BUTTON_WIDTH;
		int bh = Button.BUTTON_HEIGHT;
		int bg = Button.BUTTON_GAP;
		for (Button button : buttons) {
			int bx = button.getX() * (bg + bw);
			int by = button.getY() * (bg + bh);
			group.fillRect(button.getState().getColor(), bx, by, bw, bh);
			String label = button.getLabel();
			if (label != null)
				group.drawString(TEXT_COLOR, label, bx + 2, by + bh - 4);
		}

		if (message != null)
			group.drawString(message, 0, group.getHeight() - TextRenderGroup.FONT.getSize());
	}

	public List<Button> getButtons() {
		return Collections.unmodifiableList(buttons);
	}

	public void addButton(Button button) {
		button.setUI(this);
		buttons.add(button);
		requestUpdate();
	}

	public void addButtons(Collection<? extends Button> buttons) {
		for (Button button : buttons) {
			addButton(button);
		}
	}

	public void setMessage(String message) {
		if (this.message == message || (this.message != null && this.message.equals(message)))
			return;
		this.message = message;
		requestUpdate();
	}

	public int getX() {
		return group.getX();
	}

	public int getY() {
		return group.getX();
	}

	public int getWidth() {
		return group.getWidth();
	}

	public int getHeight() {
		return group.getHeight();
	}

	void requestUpdate() {
		updater.requestUpdate();
		scene.repaintViews();
	}
}
