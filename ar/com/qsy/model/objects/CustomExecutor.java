package ar.com.qsy.model.objects;

import ar.com.qsy.model.patterns.observer.Event;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static ar.com.qsy.model.patterns.observer.Event.EventType.commandPacketSent;
import static ar.com.qsy.model.patterns.observer.Event.EventType.executorDoneExecuting;
import static ar.com.qsy.model.patterns.observer.Event.EventType.executorStepTimeout;

public class CustomExecutor extends Executor {
	private Timer timer;
	private StepTimeout stepTimeoutTask;
	private Routine routine;
	private HashMap<Integer, Node> nodesAssociations;
	private Step currentStep;
	private Set<Integer> touchedNodes;
	private boolean touchEnabled;
	private boolean soundEnabled;

	public CustomExecutor(Routine routine, HashMap<Integer, Node> nodes, boolean soundEnabled, boolean touchEnabled) {
		this.running = new AtomicBoolean(false);
		this.timer = new Timer("Step timeouts");
		this.nodesAssociations = nodes;
		this.routine = routine;
		this.soundEnabled = soundEnabled;
		this.touchEnabled = touchEnabled;
	}

	/**
	 * start simplemente setea el flag de ejecutando a true y comienza la ejecucion.
	 */
	@Override
	public void start() {
		this.running.set(true);
		continueExecution();
	}

	/**
	 * continueExecution procede a ejecutar el siguiente paso en caso de que lo haya. Si no lo hay entonces avisa,
	 * a los que sea que esten escuchando, que la ejecucion de la rutina termino.
	 */
	@Override
	public void continueExecution() {
		if (routine.hasNext()) {
			executeNextStep();
		} else {
			try {
				// TODO: fijarse que tendria que ir de content
				sendEvent(new Event(executorDoneExecuting, null));
			} catch (Exception e) {
				// TODO: manejar excepciones bien
				e.printStackTrace();
			}
		}
	}

	/*
	 * executeNextStep es donde pasa la posta de la ejecucion. En este metodo se apagan los nodos que quedaron
	 * prendidos del paso actual, se obtiene el paso siguiente, se mandan todos los qsy packets necesarios y por
	 * ultimo se crea el timeout, si es que hay, acorde al step que se va a ejecutar
	 */
	private void executeNextStep() {
		turnOffCurrentStep();
		if (!running.get()) {
			return;
		}
		touchedNodes = new HashSet<>();
		currentStep = routine.next();
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
			stepTimeoutTask.cancel();
			timer.purge();
			maxDelay = maxDelay + timeout;
			stepTimeoutTask = new StepTimeout();
			timer.schedule(stepTimeoutTask, maxDelay);
		}
	}

	/**
	 * touche agrega el nodo correspondiente a los nodos tocados del paso actual.
	 *
	 * @param node: el nodo fisico que fue tocado por el usuario
	 */
	@Override
	public void touche(Node node) {
		int nodeId = node.getNodeId();
		int logicId = getLogicIdFromNodeId(nodeId);
		if (logicId == -1) {
			// se toco un nodo que no es de la rutina, nose cuando puede pasar
			return;
		}
		// TODO: chequear si esta touchEnabled y chequear si se toco o se paso la mano
		touchedNodes.add(logicId);
		if (!currentStep.isFinished(touchedNodes)) {
			return;
		}
		continueExecution();
	}

	@Override
	public void stop() {
		super.stop();
		timer.cancel();
	}

	/*
	 * getLogicIdFromNodeId recibe el id fisico de un nodo y devuelve el id logico que tiene asociado en la rutina
	 * actual.
	 */
	private int getLogicIdFromNodeId(int nodeId) {
		for (Map.Entry<Integer, Node> entry : nodesAssociations.entrySet()) {
			if (entry.getValue().getNodeId() == nodeId) {
				return entry.getKey();
			}
		}
		return -1;
	}

	/*
	 * turnOffCurrentStep apaga todos los nodos del paso actual que no fueron tocados
	 */
	private void turnOffCurrentStep() {
		QSYPacket qsyPacket;
		ArrayList<NodeConfiguration> stepNodes = currentStep.getNodes();
		for (NodeConfiguration nodeConfiguration : stepNodes) {
			if (touchedNodes.contains(nodeConfiguration)) {
				continue;
			}
			int logicId = nodeConfiguration.getId();
			// TODO: aca en color le tenemos que mandar el color que tiene valor 0
			qsyPacket = QSYPacket.createCommandPacket(this.nodesAssociations.get(logicId).getNodeAddress(),
				this.nodesAssociations.get(logicId).getNodeId(),
				nodeConfiguration.getColor(),
				0);
			try {
				sendEvent(new Event(commandPacketSent, qsyPacket));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class StepTimeout extends TimerTask {

		@Override
		public void run() {
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
