package unit.executor;

import libterminal.lib.executor.CustomExecutor;
import libterminal.lib.routine.Routine;
import libterminal.lib.routine.Step;

import java.util.TreeMap;

public class CustomExecutorMock extends CustomExecutor {

	public CustomExecutorMock(Routine routine, TreeMap<Integer, Integer> nodesIdsAssociations) {
		super(routine, nodesIdsAssociations, 0L);
	}

	@Override
	protected Step getNextStep() {
		return super.getNextStep();
	}

	@Override
	protected boolean hasNextStep() {
		return super.hasNextStep();
	}
}
