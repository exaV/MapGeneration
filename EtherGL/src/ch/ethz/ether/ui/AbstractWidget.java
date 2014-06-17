package ch.ethz.ether.ui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import ch.ethz.ether.view.IView;

public abstract class AbstractWidget implements IWidget {
	private UI ui;
	private int x;
	private int y;
	private String label;
	private String help;
	private IWidgetAction<? extends IWidget> action;

	protected AbstractWidget(int x, int y, String label, String help, IWidgetAction<? extends IWidget> action) {
		this.x = x;
		this.y = y;
		this.label = label;
		this.help = help;
		this.action = action;
	}

	@Override
	public final UI getUI() {
		return ui;
	}

	@Override
	public final void setUI(UI ui) {
		this.ui = ui;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getHelp() {
		return help;
	}

	@Override
	public boolean hit(int x, int y, IView view) {
		return false;
	}

	@Override
	public IWidgetAction<? extends IWidget> getAction() {
		return action;
	}

	@Override
	public void setAction(IWidgetAction<? extends IWidget> action) {
		this.action = action;
	}

	protected void requestUpdate() {
		if (ui != null)
			ui.requestUpdate();
	}

	@Override
	public boolean keyPressed(KeyEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mouseMoved(MouseEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent e, IView view) {
		return false;
	}
}
