package ar.com.qsy.src.app.executor;

import ar.com.qsy.src.app.protocol.CommandParameters;
import ar.com.qsy.src.app.routine.Color;
import ar.com.qsy.src.app.routine.NodeConfiguration;
import ar.com.qsy.src.app.routine.Step;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.Event.EventType;
import ar.com.qsy.src.patterns.observer.EventSource;
import ar.com.qsy.src.utils.BiMap;
import ar.com.qsy.src.utils.ExpressionTree;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Executor extends EventSource {

	private final AtomicBoolean running;

	private final BiMap biMap;
	private final boolean[] touchedNodes;
	private ExpressionTree expressionTree;

	private Step currentStep;
	private int numberOfStep;

	private final Timer timer;
	private StepTimeOutTimerTask timerTask;

	public Executor(final TreeMap<Integer, Integer> nodesIdsAssociations, final int numberOfNodes) {
		this.running = new AtomicBoolean(false);

		this.biMap = new BiMap(numberOfNodes, nodesIdsAssociations);
		this.touchedNodes = new boolean[numberOfNodes + 1];
		this.expressionTree = null;

		this.currentStep = null;
		this.numberOfStep = 0;

		this.timer = new Timer("Step Time Out", false);
		this.timerTask = null;
	}

	public synchronized void start() throws Exception {
		running.set(true);
		currentStep = getNextStep();
		prepareStep();
	}

	public synchronized void stop() {
		if (running.get()) {
			finalizeStep();
			timer.cancel();
			running.set(false);
		}

	}

	public synchronized void touche(final int physicalIdOfNode) {
		if (running.get()) {
			final int logicalId = biMap.getLogicalId(physicalIdOfNode);
			// TODO comprobar si pertenece al paso actual, modificar el
			// protocolo para incluir el paso
			touchedNodes[logicalId] = true;
			// TODO almacenar en log aca.
			if (expressionTree.evaluateExpressionTree(touchedNodes)) {
				finalizeStep();
				if (hasNextStep()) {
					currentStep = getNextStep();
					prepareStep();
				} else {
					sendEvent(new Event(EventType.executorDoneExecuting, null));
				}
			}
		}
	}

	protected synchronized void stepTimeout() throws Exception {
		if (running.get()) {
			sendEvent(new Event(Event.EventType.executorStepTimeout, null));
			if (currentStep.getStopOnTimeout()) {
				sendEvent(new Event(EventType.executorDoneExecuting, null));
			} else if (!hasNextStep()) {
				sendEvent(new Event(EventType.executorDoneExecuting, null));
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
			sendEvent(new Event(EventType.commandRequest, parameters));
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
				sendEvent(new Event(EventType.commandRequest, parameters));
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
