package unit.routine;

import ar.com.qsy.src.app.routine.Color;
import ar.com.qsy.src.app.routine.NodeConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitPlatform.class)
public class NodeConfigurationTest {
	private static NodeConfiguration nodeConfiguration;
	private static final int ID = 1;
	private static final long DELAY = 1000;
	private static final Color COLOR = new Color((byte) 0, (byte) 0, (byte) 0);

	@BeforeAll
	public static void setUp() {
		nodeConfiguration = new NodeConfiguration(ID, DELAY, COLOR);
	}

	@Test
	public void getId() {
		assertEquals(ID, nodeConfiguration.getId());
	}

	@Test
	public void getDelay() {
		assertEquals(DELAY, nodeConfiguration.getDelay());
	}

	@Test
	public void getColor() {
		assertEquals(COLOR, nodeConfiguration.getColor());
	}
}
