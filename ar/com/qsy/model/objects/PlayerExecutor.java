package ar.com.qsy.model.objects;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ar.com.qsy.model.patterns.observer.Event;

import static ar.com.qsy.model.patterns.observer.Event.EventType.executorDoneExecuting;

public class PlayerExecutor extends Executor {
	private RoutineTimeoutTask routineTimeoutTask;
	private ArrayList<Color> playersAndColors, stepsWinners;
	private ArrayList<NodeConfiguration> currentStepConfiguration;
	private int stepTimeout, executedSteps, totalSteps;
	private long maxExecTime;
	private boolean soundEnabled, touchEnabled;

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
		this.stepsWinners = new ArrayList<>();
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
		continueExecution();
	}

	/**
	 * touche agrega el nodo correspondiente a los nodos tocados del paso actual.
	 *
	 * @param node: el nodo fisico que fue tocado por el usuario
	 */
	public void touche(Node node) {
		super.touche(node);
		Color color = getPlayerColorFromLogicId(getLogicIdFromNodeId(node.getNodeId()));
		if(stepsWinners.size() < executedSteps) {
			stepsWinners.add(color);
		}
		if (!currentStep.isFinished(touchedNodes)) {
			return;
		}
		continueExecution();
	}

	private Color getPlayerColorFromLogicId(int logicId) {
		for(NodeConfiguration nodeConfiguration : currentStepConfiguration) {
			if(nodeConfiguration.getId() == logicId) {
				return nodeConfiguration.getColor();
			}
		}
		return null;
	}

	@Override
	public void stepTimeout() {
		stepsWinners.add(null);
		continueExecution();
	}

	/**
	 * stepTimeout procede a ejecutar el siguiente paso en caso de que corresponda. Si no lo hay entonces avisa,
	 * a los que sea que esten escuchando, que la ejecucion de la rutina termino.
	 */
	private void continueExecution() {
		if (executedSteps < totalSteps) {
			executeNextStep();
			executedSteps++;
		} else {
			try {
				sendEvent(new Event(executorDoneExecuting, null));
			} catch (Exception e) {
				// TODO: manejar excepciones bien
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void executeNextStep() {
		turnOffCurrentStep();
		if (!running.get()) {
			return;
		}
		stepTimeoutTask.cancel();
		timer.purge();
		touchedNodes = new HashSet<>();
		currentStep = generateNextStep();

		super.executeNextStep();
	}

	/*
	 * generateNextStep devuelve un Step con toda la configuracion de los nodos cargada, y con la expresion
	 * del paso random. Por ahora, la expresion es con & de todos los nodos unicamente. Pero mas adelante podemos
	 * darle la opcion al usuario de que elijan que sea & o |. Con & todos los jugadores del paso tienen que apagar
	 * sus nodos para poder pasar de paso, en cambio con | el paso se cumple una vez que el primer jugador lo apaga.
	 */
	private Step generateNextStep() {
		currentStepConfiguration = new ArrayList<>();
		String stepExpression = "";
		int numberOfPlayers = playersAndColors.size();

		if(numberOfPlayers == 1) {
			Integer randLogicId = ThreadLocalRandom.current().nextInt(1, nodesAssociations.size()+1);
			currentStepConfiguration.add(new NodeConfiguration(randLogicId, 0, playersAndColors.get(0)));
			return new Step(currentStepConfiguration, this.stepTimeout, randLogicId.toString());
		}

		List<Integer> list = IntStream.of(IntStream.rangeClosed(1, nodesAssociations.size()).
			toArray()).boxed().collect(Collectors.toList()); // lista desde 1 hasta la cantidad de nodos asociados
		Collections.shuffle(list); // desordena de manera random la lista
		int i = 0;
		for (Color color : playersAndColors) {
			// aca obtenemos uno que sabemos que va a ser unico y random gracias al shuffle
			Integer logicId = list.get(i++);
			currentStepConfiguration.add(new NodeConfiguration(logicId, 0, color));
			stepExpression = stepExpression.concat(logicId.toString().concat("&"));
		}
		return new Step(currentStepConfiguration, this.stepTimeout, stepExpression);
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
