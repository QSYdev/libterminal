package ar.com.qsy.main;

import ar.com.qsy.model.objects.externalObjects.MulticastReceiver;
import ar.com.qsy.model.objects.externalObjects.ReceiverSelector;
import ar.com.qsy.model.objects.externalObjects.SenderSelector;
import ar.com.qsy.model.objects.protocol.QSYPacket;
import ar.com.qsy.model.objects.terminal.Terminal;
import ar.com.qsy.view.QSYFrame;

public final class Main {

	public static void main(final String[] args) throws Exception {

		final MulticastReceiver multicastReceiver = new MulticastReceiver(QSYPacket.MULTICAST_ADDRESS, QSYPacket.MULTICAST_PORT);
		final Terminal terminal = new Terminal();
		final ReceiverSelector receiverSelector = new ReceiverSelector();
		final SenderSelector senderSelector = new SenderSelector(terminal.getNodes());
		final QSYFrame view = new QSYFrame(terminal);

		multicastReceiver.addListener(terminal);
		receiverSelector.addListener(terminal);
		terminal.addListener(receiverSelector);
		terminal.addListener(senderSelector);
		terminal.addListener(view);

		final Thread threadReceiveSelector = new Thread(receiverSelector, "Receive Selector");
		threadReceiveSelector.start();

		final Thread threadTerminal = new Thread(terminal, "Terminal");
		threadTerminal.start();

		final Thread threadSenderSelector = new Thread(senderSelector, "Sender Selector");
		threadSenderSelector.start();

		final Thread threadMulticastReceiver = new Thread(multicastReceiver, "Multicast Receiver");
		threadMulticastReceiver.start();

	}

}
