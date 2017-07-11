package ar.com.qsy.model.objects;

import java.util.concurrent.atomic.AtomicBoolean;

// TODO: definir si la implementacion Runnable o la no runnable
public class CustomExecutor extends Executor {

	// TODO: parametros para el constructor en caso de custom
	public CustomExecutor() {
		this.running = new AtomicBoolean(true);
	}

	public void start() {
	}
}
