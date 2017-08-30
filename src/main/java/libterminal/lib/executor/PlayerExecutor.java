package libterminal.lib.executor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import libterminal.lib.routine.Color;
import libterminal.lib.routine.NodeConfiguration;
import libterminal.lib.routine.Step;
import libterminal.patterns.observer.Event;

public class PlayerExecutor extends Executor {

	private final ArrayList<Color> playersAndColors;
	private final ArrayList<Color> stepsWinners;
	private final TreeMap<Integer, Color> logicalIdsAndColors;

	private final boolean waitForAllPlayers;
	private final long timeOut;
	private final long delay;
	private final long maxExecTime;
	private final int totalStep;
	private final boolean stopOnTimeout;
	private final int numberOfNodes;

	private final Timer timer;
	private RoutineTimerTask timerTask;

	private int stepIndex;

	public PlayerExecutor(final TreeMap<Integer, Integer> nodesIdsAssociations, final int numberOfNodes, final ArrayList<Color> playersAndColors, final boolean waitForAllPlayers, final long timeOut,
			final long delay, final long maxExecTime, final int totalStep, final boolean stopOnTimeout) {

		super(nodesIdsAssociations, numberOfNodes);

		this.playersAndColors = playersAndColors;
		this.stepsWinners = new ArrayList<>();
		this.logicalIdsAndColors = new TreeMap<>();

		this.waitForAllPlayers = waitForAllPlayers;
		this.timeOut = timeOut;
		this.delay = delay;
		this.maxExecTime = maxExecTime;
		this.totalStep = totalStep;
		this.stopOnTimeout = stopOnTimeout;
		this.numberOfNodes = numberOfNodes;

		this.timer = new Timer("Routine Time Out", false);
		this.timerTask = null;

		this.stepIndex = 0;

	}

	@Override
	public synchronized void start() {
		stepIndex = 0;
		if (maxExecTime > 0) {
			timer.schedule(timerTask = new RoutineTimerTask(), maxExecTime);
		}
		super.start();
	}

	@Override
	public synchronized void stop() {
		if (isRunning()) {
			if (timerTask != null) {
				timerTask.cancel();
			}
			timer.cancel();
		}
		super.stop();
	}

	@Override
	public synchronized void touche(int physicalIdOfNode) {
		if (stepsWinners.size() < stepIndex) {
			final Color colorWinner = logicalIdsAndColors.get(getBiMap().getLogicalId(physicalIdOfNode));
			stepsWinners.add(colorWinner);
		}
		super.touche(physicalIdOfNode);
	}

	@Override
	protected Step getNextStep() {
		logicalIdsAndColors.clear();

		final char booleanOperator = (waitForAllPlayers) ? '&' : '|';
		final LinkedList<Integer> usedIds = new LinkedList<>();
		for (int i = 1; i <= numberOfNodes; i++) {
			usedIds.add(i);
		}
		final LinkedList<NodeConfiguration> currentNodesConfiguration = new LinkedList<>();
		final StringBuilder sb = new StringBuilder();

		for (final Color color : playersAndColors) {
			final int id = usedIds.remove(ThreadLocalRandom.current().nextInt(0, usedIds.size()));
			currentNodesConfiguration.add(new NodeConfiguration(id, delay, color));
			logicalIdsAndColors.put(id, color);
			sb.append(id);
			sb.append(booleanOperator);
		}
		sb.deleteCharAt(sb.length() - 1);

		final String expression = sb.toString();

		++stepIndex;
		return new Step(currentNodesConfiguration, timeOut, expression, stopOnTimeout);
	}

	@Override
	protected boolean hasNextStep() {
		return totalStep == 0 || stepIndex < totalStep;
	}

	@Override
	protected synchronized void stepTimeout() {
		stepsWinners.add(null);
		super.stepTimeout();
	}

	private final class RoutineTimerTask extends TimerTask {

		public RoutineTimerTask() {
		}

		@Override
		public void run() {
			if (isRunning()) {
				sendEvent(new Event(Event.EventType.executorDoneExecuting, null));
			}
		}

	}

}
