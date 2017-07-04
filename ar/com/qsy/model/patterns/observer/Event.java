package ar.com.qsy.model.patterns.observer;

public abstract class Event {

	private final EventType eventType;

	public static enum EventType {

	}

	public Event(final EventType eventType) {
		this.eventType = eventType;
	}

	public final EventType getEventType() {
		return eventType;
	}

	public abstract Object getContent();

}
