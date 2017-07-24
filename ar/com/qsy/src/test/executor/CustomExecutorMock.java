package ar.com.qsy.src.test.executor;

import ar.com.qsy.src.app.executor.CustomExecutor;
import ar.com.qsy.src.app.routine.Routine;
import ar.com.qsy.src.app.routine.Step;

import java.util.TreeMap;

public class CustomExecutorMock extends CustomExecutor {

	public CustomExecutorMock(Routine routine, TreeMap<Integer, Integer> nodesIdsAssociations) {
		super(routine, nodesIdsAssociations);
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
