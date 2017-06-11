package ar.com.qsy;

public final class Main {

	public static void main(final String[] args) {

		final Buffer<QSYPacket> buffer = new Buffer<>();

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
