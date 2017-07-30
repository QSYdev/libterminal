package ar.com.qsy.src.test.unit.keepalive;

import ar.com.qsy.src.app.keepalive.KeepAlive;
import ar.com.qsy.src.app.protocol.QSYPacket;
import ar.com.qsy.src.app.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitPlatform.class)
public class KeepAliveTest {
	private Node node;
	private Node newNode;
	private QSYPacket qsyPacket;

	private KeepAliveRunner runner;
	private KeepAlive keepAlive;
	private TreeMap<Integer, Node> nodes;

	@BeforeEach
	public void setUp() {
		qsyPacket = mock(QSYPacket.class);
		node = mock(Node.class);
		newNode = mock(Node.class);

		when(qsyPacket.getId()).thenReturn(1);
		when(node.getNodeId()).thenReturn(1);
		when(newNode.getNodeId()).thenReturn(2);

		doCallRealMethod().when(node).keepAlive(anyLong());
		doCallRealMethod().when(node).isAlive(anyLong());
		doCallRealMethod().when(newNode).keepAlive(anyLong());
		doCallRealMethod().when(newNode).isAlive(anyLong());

		nodes = new TreeMap<>();
		nodes.put(1, node);
	}

	@Test
	public void qsyKeepAlivePacketReceived() {
		runner = new KeepAliveRunner();
		keepAlive = new KeepAlive(nodes);
		keepAlive.addListener(runner);

		keepAlive.qsyKeepAlivePacketReceived(qsyPacket);
		sleep(KeepAlive.MAX_KEEP_ALIVE_DELAY/2);
		assertEquals(0, runner.getKeepAliveErrorCountNode1());

		keepAlive.qsyKeepAlivePacketReceived(qsyPacket);
		sleep(KeepAlive.MAX_KEEP_ALIVE_DELAY*2);
		assertNotEquals(0, runner.getKeepAliveErrorCountNode1());
	}

	@Test
	public void newNodeCreated() {
		runner = new KeepAliveRunner();
		keepAlive = new KeepAlive(nodes);
		keepAlive.addListener(runner);

		nodes.put(2, newNode);
		keepAlive.newNodeCreated(newNode);
		assertEquals(0, runner.getKeepAliveErrorCountNode2());
		sleep(KeepAlive.MAX_KEEP_ALIVE_DELAY*4);
		assertNotEquals(0, runner.getKeepAliveErrorCountNode2());
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
