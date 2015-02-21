package ch.fhnw.ether.controller.event;

import ch.fhnw.ether.view.IView;

public class WindowEvent extends AbstractEvent {

	public WindowEvent(IView view) {
		super(view);
	}

}
