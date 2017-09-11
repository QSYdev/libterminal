package unit.utils;

import libterminal.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
public class UtilsStaticTest {
	private static final String UNEXPECTED_LENGTH = "La cantidad de numeros no es la correcta";
	private static final String UNEXPECTED_VALUE = "El entero identificador obtenido es incorrecto";

	@ClassTest
	public void fromInfixToPostfix() {
		final int[] result = {1, 2, Utils.AND_INT_VALUE, 3, 4, Utils.AND_INT_VALUE, Utils.OR_INT_VALUE};
		final int[] numbers = Utils.fromInfixToPostfix("(1&2)|(3&4)");
		assertAll("resultado", new Executable() {
			@Override
			public void execute() throws Throwable {
				assertEquals(7, numbers.length, UNEXPECTED_LENGTH);

				for (int i = 0; i < result.length; i++)
					assertEquals(result[i], numbers[i], UNEXPECTED_VALUE);

			}
		});
	}
}
