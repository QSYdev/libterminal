package ar.com.qsy.model.objects;

import java.util.concurrent.atomic.AtomicBoolean;

// TODO: definir si la implementacion Runnable o la no runnable
public class PlayerExecutor extends Executor {

	// TODO: parametros para el constructor en caso de player
	public PlayerExecutor() {
		this.running = new AtomicBoolean(true);
	}

	public void start() {
	}
}
