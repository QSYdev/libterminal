package ar.com.qsy.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import ar.com.qsy.model.objects.Color;
import ar.com.qsy.model.objects.NodeConfiguration;
import ar.com.qsy.model.objects.Routine;
import ar.com.qsy.model.objects.Step;
import ar.com.qsy.model.utils.RoutineManager;

public class Main {

	public static void main(String[] args) throws IOException {

		final ArrayList<Step> steps = new ArrayList<>();
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, new Color((byte) 0xF, (byte) 0xA, (byte) 0xC)));
			steps.add(new Step(nodesConfig, 0L, "1", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, new Color((byte) 0x3, (byte) 0x4, (byte) 0x5)));
			steps.add(new Step(nodesConfig, 2000L, "1", false));
		}
		{
			final LinkedList<NodeConfiguration> nodesConfig = new LinkedList<>();
			nodesConfig.add(new NodeConfiguration(1, 500, new Color((byte) 0x3, (byte) 0x4, (byte) 0x5)));
			steps.add(new Step(nodesConfig, 2000L, "1", false));
		}
		final Routine routine = new Routine((byte) 1, steps);

		RoutineManager.storeRoutine("ar/com/qsy/io/routine1.json", routine);
	}

}
