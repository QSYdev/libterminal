package ar.com.qsy.src.test.unit.executor;

import ar.com.qsy.src.app.routine.NodeConfiguration;
import ar.com.qsy.src.app.routine.Routine;

import java.util.ArrayList;
import java.util.TreeMap;

import ar.com.qsy.src.app.routine.Step;
import ar.com.qsy.src.utils.RoutineManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(JUnitPlatform.class)
public class CustomExecutorMockTest {
	public CustomExecutorMock executor;
	public int NUMBER_OF_STEPS;
	public Routine routine;
	public TreeMap<Integer, Integer> nodesIdsAssociations;
	public ArrayList<Step> steps;

	@BeforeEach
	public void setUp() {
		try{
			routine = RoutineManager.loadRoutine("ar/com/qsy/src/test/factory/routine.json");
		} catch(Exception e) {
			e.printStackTrace();
		}
		steps = routine.getSteps();
		NUMBER_OF_STEPS = steps.size();
		nodesIdsAssociations = new TreeMap<>(); nodesIdsAssociations.put(1, 0); nodesIdsAssociations.put(2, 3);
		executor = new CustomExecutorMock(routine, nodesIdsAssociations);
		executor.start();
	}

	@Test
	public void getNextStep() {
		for(int i = 1; i < NUMBER_OF_STEPS; i++) {
			Step step = executor.getNextStep();
			assertEquals(steps.get(i).getExpression(), step.getExpression(), "Expresion de paso incorrecta");
			assertEquals(steps.get(i).getStopOnTimeout(), step.getStopOnTimeout(), "Condicion de timeout incorrecta");
			assertEquals(steps.get(i).getTimeOut(), step.getTimeOut(), "Timeout de paso incorrecto");
			assertEquals(steps.get(i).getNodesConfiguration(), step.getNodesConfiguration(), "Configuracion de nodos incorrecta");
			for(NodeConfiguration nodeConfig : step.getNodesConfiguration()) {
				assertEquals(true, nodesIdsAssociations.containsKey(nodeConfig.getId()), "ID de nodo incorrecto");
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