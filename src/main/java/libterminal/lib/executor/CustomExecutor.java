package libterminal.lib.executor;

import libterminal.lib.results.CustomResults;
import libterminal.lib.routine.Routine;
import libterminal.lib.routine.Step;

import java.util.Iterator;
import java.util.TreeMap;


public class CustomExecutor extends Executor {

	private final Iterator<Step> routineIterator;

	public CustomExecutor(final Routine routine, final TreeMap<Integer, Integer> nodesIdsAssociations) {
		super(nodesIdsAssociations, routine.getNumberOfNodes(), new CustomResults(routine));
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
