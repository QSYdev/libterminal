package ar.com.qsy.model.objects;

import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.Event.EventType;
import java.awt.Color;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomExecutor extends Executor {
	private Timer timer;
	private StepTimeout stepTimeoutTask;
	private Routine routine;
	private HashMap<Integer, InetAddress> nodes;
	private Step currentStep;
	private Set<Integer> touchedNodes;

	public CustomExecutor(Routine routine, HashMap<Integer, InetAddress> nodes) {
		this.running = new AtomicBoolean(false);
		this.timer = new Timer("Step timeouts");
		this.nodes = nodes;
		this.routine = routine;
	}

	@Override
	public void start() {
		this.running.set(true);
		executeNextStep();
	}

	private void executeNextStep() {
		currentStep = routine.getSteps().next();
		ArrayList<NodeConfiguration> nodes = step.getNodes();
		QSYPacket qsyPacket;
		long totalDelay = -1;

		for (ArrayList<NodeConfiguration> node : nodes) {
			final int id = node.getId();
			final int delay = node.getDelay();
			if(delay > totalDelay) {
				totalDelay = delay;
			}
			qsyPacket = QSYPacket.createCommandPacket(this.nodes.get(id),id, node.getColor(), delay);
			sendEvent(new Event(commandPacketSent, qsyPacket));
		}

		int timeout = currentStep.getTimeout();
		if (timeout > 0) {
			stepTimeoutTask.cancel();
			timer.purge();
			totalDelay = totalDelay + timeout;
			stepTimeoutTask = new StepTimeout();
			timer.schedule(stepTimeoutTask, totalDelay);
		}
	}

	@Override
	public void touche(Node node) {
		int id = node.getNodeId();
		touchedNodes.add(id);
		if(!currentStep.isFinished(touchedNodes)) {
			return;
		}
		if(routine.getSteps().hasNext()) {
			executeNextStep();
		}
	}

	@Override
	public void stop() {
		super.stop();
		timer.cancel();
	}

	private class StepTimeout extends TimerTask {

		@Override
		public void run() {
			/*
			 * TODO: chequear timeout de steps
			 * dentro de este metodo deberiamos confirmar que el step actual
			 * haya sido terminado, si no se termino tenemos que hacer lo
			 * que la rutina especifique. Por eso para mi las rutinas custom
			 * tendrian que especificar si el timeout de un paso causa que se
			 * termine la rutina o si simplemente se lo marca como no tocado y
			 * se continua.
			 */
		}
	}
}
