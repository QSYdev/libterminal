package unit.utils;

import libterminal.lib.routine.Routine;
import libterminal.utils.RoutineManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(JUnitPlatform.class)
public class RoutineManagerTest {
	private static final String FACTORY_TEST_ROUTINE_LOAD_PATH = "src/test/java/factory/routine.json";
	private static final String UNEXISTENT_ROUTINE = "factory/ASASADS";
	private static final String FACTORY_TEST_ROUTINE_STORE_PATH = "src/test/java/factory/routine-manager-test.json";

	private final String UNEXPECTED_NULL = "La rutina no deberia ser null";

	@ClassTest
	public void loadRoutine() {
		assertThrows(IOException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				RoutineManager.loadRoutine(UNEXISTENT_ROUTINE);
			}
		});
		assertNotNull(loadRoutineMethod(), UNEXPECTED_NULL);
	}

	@ClassTest
	public void storeRoutine() {
		try {
			RoutineManager.storeRoutine(FACTORY_TEST_ROUTINE_STORE_PATH, loadRoutineMethod());
		} catch(IOException e) {
			e.printStackTrace();
		}
		File file = new File(FACTORY_TEST_ROUTINE_STORE_PATH);
		assertEquals(true, file.exists());
	}

	private Routine loadRoutineMethod() {
		Routine routine = null;
		try {
			routine = RoutineManager.loadRoutine(FACTORY_TEST_ROUTINE_LOAD_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return routine;
	}

	@AfterAll
	public static void tearDown() {
		try {
			Files.deleteIfExists(Paths.get(UNEXISTENT_ROUTINE));
			Files.deleteIfExists(Paths.get(FACTORY_TEST_ROUTINE_STORE_PATH));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

}
