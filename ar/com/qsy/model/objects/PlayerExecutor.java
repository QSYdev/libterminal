package ar.com.qsy.model.objects;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ar.com.qsy.model.patterns.observer.Event;

import static ar.com.qsy.model.patterns.observer.Event.EventType.executorDoneExecuting;

public class PlayerExecutor extends Executor {
	private RoutineTimeoutTask routineTimeoutTask;
	private ArrayList<Color> playersAndColors;
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
