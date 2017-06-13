package ar.com.qsy.model.patterns.observer.terminalEventArgs;

import ar.com.qsy.model.patterns.observer.EventArgs;

public final class TerminalEventArgs extends EventArgs {

	/**
	 * En args[0] se encuentra la instancia de la terminal.<br>
	 * En args[1] se encuentra la instancia del QSYHelloPacket recibido.<br>
	 */
	public static final int NEW_NODE_CONNECTION_EVENT = 0;

	/**
	 * En args[0] se encuentra la instancia de la terminal.<br>
	 * En args[1] se encuentra la instancia del QSYKeepAlivePacket recibido.<br>
	 */
	public static final int KEEP_ALIVE_PACKET_EVENT = 1;

	public TerminalEventArgs(final int event, final Object[] args) {
		super(event, args);
	}

}
