package ar.com.qsy.model.objects;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerExecutor extends Executor {
	private Timer timer;

	// TODO: parametros para el constructor en caso de player
	public PlayerExecutor() {
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void start() {
		/*
		 * TODO: implementacion inicial de la rutina
		 * en start deberiamos obtener el primer paso de la rutina, crear el StepTimeout acorde al paso
		 * y mandarle el evento commandPacketSent a la terminal, indicando los nodos del primer paso
		 */
	}

	@Override
	public void touche(Node node) {
		/*
		 * TODO: schedule de timer
		 * para la ejecucion del timer deberiamos usar schedule(TimerTask task, long delay), ya que
		 * ese ejecuta el task una sola vez. Ademas, una vez que nos llegan todos los touches necesarios
		 * para terminar el paso, tenemos que hacer timer.cancel() Y timer.purge(), ya que cancel deja
		 * al timerTask dando vueltas, con purge lo limpias.
		 */
	}

	@Override
	public void stop() {
		super.stop();
		timer.cancel();
		timer.purge();
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
