package libterminal.patterns.observer;

import libterminal.lib.node.Node;
import libterminal.lib.protocol.QSYPacket;
import libterminal.lib.results.Results;
import libterminal.lib.routine.Color;

public abstract class Event {

	public Event() {
	}

	public static abstract class ExternalEvent extends Event {

		public ExternalEvent() {
		}

	}

	public static abstract class InternalEvent extends Event {

		public InternalEvent() {
		}

	}

	/**
	 * El evento, destaca un QSYPacket que se recibe por el multicast o por el
	 * ReceiverSelector. El mismo no se determina su tipo. En Content se encuentra
	 * el QSYPacket. Listeners: - <b>Terminal</b>: chequea el tipo del qsy packet y
	 * hace lo correspondiente a cada caso. Senders: - <b>ReceiverSelector</b>:
	 * cuando llega un packet por el selector. - <b>MulticastReveiver</b>: cuando
	 * llega un packet por multicast.
	 */
	public static final class IncomingPacketEvent extends InternalEvent {

		private final QSYPacket packet;

		public IncomingPacketEvent(final QSYPacket packet) {
			this.packet = packet;
		}

		public QSYPacket getPacket() {
			return packet;
		}

	}

	/**
	 * La terminal ha indentificado un nuevo nodo y lo ha agregado a su tabla nodes.
	 * En Content se encuentra la instancia del nodo creado. Listeners: -
	 * <b>KeepAlive</b>: actualiza el keep alive del nodo correspondiente. -
	 * <b>ReceiverSelector</b>: agrega la conexion del nuevo nodo y despierta al
	 * selector. Senders: - <b>Terminal</b>: envia el evento cuando llega un
	 * QSYPacket de tipo Hello.
	 */
	public static final class NewNodeEvent extends InternalEvent {

		private final Node node;

		public NewNodeEvent(final Node node) {
			this.node = node;
		}

		public Node getNode() {
			return node;
		}

	}

	/**
	 * La aplicacion ha enviado un command hacia el nodo. En Content se encuentra el
	 * QSYPacket. Listeners: - <b>Sender</b>: cuando se quiere enviar un qsy command
	 * a un nodo. Senders: - <b>Terminal</b>: cuando se quiere enviar un comando a
	 * algun nodo en particular.
	 */
	public static final class CommandPacketSentEvent extends InternalEvent {

		private final QSYPacket packet;

		public CommandPacketSentEvent(final QSYPacket packet) {
			this.packet = packet;
		}

		public QSYPacket getPacket() {
			return packet;
		}

	}

	/**
	 * El modulo keepalive detecto que un nodo no ha enviado a tiempo su keepalive.
	 * En Content se encuentra la instancia del nodo desconectado. Listeners: -
	 * <b>Terminal</b>: elimina el nodo y cierra la conexion enviando
	 * disconnectedNode. Senders: - <b>KeepAlive</b>: cuando el nodo ya no esta
	 * vivo, es decir pasa el tiempo limite. - <b>KeepAlive::DeadNodesPurger</b>:
	 * cuando se identifica un nodo no esta vivo.
	 */
	public static final class KeepAliveErrorEvent extends InternalEvent {

		private final Node node;

		public KeepAliveErrorEvent(final Node node) {
			this.node = node;
		}

		public Node getNode() {
			return node;
		}

	}

	/**
	 * La terminal determina que un nodo ha sido desconectado. En content se
	 * encuentra la instancia del nodo. Listeners: - <b>ReceiverSelector</b>:
	 * cancela la conexion del nodo que se desconecto. Senders: - <b>Terminal</b>:
	 * cuando le llega un keepAliveError.
	 */
	public static final class DisconnectedNodeEvent extends InternalEvent {

		private final Node node;

		public DisconnectedNodeEvent(final Node node) {
			this.node = node;
		}

		public Node getNode() {
			return node;
		}

	}

	/**
	 * El functional.executor termino de ejecutar la rutina, esto se puede dar
	 * porque se terminaron todos los pasos o porque se cumplio el tiempo de la
	 * rutina. TODO: en Content que vamos a poner? Listeners: - <b>Terminal</b>:
	 * frena el functional.executor lo setea a null y le avisa al cliente que la
	 * ejecucion termino Senders: - <b>Executor</b>: cuando la ejecucion de la
	 * rutina actual termina sin ser cortada por el usuario
	 */
	public static final class ExecutorDoneExecutingEvent extends InternalEvent {

		public ExecutorDoneExecutingEvent() {
		}

	}

	/**
	 * El Executor avisa que se cumplio la cantidad de tiempo maxima establecida
	 * para este paso. En content va null por ahora. Listeners: - <b>Terminal</b>:
	 * llama a ejecutar el proximo paso de la rutina Senders: - <b>Executor</b>:
	 * cuando no se tocaron los nodos que se debian tocar en cierta cantidad de
	 * tiempo
	 */
	public static final class ExecutorStepTimeOutEvent extends InternalEvent {

		public ExecutorStepTimeOutEvent() {
		}

	}

	/**
	 * Evento que envia el functional.executor a terminal para notificar que existe
	 * un nuevo command a ser enviado hacia el sender. En content se encuentran los
	 * parametros de ese command. La terminal es la encargada de crear el paquete y
	 * enviarlo.
	 */
	public static final class CommandRequestEvent extends InternalEvent {

		private final int physicalId;
		private final long delay;
		private final Color color;
		private final int numberOfStep;

		public CommandRequestEvent(final int physicalId, final long delay, final Color color, final int numberOfStep) {
			this.physicalId = physicalId;
			this.delay = delay;
			this.color = color;
			this.numberOfStep = numberOfStep;
		}

		public int getPhysicalId() {
			return physicalId;
		}

		public long getDelay() {
			return delay;
		}

		public Color getColor() {
			return color;
		}

		public int getNumberOfStep() {
			return numberOfStep;
		}

	}

	/**
	 * Evento que envia la terminal hacia fuera de la API para indicar que la rutina
	 * a iniciado
	 */
	public static final class RoutineStartedEvent extends ExternalEvent {

		public RoutineStartedEvent() {
		}

	}

	/**
	 * Indica que se ha enviado un command. En content se encuentra un array de
	 * Objects que tienen los siguientes elementos. Número de nodo, color y delay
	 * (en ese orden).
	 */
	public static final class CommandIssuedEvent extends ExternalEvent {

		private final int physicalId;
		private final Color color;
		private final long delay;

		public CommandIssuedEvent(final int physicalId, final Color color, final long delay) {
			this.physicalId = physicalId;
			this.color = color;
			this.delay = delay;
		}

		public int getPhysicalId() {
			return physicalId;
		}

		public Color getColor() {
			return color;
		}

		public long getDelay() {
			return delay;
		}

	}

	/**
	 * Indica que se ha recibido un touche. En content se encuentra el numero de
	 * nodo.
	 */
	public static final class ToucheReceivedEvent extends ExternalEvent {

		private final int physicalId;

		public ToucheReceivedEvent(final int physicialId) {
			this.physicalId = physicialId;
		}

		public int getPhysicalId() {
			return physicalId;
		}
	}

	/**
	 * La terminal envia este evento hacia afuera, para avisar a la vista, que la
	 * rutina ha finalizado. En Content se pueden extraer los resultados para luego
	 * ser guardados en caso de que se desee.
	 */
	public static final class RoutineFinishedEvent extends ExternalEvent {

		private final Results results;

		public RoutineFinishedEvent(final Results results) {
			this.results = results;
		}

		public Results getResults() {
			return results;
		}
	}

}
