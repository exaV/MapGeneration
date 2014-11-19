package ch.fhnw.util;

import java.util.ArrayList;
import java.util.List;

public interface IUpdateRequester {
	
	final class UpdateListeners implements IUpdateListener {
		private final List<IUpdateListener> listeners = new ArrayList<>();
		
		public UpdateListeners() {
			
		}
		
		public void addListener(IUpdateListener listener) {
			listeners.add(listener);
		}

		public void removeListener(IUpdateListener listener) {
			listeners.remove(listener);
		}
		
		public void clear() {
			listeners.clear();
		}
		
		@Override
		public void requestUpdate(Object source) {
			for (IUpdateListener listener : listeners)
				listener.requestUpdate(source);
		}
	}
	
	void addUpdateListener(IUpdateListener listener);
	void removeUpdateListener(IUpdateListener listener);
}
