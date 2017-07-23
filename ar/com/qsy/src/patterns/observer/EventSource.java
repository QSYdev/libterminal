package ar.com.qsy.src.patterns.observer;

import ar.com.qsy.src.patterns.command.Command;

import java.util.LinkedList;
import java.util.List;

public abstract class EventSource implements AutoCloseable {

	private final List<EventListener> listeners;
	private final List<Command> pendingActions;

	public EventSource() {
		this.listeners = new LinkedList<>();
		this.pendingActions = new LinkedList<>();
	}

	public final void addListener(final EventListener eventListener) {
		synchronized (pendingActions) {
			pendingActions.add(() -> listeners.add(eventListener));
		}
	}

	public final void removeListener(final EventListener eventListener) {
		synchronized (pendingActions) {
			pendingActions.add(() -> listeners.remove(eventListener));
		}
	}

	public final void removeAllListeners() {
		synchronized (pendingActions) {
			pendingActions.add(() -> listeners.clear());
		}
	}

	public final void sendEvent(final Event event) throws Exception {
		synchronized (pendingActions) {
			for (final Command action : pendingActions) {
				action.execute();
			}
			pendingActions.clear();
		}
		for (final EventListener eventListener : listeners) {
			eventListener.receiveEvent(event);
		}
	}

	@Override
	public void close() throws Exception {
		for (final Command action : pendingActions) {
			action.execute();
		}
		listeners.clear();
	}

}
