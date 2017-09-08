package unit.node;

import libterminal.lib.keepalive.KeepAlive;
import libterminal.lib.node.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnitPlatform.class)
public class NodeTest {
	private Node node;

	@BeforeEach
	public void setUp() {
		node = mock(Node.class);

		when(node.getNodeId()).thenReturn(1);

		doCallRealMethod().when(node).keepAlive(anyLong());
		doCallRealMethod().when(node).isAlive(anyLong());
	}

	@ClassTest
	public void keepAliveInteractions() {
		node.keepAlive(System.currentTimeMillis());
		assertEquals(true, node.isAlive(System.currentTimeMillis()), "El nodo deberia estar vivo");
		sleep(KeepAlive.MAX_KEEP_ALIVE_DELAY);
		assertEquals(false, node.isAlive(System.currentTimeMillis()), "El nodo no deberia estar vivo");
	}

	@ClassTest
	public void compareTo() {
		Node newNode = mock(Node.class);
		when(newNode.getNodeId()).thenReturn(3);
		assertEquals(1, node.compareTo(newNode));
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}
