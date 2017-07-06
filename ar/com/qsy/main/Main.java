package ar.com.qsy.main;

import java.util.concurrent.LinkedBlockingQueue;

import ar.com.qsy.model.objects.MulticastReceiver;
import ar.com.qsy.model.objects.QSYPacket;
import ar.com.qsy.model.objects.ReceiverSelector;
import ar.com.qsy.model.objects.SenderSelector;
import ar.com.qsy.model.objects.Terminal;
import ar.com.qsy.view.QSYFrame;

public final class Main {

	public static void main(final String[] args) throws Exception {

		final QSYFrame view = new QSYFrame();

		final LinkedBlockingQueue<QSYPacket> inputBuffer = new LinkedBlockingQueue<>();
		final LinkedBlockingQueue<QSYPacket> outputBuffer = new LinkedBlockingQueue<>();

		final ReceiverSelector receiverSelector = new ReceiverSelector(inputBuffer);
		final Thread threadReceiveSelector = new Thread(receiverSelector, "Receive Selector");
		threadReceiveSelector.start();

		final Terminal terminal = new Terminal(inputBuffer, receiverSelector, outputBuffer, view);
		final Thread threadTerminal = new Thread(terminal, "Terminal");
		threadTerminal.start();

		final SenderSelector senderSelector = new SenderSelector(terminal.getNodes(), outputBuffer);
		final Thread threadSenderSelector = new Thread(senderSelector, "Sender Selector");
		threadSenderSelector.start();

		final MulticastReceiver multicastReceiver = new MulticastReceiver(QSYPacket.MULTICAST_ADDRESS, QSYPacket.MULTICAST_PORT, inputBuffer);
		final Thread threadMulticastReceiver = new Thread(multicastReceiver, "Multicast Receiver");
		threadMulticastReceiver.start();

		view.setTerminal(terminal);

	}

}
