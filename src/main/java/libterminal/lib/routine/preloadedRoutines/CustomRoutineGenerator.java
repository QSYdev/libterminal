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
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));
			steps.add(new Step(nodesConfig, 2000L, "1", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, Color.BLUE));
			steps.add(new Step(nodesConfig, 2000L, "1", false));
		}
		final Routine routine = new Routine((byte) 1, (byte) 1, 0L, steps);

		RoutineManager.storeRoutine(PATH + "routine1.json", routine);
	}

}
