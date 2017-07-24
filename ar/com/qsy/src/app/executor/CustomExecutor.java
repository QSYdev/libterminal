package ar.com.qsy.src.app.executor;

import ar.com.qsy.src.app.routine.Routine;
import ar.com.qsy.src.app.routine.Step;

import java.util.Iterator;
import java.util.TreeMap;


public class CustomExecutor extends Executor {

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
