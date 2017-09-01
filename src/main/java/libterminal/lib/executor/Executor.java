package libterminal.lib.executor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import libterminal.lib.protocol.CommandParameters;
import libterminal.lib.routine.Color;
import libterminal.lib.routine.Step;
import libterminal.lib.routine.NodeConfiguration;
import libterminal.patterns.observer.Event;
import libterminal.patterns.observer.EventSource;
import libterminal.utils.BiMap;
import libterminal.utils.ExpressionTree;
import libterminal.lib.results.Results;

public abstract class Executor extends EventSource {

	private final AtomicBoolean running;

	private final BiMap biMap;
	private final boolean[] touchedNodes;
	private ExpressionTree expressionTree;

	private Step currentStep;
	private int numberOfStep;

	private final Timer timer;
	private StepTimeOutTimerTask timerTask;
	private Results results;

	public Executor(final TreeMap<Integer, Integer> nodesIdsAssociations, final int numberOfNodes, final Results results) {
		this.running = new AtomicBoolean(false);

		this.biMap = new BiMap(numberOfNodes, nodesIdsAssociations);
		this.touchedNodes = new boolean[numberOfNodes + 1];
		this.expressionTree = null;

		this.currentStep = null;
		this.numberOfStep = 0;

		this.timer = new Timer("Step Time Out", false);
		this.timerTask = null;
		this.results = results;
	}

	public synchronized void start() {
		running.set(true);
		currentStep = getNextStep();
		final Color noColor = new Color((byte) 0, (byte) 0, (byte) 0);
		for (int i = 0; i < touchedNodes.length - 1; i++) {
			final CommandParameters parameters = new CommandParameters(biMap.getPhysicalId(i + 1), 0, noColor);
			sendEvent(new Event(Event.EventType.commandRequest, parameters));
		}
		prepareStep();
		results.start();
	}

	public synchronized void stop() {
		if (running.get()) {
			finalizeStep();
			timer.cancel();
			running.set(false);
		}

	}

	public synchronized void touche(final int physicalIdOfNode, final Color toucheColor, final long toucheDelay) {
		if (running.get()) {
			final int logicalId = biMap.getLogicalId(physicalIdOfNode);
			// TODO comprobar si pertenece al paso actual, modificar el
			// protocolo para incluir el paso
			touchedNodes[logicalId] = true;
			results.touche(logicalId, toucheColor, toucheDelay);
			if (expressionTree.evaluateExpressionTree(touchedNodes)) {
				finalizeStep();
				if (hasNextStep()) {
					currentStep = getNextStep();
					prepareStep();
				} else {
					results.finish();
					sendEvent(new Event(Event.EventType.executorDoneExecuting, null));
				}
			}
		}
	}

	protected synchronized void stepTimeout() {
		if (running.get()) {
			results.stepTimeout();
			sendEvent(new Event(Event.EventType.executorStepTimeout, null));
			if (currentStep.getStopOnTimeout()) {
				results.finish();
				sendEvent(new Event(Event.EventType.executorDoneExecuting, null));
			} else if (!hasNextStep()) {
				results.finish();
				sendEvent(new Event(Event.EventType.executorDoneExecuting, null));
			} else {
				finalizeStep();
				currentStep = getNextStep();
				prepareStep();
			}
		}
	}

	public boolean isRunning() {
		return running.get();
	}

	private void prepareStep() {
		++numberOfStep;
		long maxDelay = 0;
		for (final NodeConfiguration nodeConfiguration : currentStep.getNodesConfiguration()) {
			final int physicalId = biMap.getPhysicalId(nodeConfiguration.getId());
			final long delay = nodeConfiguration.getDelay();
			if (delay > maxDelay) {
				maxDelay = delay;
			}
			final Color color = nodeConfiguration.getColor();
			final CommandParameters parameters = new CommandParameters(physicalId, delay, color);
			sendEvent(new Event(Event.EventType.commandRequest, parameters));
		}
		if (currentStep.getTimeOut() > 0) {
			timer.schedule(timerTask = new StepTimeOutTimerTask(), currentStep.getTimeOut() + maxDelay);
		}
		expressionTree = new ExpressionTree(currentStep.getExpression());
	}

	private void finalizeStep() {
		final Color noColor = new Color((byte) 0, (byte) 0, (byte) 0);
		for (final NodeConfiguration nodeConfiguration : currentStep.getNodesConfiguration()) {
			final int logicalId = nodeConfiguration.getId();
			if (!touchedNodes[logicalId]) {
				final int physicalId = biMap.getPhysicalId(nodeConfiguration.getId());
				final CommandParameters parameters = new CommandParameters(physicalId, 0, noColor);
				sendEvent(new Event(Event.EventType.commandRequest, parameters));
			}
		}
		for (int i = 0; i < touchedNodes.length; i++) {
			touchedNodes[i] = false;
		}
		if (timerTask != null) {
			timerTask.cancel();
		}
		timer.purge();
		expressionTree = null;
	}

	protected BiMap getBiMap() {
		return biMap;
	}

	protected abstract Step getNextStep();

	protected abstract boolean hasNextStep();

	public final Results getResults(){
		return this.results;
	}

	private final class StepTimeOutTimerTask extends TimerTask {

		public StepTimeOutTimerTask() {
		}

		@Override
		public void run() {
			try {
				stepTimeout();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}

}
