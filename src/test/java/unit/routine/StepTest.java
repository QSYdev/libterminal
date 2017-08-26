package unit.routine;

import ar.com.qsy.src.app.routine.Color;
import ar.com.qsy.src.app.routine.NodeConfiguration;
import ar.com.qsy.src.app.routine.Step;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class StepTest {
	private static Step step;
	private static LinkedList<NodeConfiguration> nodesConfiguration;
	private static final long TIMEOUT = 1000;
	private static final String EXPRESSION = "1" ;
	private static final boolean STOP_ON_TIMEOUT=false;

	@BeforeAll
	public static void setUp() {
		nodesConfiguration = new LinkedList<>();
		nodesConfiguration.add(new NodeConfiguration(1, 500, new Color((byte) 0xF, (byte) 0xA, (byte) 0xC)));
		step = new Step(nodesConfiguration, TIMEOUT, EXPRESSION, STOP_ON_TIMEOUT);
	}

	@Test
	public void getTimeout() {
		assertEquals(TIMEOUT, step.getTimeOut());
	}

	@Test
	public void getExpression() {
		assertEquals(EXPRESSION, step.getExpression());
	}

	@Test
	public void getStopOnTimeout() {
		assertEquals(STOP_ON_TIMEOUT, step.getStopOnTimeout());
	}

	@Test
	public void getNodesConfiguration() {
		assertEquals(nodesConfiguration, step.getNodesConfiguration());
	}

}
