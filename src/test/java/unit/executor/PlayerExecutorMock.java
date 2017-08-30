package unit.executor;

import libterminal.lib.executor.PlayerExecutor;
import libterminal.lib.routine.Color;
import libterminal.lib.routine.Step;

import java.util.ArrayList;
import java.util.TreeMap;

public class PlayerExecutorMock extends PlayerExecutor{
	public PlayerExecutorMock(TreeMap<Integer, Integer> nodesIdsAssociations, int numberOfNodes, ArrayList<Color> playersAndColors, boolean waitForAllPlayers, long timeOut, long delay, long maxExecTime, int totalStep, boolean stopOnTimeout) {
		super(nodesIdsAssociations, numberOfNodes, playersAndColors, waitForAllPlayers, timeOut, delay, maxExecTime, totalStep, stopOnTimeout);
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
