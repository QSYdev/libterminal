package ar.com.qsy.model.objects;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomExecutor extends Executor {

	// TODO: parametros para el constructor en caso de custom
	public CustomExecutor() {
		this.running = new AtomicBoolean(true);
	}

	public void start() {
	}

	public void touche(Node node) {}
}
