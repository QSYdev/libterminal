package ar.com.qsy.model.patterns.observer;

public final class Event {

        public static enum EventType {
                /**
                 * El evento, destaca un QSYPacket que se recibe por el multicast o por
                 * el ReceiverSelector. El mismo no se determina su tipo. En Content se
                 * encuentra el QSYPacket.
                 * Listeners:
                 * - <b>Terminal</b>: chequea el tipo del qsy packet y hace lo correspondiente
                 * a cada caso.
                 * Senders:
                 * - <b>ReceiverSelector</b>: cuando llega un packet por el selector.
                 * - <b>MulticastReveiver</b>: cuando llega un packet por multicast.
                 */
                incomingQSYPacket,

                /**
                 * La terminal ha indentificado un nuevo nodo y lo ha agregado a su
                 * tabla nodes. En Content se encuentra la instancia del nodo creado.
                 * Listeners:
                 * - <b>KeepAlive</b>: actualiza el keep alive del nodo correspondiente.
                 * - <b>ReceiverSelector</b>: agrega la conexion del nuevo nodo y despierta
                 * al selector.
                 * Senders:
                 * - <b>Terminal</b>: envia el evento cuando llega un QSYPacket de tipo Hello.
                 */
                newNode,

                /**
                 * La terminal ha identificado un keepAliveReceived. En Content se
                 * encuentra el QSYPacket recibido.
                 * Listeners:</b>
                 * - <b>KeepAlive: actualiza el keepAlive del nodo correspondiente.
                 * Senders:
                 * - <b>Terminal</b>: cuando el incomingQSYPacket es de tipo keepAlive.
                 */
                keepAliveReceived,

                /**
                 * La aplicacion ha enviado un command hacia el nodo. En Content se
                 * encuentra el QSYPacket.
                 * Listeners:
                 * - <b>SenderSelector</b>: cuando se quiere enviar un qsy command a un nodo.
                 * Senders:
                 * - <b>Terminal</b>: cuando se quiere enviar un comando a algun nodo en particular.
                 */
                commandPacketSent,

                /**
                 * El modulo keepalive detecto que un nodo no ha enviado a tiempo su
                 * keepalive. En Content se encuentra la instancia del nodo
                 * desconectado.
                 * Listeners:
                 * - <b>Terminal</b>: elimina el nodo y cierra la conexion enviando disconnectedNode.
                 * Senders:
                 * - <b>KeepAlive</b>: cuando el nodo ya no esta vivo, es decir pasa el tiempo limite.
                 * - <b>KeepAlive::DeadNodesPurger</b>: cuando se identifica un nodo no esta vivo.
                 */
                keepAliveError,

                /**
                 * La terminal determina que un nodo ha sido desconectado. En content se
                 * encuentra la instancia del nodo.
                 * Listeners:
                 * - <b>ReceiverSelector</b>: cancela la conexion del nodo que se desconecto.
                 * Senders:
                 * - <b>Terminal</b>: cuando le llega un keepAliveError.
                 */
                disconnectedNode
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
