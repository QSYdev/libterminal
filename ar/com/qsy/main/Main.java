package ar.com.qsy.main;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import ar.com.qsy.model.objects.MulticastReceiver;
import ar.com.qsy.model.objects.QSYPacket;
import ar.com.qsy.model.objects.ReceiverSelector;
import ar.com.qsy.model.objects.Terminal;

public final class Main {

	public static void main(final String[] args) throws IOException {

		final LinkedBlockingQueue<QSYPacket> buffer = new LinkedBlockingQueue<>();

		final ReceiverSelector receiverSelector = new ReceiverSelector(buffer);
		final Thread threadReceiveSelector = new Thread(receiverSelector, "Receive Selector");
		threadReceiveSelector.start();

		final Terminal terminal = new Terminal(buffer, receiverSelector);
		final Thread threadTerminal = new Thread(terminal, "Terminal");
		threadTerminal.start();
		terminal.searchNodes();

		final Thread threadMulticastReceiver = new Thread(new MulticastReceiver(QSYPacket.MULTICAST_ADDRESS, QSYPacket.MULTICAST_PORT, buffer), "Multicast Receiver");
		threadMulticastReceiver.start();

	}

}
