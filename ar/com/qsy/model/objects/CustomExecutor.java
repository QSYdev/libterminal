package ar.com.qsy.model.objects;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomExecutor extends Executor {
	private Timer timer;
	private StepTimeout stepTimeoutTask;

	// TODO: parametros para el constructor en caso de custom
	public CustomExecutor() {
		this.running = new AtomicBoolean(true);
		this.timer = new Timer("Step timeouts");
	}

	@Override
	public void start() {
	}

	@Override
	public void touche(Node node) {
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
