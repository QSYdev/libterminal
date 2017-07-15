package ar.com.qsy.model.objects;

import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.EventSource;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static ar.com.qsy.model.patterns.observer.Event.EventType.commandPacketSent;
import static ar.com.qsy.model.patterns.observer.Event.EventType.executorStepTimeout;

public abstract class Executor extends EventSource {
	protected AtomicBoolean running;
	protected Timer timer;
	protected StepTimeoutTask stepTimeoutTask;
	protected Step currentStep;
	protected Set<Integer> touchedNodes;
	protected HashMap<Integer, Node> nodesAssociations;

	public void stop() {
		running.set(false);
		timer.cancel();
	}

	public abstract void start();

	/**
	 * touche agrega el nodo correspondiente a los nodos tocados del paso actual.
	 *
	 * @param node: el nodo fisico que fue tocado por el usuario
	 */
	public void touche(Node node) {
		int logicId = getLogicIdFromNodeId(node.getNodeId());
		if (logicId == -1) {
			// se toco un nodo que no es de la rutina, nose cuando puede pasar
			return;
		}
		touchedNodes.add(logicId);
	}

	public boolean isRunning() {
		return running.get();
	}

	/**
	 * stepTimeout hace lo que se debe hacer en caso de que se de el timeout del step.
	 */
	public abstract void stepTimeout();

	/*
	 * turnOffCurrentStep apaga todos los nodos del paso actual que no fueron tocados
	 */
	protected void turnOffCurrentStep() {
		QSYPacket qsyPacket;
		ArrayList<NodeConfiguration> stepNodes = currentStep.getNodes();
		for (NodeConfiguration nodeConfiguration : stepNodes) {
			if (touchedNodes.contains(nodeConfiguration.getId())) {
				continue;
			}
			int logicId = nodeConfiguration.getId();
			qsyPacket = QSYPacket.createCommandPacket(this.nodesAssociations.get(logicId).getNodeAddress(),
				this.nodesAssociations.get(logicId).getNodeId(),
				null,
				0);
			try {
				sendEvent(new Event(commandPacketSent, qsyPacket));
			} catch (Exception e) {
				// TODO: manejo de excepciones
				e.printStackTrace();
			}

		}
	}

	/*
	 * getLogicIdFromNodeId recibe el id fisico de un nodo y devuelve el id logico que tiene asociado en la rutina
	 * actual.
	 */
	protected int getLogicIdFromNodeId(int nodeId) {
		for (Map.Entry<Integer, Node> entry : nodesAssociations.entrySet()) {
			if (entry.getValue().getNodeId() == nodeId) {
				return entry.getKey();
			}
		}
		return -1;
	}

	protected void executeNextStep() {
		ArrayList<NodeConfiguration> nodesConfiguration = currentStep.getNodes();
		QSYPacket qsyPacket;
		long maxDelay = -1;

		for (NodeConfiguration nodeConfiguration : nodesConfiguration) {
			final int logicId = nodeConfiguration.getId();
			final int delay = nodeConfiguration.getDelay();
			if (delay > maxDelay) {
				maxDelay = delay;
			}
			// TODO: cuando se cambie el protocolo para incluir el sonido lo tenemos que mandar aca
			// solo si soundEnabled es true
			// TODO: touchEnabled
			qsyPacket = QSYPacket.createCommandPacket(this.nodesAssociations.get(logicId).getNodeAddress(),
				this.nodesAssociations.get(logicId).getNodeId(),
				nodeConfiguration.getColor(),
				delay);
			try {
				sendEvent(new Event(commandPacketSent, qsyPacket));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int timeout = currentStep.getTimeout();
		if (timeout > 0) {
			maxDelay = maxDelay + timeout;
			stepTimeoutTask = new StepTimeoutTask();
			timer.schedule(stepTimeoutTask, maxDelay);
		}
	}

	protected class StepTimeoutTask extends TimerTask {

		@Override
		public void run() {
			// TODO: que pasa si se toca el que falta cuando estamos aca
			if (currentStep.isFinished(touchedNodes)) return;

			try {
				sendEvent(new Event(executorStepTimeout, null));
			} catch (Exception e) {
				// TODO: manejo correcto de excepciones
				e.printStackTrace();
			}
		}
	}


}
