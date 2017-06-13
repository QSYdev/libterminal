package ar.com.qsy.main;

import java.util.concurrent.LinkedBlockingQueue;

import ar.com.qsy.model.objects.MulticastReceiver;
import ar.com.qsy.model.objects.ReceiverSelector;
import ar.com.qsy.model.objects.Terminal;
import ar.com.qsy.model.utils.QSYPacketTools;
import ar.com.qsy.model.utils.QSYPacketTools.QSYPacket;

public final class Main {

	public static void main(final String[] args) {

		final LinkedBlockingQueue<QSYPacket> buffer = new LinkedBlockingQueue<>();

		final ReceiverSelector receiverSelector = new ReceiverSelector(buffer);
		final Thread threadReceiveSelector = new Thread(receiverSelector, "Receive Selector");
		threadReceiveSelector.start();

		final Terminal terminal = new Terminal(buffer, receiverSelector);
		final Thread threadTerminal = new Thread(terminal, "Terminal");
		terminal.searchNodes();
		threadTerminal.start();

		final Thread threadMulticastReceiver = new Thread(new MulticastReceiver(QSYPacketTools.MULTICAST_ADDRESS, QSYPacketTools.MULTICAST_PORT, buffer), "Multicast Receiver");
		threadMulticastReceiver.start();

	}

}
