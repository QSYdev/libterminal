package ar.com.qsy.src.app.network;

import ar.com.qsy.src.app.protocol.QSYPacket;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.Event.EventType;
import ar.com.qsy.src.patterns.observer.EventSource;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MulticastReceiver extends EventSource implements Runnable {

	private final InetAddress multicastGroupAddress;
	private final DatagramChannel channel;
	private final ByteBuffer packet;

	private final AtomicBoolean running;

	public MulticastReceiver(InetSocketAddress localAddress, String group) throws IOException, UnknownHostException {
		this.multicastGroupAddress = InetAddress.getByName(group);
		this.channel = DatagramChannel.open(StandardProtocolFamily.INET);
		this.channel.bind(localAddress);
		this.channel.join(multicastGroupAddress, NetworkInterface.getByInetAddress(localAddress.getAddress()));

		this.packet = ByteBuffer.allocate(QSYPacket.PACKET_SIZE);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {
			InetSocketAddress sender;
			try {
				sender = (InetSocketAddress) this.channel.receive(packet);
				sendEvent(new Event(EventType.incomingQSYPacket, new QSYPacket(sender.getAddress(), packet.array())));
			} catch (ClosedByInterruptException e) {
				try {
					this.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		channel.close();
		super.close();
	}

}
