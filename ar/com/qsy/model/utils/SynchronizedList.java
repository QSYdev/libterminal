package ar.com.qsy.model.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ar.com.qsy.model.patterns.command.Command;

public final class SynchronizedList<T> implements Iterable<T> {

	private final List<T> list;
	private final List<Command> pendingActions;

	public SynchronizedList(final List<T> list) {
		this.list = new LinkedList<>(list);
		this.pendingActions = new LinkedList<>();
	}

	public SynchronizedList() {
		this(new LinkedList<T>());
	}

	public void tick() {
		for (final Command accion : pendingActions) {
			accion.execute();
		}
		pendingActions.clear();
	}

	public T get(final int index) {
		return list.get(index);
	}

	public void add(final T elem) {
		pendingActions.add(new Command() {

			@Override
			public void execute() {
				list.add(elem);
			}

		});
	}

	public void remove(final T elem) {
		pendingActions.add(new Command() {

			@Override
			public void execute() {
				list.remove(elem);
			}

		});
	}

	public void remove(final int index) {
		pendingActions.add(new Command() {
			@Override
			public void execute() {
				list.remove(index);
			}
		});
	}

	public void clear() {
		pendingActions.add(new Command() {

			@Override
			public void execute() {
				list.clear();
			}

		});
	}

	public boolean contains(final T elem) {
		return list.contains(elem);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public int size() {
		return list.size();
	}

	public List<T> getReadOnlyList() {
		return list;
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}
}
