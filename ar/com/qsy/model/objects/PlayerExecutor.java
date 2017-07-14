package ar.com.qsy.model.objects;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ar.com.qsy.model.patterns.observer.Event;

import static ar.com.qsy.model.patterns.observer.Event.EventType.commandPacketSent;
import static ar.com.qsy.model.patterns.observer.Event.EventType.executorDoneExecuting;
import static ar.com.qsy.model.patterns.observer.Event.EventType.executorStepTimeout;

public class PlayerExecutor extends Executor {
	private Timer timer;
	private StepTimeoutTask stepTimeoutTask;
	private RoutineTimeoutTask routineTimeoutTask;
	private Step currentStep;
	private HashMap<Integer, Node> nodesAssociations;
	private ArrayList<Color> playersAndColors;
	private int stepTimeout, executedSteps, totalSteps;
	private boolean soundEnabled, touchEnabled;
	private long maxExecTime;
	private Set<Integer> touchedNodes;

	public PlayerExecutor(ArrayList<Color> playersAndColors, HashMap<Integer, Node> nodesAssociations,
	                      boolean soundEnabled, boolean touchEnabled, long maxExecTime, int totalSteps, int stepTimeout) {

		this.running = new AtomicBoolean(false);
		this.timer = new Timer("Step timeouts");
		this.nodesAssociations = nodesAssociations;
		this.playersAndColors = playersAndColors;
		this.stepTimeout = stepTimeout;
		this.soundEnabled = soundEnabled;
		this.touchEnabled = touchEnabled;
		this.maxExecTime = maxExecTime;
		this.totalSteps = totalSteps;
		this.executedSteps = 0;
	}

	/**
	 * start simplemente setea el flag de ejecutando a true y comienza la ejecucion.
	 */
	@Override
	public void start() {
		this.running.set(true);
		routineTimeoutTask = new RoutineTimeoutTask();
		this.timer.schedule(routineTimeoutTask, maxExecTime);
		executeNextStep();
		executedSteps++;
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

	/**
	 * continueExecution procede a ejecutar el siguiente paso en caso de que lo haya. Si no lo hay entonces avisa,
	 * a los que sea que esten escuchando, que la ejecucion de la rutina termino.
	 */
	@Override
	public void continueExecution() {
		if (executedSteps < totalSteps) {
			executeNextStep();
			executedSteps++;
		} else {
			try {
				// TODO: fijarse que tendria que ir en content
				sendEvent(new Event(executorDoneExecuting, null));
			} catch (Exception e) {
				// TODO: manejar excepciones bien
				e.printStackTrace();
			}
		}
	}

	private void executeNextStep() {
		turnOffCurrentStep();
		if (!running.get()) {
			return;
		}
		touchedNodes = new HashSet<>();
		currentStep = generateNextStep();

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
			stepTimeoutTask = new StepTimeoutTask();
			timer.schedule(stepTimeoutTask, maxDelay);
		}
	}

	/*
	 * generateNextStep devuelve un Step con toda la configuracion de los nodos cargada, y con la expresion
	 * del paso random. Por ahora, la expresion es con & de todos los nodos unicamente. Pero mas adelante podemos
	 * darle la opcion al usuario de que elijan que sea & o |. Con & todos los jugadores del paso tienen que apagar
	 * sus nodos para poder pasar de paso, en cambio con | el paso se cumple una vez que el primer jugador lo apaga.
	 */
	private Step generateNextStep() {
		int i = 0;
		// esto genera una lista de enteros desde 1 hasta la cantidad de jugadores que hay
		// TODO: cuando playersAndColors.size es 1, estamos creando y haciendo banda de giladas al pedo
		List<Integer> list = IntStream.of(IntStream.rangeClosed(1, playersAndColors.size()).
			toArray()).boxed().collect(Collectors.toList());
		// shuffle desordena la lista que generamos antes
		Collections.shuffle(list);
		ArrayList<NodeConfiguration> nodesConfigurations = new ArrayList<>();
		String stepExpression = "";
		for (Color color : playersAndColors) {
			// aca obtenemos uno que sabemos que va a ser unico y random gracias al shuffle
			Integer logicId = list.get(i++);
			nodesConfigurations.add(new NodeConfiguration(logicId, 0, color));
			stepExpression = stepExpression.concat(logicId.toString().concat("&"));
		}
		return new Step(nodesConfigurations, this.stepTimeout, stepExpression);
	}

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

	private int getLogicIdFromNodeId(int nodeId) {
		for (Map.Entry<Integer, Node> entry : nodesAssociations.entrySet()) {
			if (entry.getValue().getNodeId() == nodeId) {
				return entry.getKey();
			}
		}
		return -1;
	}

	@Override
	public void stop() {
		super.stop();
		timer.cancel();
	}

	private class StepTimeoutTask extends TimerTask {

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

	private class RoutineTimeoutTask extends TimerTask {

		@Override
		public void run() {
			try {
				sendEvent(new Event(executorDoneExecuting, null));
			} catch (Exception e) {
				// TODO: manejo de excepciones
				e.printStackTrace();
			}
		}
	}
}
