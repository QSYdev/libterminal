package ar.com.qsy.model.objects;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerExecutor extends Executor {
	private Timer timer;
	private StepTimeout stepTimeoutTask;

	// TODO: parametros para el constructor en caso de player
	public PlayerExecutor() {
		this.running = new AtomicBoolean(false);
		this.timer = new Timer("Step timeouts");
	}

	@Override
	public void start() {
		this.running.set(true);
		generateAndExecuteNextStep();
	}

	private void generateAndExecuteNextStep() {

	}

	@Override
	public void touche(Node node) {
		/*
		 * TODO: schedule de timer
		 * para la ejecucion del timer deberiamos usar schedule(TimerTask task, long delay), ya que
		 * ese ejecuta el task una sola vez. Ademas, una vez que nos llegan todos los touches necesarios
		 * para terminar el paso, tenemos que hacer timerTask.cancel() Y timer.purge(), ya que cancel lo
		 * pone en estado cancelado y purge limpia todos los cancelados.
		 */
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
			 * haya sido terminado, si no se termino deberiamos llevar a cabo la accion
			 * establecida. En rutinas player creo que simplemente se pasa al siguiente paso
			 */
		}
	}
}
