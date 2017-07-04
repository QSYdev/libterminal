package ar.com.qsy.model.patterns.observer;

public interface EventListener {

	void receiveEvent(final Event event) throws InterruptedException;

}
