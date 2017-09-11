package unit.routine;


import libterminal.lib.routine.Color;
import libterminal.lib.routine.NodeConfiguration;
import libterminal.lib.routine.Routine;
import libterminal.lib.routine.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class RoutineTest {
	private ArrayList<Step> steps;
	private Routine routine;

	@BeforeEach
	public void setUp() {
		steps = new ArrayList<>();
		{
			LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, new Color((byte) 0xF, (byte) 0xA, (byte) 0xC)));
			steps.add(new Step(nodesConfig, 0L, "1", false));
		}
		{
			LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, new Color((byte) 0x3, (byte) 0x4, (byte) 0x5)));
			steps.add(new Step(nodesConfig, 2000L, "1", false));
		}
		{
			LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, new Color((byte) 0x3, (byte) 0x4, (byte) 0x5)));
			steps.add(new Step(nodesConfig, 2000L, "1", false));
		}
		routine = new Routine((byte)1, steps);
	}

	@ClassTest
	public void routineTest() {
		assertEquals(1, routine.getNumberOfNodes());
		assertEquals(steps, routine.getSteps());
	}

	@ClassTest
	public void getIterator() {
		final Iterator iterator = routine.iterator();
		assertEquals(true, iterator.hasNext());
		assertNotNull(iterator.next());
		iterator.next(); iterator.next();
		assertEquals(false, iterator.hasNext());
		assertThrows(IndexOutOfBoundsException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				iterator.next();
			}
		});
	}
}
