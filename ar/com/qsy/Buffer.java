package ar.com.qsy;

import java.util.LinkedList;
import java.util.List;

public final class Buffer<T> {

	private final List<T> elements;

	public Buffer() {
		this.elements = new LinkedList<>();
	}

	public synchronized void add(final T elem) {
		elements.add(elem);
		notify();
	}

	public synchronized T remove(final long timeOut) {
		while (elements.isEmpty()) {
			try {
				wait(timeOut);
				if (timeOut > 0 && elements.isEmpty()) {
					return null;
				}
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return elements.remove(0);
	}

	public synchronized T remove() {
		return remove(0);
	}

}
