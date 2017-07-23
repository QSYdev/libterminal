package ar.com.qsy.src.patterns.observer;

public interface EventListener {

	void receiveEvent(final Event event) throws Exception;

}
