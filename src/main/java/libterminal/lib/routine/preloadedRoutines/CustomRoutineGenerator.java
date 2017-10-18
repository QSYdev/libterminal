package libterminal.lib.routine.preloadedRoutines;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import libterminal.lib.routine.Color;
import libterminal.lib.routine.NodeConfiguration;
import libterminal.lib.routine.Routine;
import libterminal.lib.routine.Step;
import libterminal.utils.RoutineManager;

public class CustomRoutineGenerator {

	private static final String PATH = "src/main/java/libterminal/resources/";

	public static void main(String[] args) throws IOException {

		final ArrayList<Step> steps = new ArrayList<>();
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));

			steps.add(new Step(nodesConfig, 0L, "1", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(2, 500, Color.RED));
			steps.add(new Step(nodesConfig, 0L, "2", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(3, 500, Color.MAGENTA));
			steps.add(new Step(nodesConfig, 0L, "3", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));
			nodesConfig.add(new NodeConfiguration(2, 500, Color.RED));
			nodesConfig.add(new NodeConfiguration(3, 500, Color.MAGENTA));
			steps.add(new Step(nodesConfig, 0L, "1&2&3", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));
			nodesConfig.add(new NodeConfiguration(2, 500, Color.RED));
			nodesConfig.add(new NodeConfiguration(3, 500, Color.MAGENTA));
			steps.add(new Step(nodesConfig, 0L, "1|2|3", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));
			nodesConfig.add(new NodeConfiguration(2, 500, Color.RED));
			nodesConfig.add(new NodeConfiguration(3, 500, Color.MAGENTA));
			steps.add(new Step(nodesConfig, 0L, "(1&2)|3", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));
			nodesConfig.add(new NodeConfiguration(2, 500, Color.RED));
			nodesConfig.add(new NodeConfiguration(3, 500, Color.MAGENTA));
			steps.add(new Step(nodesConfig, 0L, "(1|2)&3", false));
		}

		final Routine routine = new Routine((byte) 1, (byte) 3, 0L, steps, "Rutina de funcionalidades");
		RoutineManager.storeRoutine(PATH + "qsy.json", routine);
	}

}
