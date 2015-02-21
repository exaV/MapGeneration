package ch.fhnw.ether.controller.event;

import ch.fhnw.ether.view.IView;

public abstract class AbstractEvent implements IEvent {

	private final IView view;
	
	protected AbstractEvent(IView view) {
		this.view = view;
	}
	
	@Override
	public final IView getView() {
		return view;
	}
}
