package ar.com.qsy.model.patterns.observer;

import ar.com.qsy.model.interfaces.Cleanable;
import ar.com.qsy.model.utils.SynchronizedList;

public abstract class Observable implements Cleanable {

	private final SynchronizedList<Observer> observers;

	public Observable() {
		this.observers = new SynchronizedList<>();
	}

	public void addObserver(final Observer obs) {
		observers.add(obs);
	}

	public void removeObserver(final Observer obs) {
		observers.remove(obs);
	}

	public void clearObserver() {
		observers.clear();
	}

	public void notifyEvent(final EventArgs e) {
		observers.tick();
		for (final Observer observer : observers) {
			observer.notify(e);
		}
		observers.tick();
	}

	@Override
	public void cleanUp() {
		observers.clear();
		observers.tick();
	}

	@Override
	protected void finalize() throws Throwable {
		cleanUp();
		super.finalize();
	}

}
