package ar.com.qsy.model.patterns.observer;

public final class Event {

	public static enum EventType {
		/**
		 * El evento, destaca un QSYPacket que se recibe por el multicast o por
		 * el ReceiverSelector. El mismo no se determina su tipo. En Content se
		 * encuentra el QSYPacket.
		 */
		IncomingQSYPacket,

		/**
		 * La terminal ha indentificado un nuevo nodo y lo ha agregado a su
		 * tabla nodes. En Content se encuentra la instancia del nodo creado.
		 */
		newNode,

		/**
		 * La terminal ha identificado un keepAliveReceived. En Content se
		 * encuentra el QSYPacket recibido.
		 */
		keepAliveReceived,

		/**
		 * La aplicacion ha enviado un command hacia el nodo. En Content se
		 * encuentra el QSYPacket.
		 */
		commandPacketSent,

		/**
		 * El modulo keepalive detecto que un nodo no ha enviado a tiempo su
		 * keepalive. En Content se encuentra la instancia del nodo
		 * desconectado.
		 */
		keepAliveError,

		/**
		 * La terminal determina que un nodo ha sido desconectado. En content se
		 * encuentra la instancia del nodo.
		 */
		disconectedNode
	}

	private final EventType eventType;
	private final Object content;

	public Event(final EventType eventType, final Object content) {
		this.eventType = eventType;
		this.content = content;
	}

	public final EventType getEventType() {
		return eventType;
	}

	public Object getContent() {
		return content;
	}

}
