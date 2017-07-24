package ar.com.qsy.src.test.executor;

import ar.com.qsy.src.app.routine.Color;
import ar.com.qsy.src.app.routine.NodeConfiguration;
import ar.com.qsy.src.app.routine.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.TreeMap;

class PlayerExecutorMockTest {
	public PlayerExecutorMock executor;
	public ArrayList<Color> playersAndColors;
	public Color color;
	public TreeMap<Integer, Integer> idsAssociations;
	public boolean stopOnTimeout;
	public boolean waitForAllPlayers;
	public long timeout;
	public long delay;
	public long maxExecTime;
	public int totalStep;

	@BeforeEach
	public void setUp() {
		idsAssociations = new TreeMap<>();
		idsAssociations.put(1, 0); idsAssociations.put(2, 4);
		color = new Color((byte) 0xF, (byte) 0, (byte) 0);
		playersAndColors = new ArrayList<>(); playersAndColors.add(color);
		waitForAllPlayers = true; timeout = 1000; delay = 0; maxExecTime = -1; totalStep = 4; stopOnTimeout = false;

		executor = new PlayerExecutorMock(idsAssociations, idsAssociations.size(), playersAndColors,
			waitForAllPlayers, timeout, delay, maxExecTime, totalStep, stopOnTimeout);
		try {
			executor.start();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void getNextStep() {
		Step step = executor.getNextStep();
		assertNotNull(step, "Paso no deberia ser null");
		assertEquals(stopOnTimeout, step.getStopOnTimeout(), "Configuracion de stopOnTimeout incorrecta");
		assertEquals(timeout, step.getTimeOut(), "Configuracion de timeout incorrecta");
		assertNotEquals("", step.getExpression(), "Expresion de paso incorrecta");
		for(NodeConfiguration nodeConfig : step.getNodesConfiguration()) {
			assertEquals(true, idsAssociations.containsKey(nodeConfig.getId()), "Configuracion de paso incorrecta");
			assertEquals(delay, nodeConfig.getDelay(), "Configuracion de delay incorrecta");
			assertEquals(color, nodeConfig.getColor(), "Configuracion de color incorrecta");
		}
	}

	@Test
	public void hasNextStep() {
		assertEquals(true, executor.hasNextStep(), "Una vez creado deberian haber pasos");
		for(int i = 0; i < totalStep-1; i++)
			executor.getNextStep();
		assertEquals(false, executor.hasNextStep(), "No deberian quedar pasos despues de obtener todos");
	}

}