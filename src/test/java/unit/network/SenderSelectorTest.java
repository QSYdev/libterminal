package unit.network;

import libterminal.lib.network.SenderSelector;
import libterminal.lib.node.Node;
import libterminal.lib.protocol.QSYPacket;
import libterminal.patterns.observer.Event;
import libterminal.patterns.observer.EventSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

@RunWith(JUnitPlatform.class)
public class SenderSelectorTest {
	private static final byte Q_INDEX = 0x00;
	private static final byte S_INDEX = 0x01;
	private static final byte Y_INDEX = 0x02;

	private TreeMap<Integer, Node> nodes;
	private QSYPacket qsyPacket;
	private Node node;
	private SocketChannel nodeSocketChannel;
	private byte[] qsyData;

	@BeforeEach
	public void setUp() {
		qsyData = new byte[QSYPacket.PACKET_SIZE];
		qsyData[Q_INDEX] = 'Q'; qsyData[S_INDEX] = 'S'; qsyData[Y_INDEX] = 'Y';
		qsyPacket = mock(QSYPacket.class);
		node = mock(Node.class);
		nodeSocketChannel = mock(SocketChannel.class);
		when(qsyPacket.getId()).thenReturn(1);
		when(qsyPacket.getRawData()).thenReturn(qsyData);
		when(node.getNodeId()).thenReturn(1);
		when(node.getNodeSocketChannel()).thenReturn(nodeSocketChannel);
		nodes = new TreeMap<>();
		nodes.put(1, node);
	}

	@Test
	public void run() {
		SenderSelector sender = new SenderSelector(nodes);
		Thread senderThread = new Thread(sender, "Sender thread");
		SenderSelectorRunner senderSelectorRunner = new SenderSelectorRunner();
		senderSelectorRunner.addListener(sender);
		senderThread.start();
		senderSelectorRunner.sendEvent(new Event(Event.EventType.commandPacketSent, qsyPacket));
		sleep(200);
		synchronized (nodes) {
			verify(nodes.get(1), times(1)).getNodeSocketChannel();
		}
		verify(qsyPacket, times(1)).getRawData();
		try {
			verify(nodeSocketChannel, times(1)).write(any(ByteBuffer.class));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class SenderSelectorRunner extends EventSource {
	}
}
