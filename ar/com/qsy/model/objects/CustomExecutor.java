package ar.com.qsy.model.objects;

import ar.com.qsy.model.patterns.observer.Event;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static ar.com.qsy.model.patterns.observer.Event.EventType.executorDoneExecuting;

public class CustomExecutor extends Executor {
	private Routine routine;
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

	@Override
	public void touche(Node node) {
		super.touche(node);
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
	@Override
	protected void executeNextStep() {
		turnOffCurrentStep();
		if (!running.get()) {
			return;
		}
		stepTimeoutTask.cancel();
		timer.purge();
		touchedNodes = new HashSet<>();
		currentStep = routine.next();
		super.executeNextStep();
	}
}
