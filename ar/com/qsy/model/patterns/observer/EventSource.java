package ar.com.qsy.model.patterns.observer;

import java.util.LinkedList;
import java.util.List;

import ar.com.qsy.model.patterns.command.Command;

public abstract class EventSource {

	private final List<EventListener> listeners;
	private final List<Command> pendingActions;

	public EventSource() {
		this.listeners = new LinkedList<>();
		this.pendingActions = new LinkedList<>();
	}

	public final void addListener(final EventListener eventListener) {
		pendingActions.add(new Command() {

			@Override
			public void execute() {
				listeners.add(eventListener);
			}

		});
	}

	public final void removeListener(final EventListener eventListener) {
		pendingActions.add(new Command() {

			@Override
			public void execute() {
				listeners.remove(eventListener);
			}

		});
	}

	public final void sendEvent(final Event event) throws InterruptedException {
		for (final Command accion : pendingActions) {
			accion.execute();
		}
		for (final EventListener eventListener : listeners) {
			eventListener.receiveEvent(event);
		}
	}
}
