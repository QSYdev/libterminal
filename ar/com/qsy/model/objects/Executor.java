package ar.com.qsy.model.objects;

import ar.com.qsy.model.patterns.observer.EventSource;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Executor extends EventSource {
	protected AtomicBoolean running;

	public void stop() {
		running.set(false);
	}

	public void start() {}

	public void touche(Node node) {}

	public boolean isRunning() {
		return running.get();
	}
}
