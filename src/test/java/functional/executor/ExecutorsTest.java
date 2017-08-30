package functional.executor;

import libterminal.lib.executor.CustomExecutor;
import libterminal.lib.executor.Executor;
import libterminal.lib.routine.NodeConfiguration;
import libterminal.lib.routine.Routine;
import libterminal.lib.routine.Step;
import libterminal.utils.RoutineManager;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExecutorsTest {
	public Executor executor;
	public ExecutorRunner runner;
	public static TreeMap<Integer, Integer> nodesIdsAssociations;

	@BeforeAll
	public static void setUpAll() {
		nodesIdsAssociations = new TreeMap<>(); nodesIdsAssociations.put(1, 0); nodesIdsAssociations.put(2, 3);
	}

	public void execution() {
		Routine routine = setupCustom();
		ArrayList<Step> steps = routine.getSteps();
		executor = new CustomExecutor(routine, nodesIdsAssociations);
		runner = new ExecutorRunner(executor);
		executor.addListener(runner);

		executor.start();
		for(NodeConfiguration nodeConfig : steps.get(0).getNodesConfiguration()) {
			touche(nodesIdsAssociations.get(nodeConfig.getId()));
		}
		assertEquals(true, executor.isRunning());
		sleep(steps.get(1).getTimeOut()+1000);
		assertEquals(false, executor.isRunning(), "Deberia haber terminado de ejecutar");

		assertEquals(steps.get(0).getNodesConfiguration().size()+steps.get(1).getNodesConfiguration().size()*2,
			runner.getNumberOfRequestedCommands());
		assertEquals(1, runner.getNumberOfTimeouts());
		assertEquals(1, runner.getNumberOfDoneExecuting());
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void touche(int nodeId) {
		try {
			executor.touche(nodeId);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Routine setupCustom() {
		Routine routine = null;
		try {
			routine = RoutineManager.loadRoutine("src/test/java/factory/routine.json");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return routine;
	}
}
