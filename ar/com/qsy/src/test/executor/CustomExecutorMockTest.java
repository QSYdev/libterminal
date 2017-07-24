package ar.com.qsy.src.test.executor;

import ar.com.qsy.src.app.routine.NodeConfiguration;
import ar.com.qsy.src.app.routine.Routine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import ar.com.qsy.src.app.routine.Step;
import ar.com.qsy.src.utils.RoutineManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomExecutorMockTest {
	public CustomExecutorMock executor;
	public int NUMBER_OF_STEPS;
	public Routine routine;
	public TreeMap<Integer, Integer> nodesIdsAssociations;
	public boolean[] stepsStopOnTimeouts;
	public long[] stepsTimeouts;
	public String[] stepsExpressions;
	public ArrayList<LinkedList<NodeConfiguration>> stepsNodesConfigurations;

	@BeforeEach
	public void setUp() {
		try{
			routine = RoutineManager.loadRoutine("ar/com/qsy/src/test/factory/routine.json");
		} catch(Exception e) {
			e.printStackTrace();
		}
		NUMBER_OF_STEPS = routine.getSteps().size();
		stepsStopOnTimeouts = new boolean[NUMBER_OF_STEPS];
		stepsExpressions = new String[NUMBER_OF_STEPS];
		stepsTimeouts = new long[NUMBER_OF_STEPS];
		stepsNodesConfigurations = new ArrayList<>();
		Iterator rt = routine.iterator();
		for(int i=0; i < NUMBER_OF_STEPS; i++) {
			Step step = (Step) rt.next();
			stepsExpressions[i] = step.getExpression();
			stepsStopOnTimeouts[i] = step.getStopOnTimeout();
			stepsTimeouts[i] = step.getTimeOut();
			stepsNodesConfigurations.add(step.getNodesConfiguration());
		}
		nodesIdsAssociations = new TreeMap<>(); nodesIdsAssociations.put(1, 0); nodesIdsAssociations.put(2, 3);
		executor = new CustomExecutorMock(routine, nodesIdsAssociations);
		try {
			executor.start();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getNextStep() {
		for(int i = 1; i < NUMBER_OF_STEPS; i++) {
			Step step = executor.getNextStep();
			assertEquals(stepsExpressions[i], step.getExpression(), "Expresion de paso incorrecta");
			assertEquals(stepsStopOnTimeouts[i], step.getStopOnTimeout(), "Condicion de timeout incorrecta");
			assertEquals(stepsTimeouts[i], step.getTimeOut(), "Timeout de paso incorrecto");
			assertEquals(stepsNodesConfigurations.get(i), step.getNodesConfiguration(), "Configuracion de nodos incorrecta");
			for(NodeConfiguration nodeConfig : step.getNodesConfiguration()) {
				assertEquals(true, nodesIdsAssociations.containsKey(nodeConfig.getId()), "Configuracion de paso incorrecta");
			}
		}
	}

	@Test
	public void hasNextStep() {
		assertEquals(true, executor.hasNextStep(), "Una vez creado deberian haber pasos");
		for(int i = 0; i < NUMBER_OF_STEPS-1; i++)
			executor.getNextStep();
		assertEquals(false, executor.hasNextStep(), "No deberian quedar pasos despues de obtener todos");
	}
}