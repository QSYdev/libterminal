package ar.com.qsy.model.objects;

import java.util.Iterator;
import java.util.TreeMap;

public final class CustomExecutor extends Executor {

	private final Iterator<Step> routineIterator;

	public CustomExecutor(final Routine routine, final TreeMap<Integer, Integer> nodesIdsAssociations) {
		super(nodesIdsAssociations, routine.getNumberOfNodes());
		this.routineIterator = routine.iterator();
	}

	@Override
	protected Step getNextStep() {
		return routineIterator.next();
	}

	@Override
	protected boolean hasNextStep() {
		return routineIterator.hasNext();
	}

}
