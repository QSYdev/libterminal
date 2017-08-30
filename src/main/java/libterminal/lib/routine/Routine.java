package libterminal.lib.routine;

import java.util.ArrayList;
import java.util.Iterator;

public final class Routine implements Iterable<Step> {

	private final byte numberOfNodes;
	private final ArrayList<Step> steps;

	public Routine(final byte numberOfNodes, final ArrayList<Step> steps) {
		this.numberOfNodes = numberOfNodes;
		this.steps = steps;
	}

	public byte getNumberOfNodes() {
		return numberOfNodes;
	}

	public ArrayList<Step> getSteps() {
		return steps;
	}

	@Override
	public Iterator<Step> iterator() {
		return new RoutineIterator();
	}

	private final class RoutineIterator implements Iterator<Step> {

		private int index;

		public RoutineIterator() {
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return index < steps.size();
		}

		@Override
		public Step next() {
			return getSteps().get(index++);
		}

	}
}
