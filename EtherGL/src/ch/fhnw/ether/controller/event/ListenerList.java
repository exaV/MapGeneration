package ch.fhnw.ether.controller.event;

import java.util.function.Consumer;

import ch.fhnw.util.ArrayUtilities;

public final class ListenerList<L> {
	private volatile L[] listeners;

	public void post(Consumer<L> action) {
		for (L listener : listeners)
			action.accept(listener);
	}
	
	public synchronized void add(L listener) {
		if (ArrayUtilities.contains(listeners, listener))
			throw new IllegalArgumentException("listener already in registered: " + listener);
		
		listeners = ArrayUtilities.append(listeners, listener);
	}
	
	public synchronized void remove(L listener) {
		if (!ArrayUtilities.contains(listeners, listener))
			throw new IllegalArgumentException("listener already not registered: " + listener);
		
		listeners = ArrayUtilities.remove(listeners, listener);
	}
}
