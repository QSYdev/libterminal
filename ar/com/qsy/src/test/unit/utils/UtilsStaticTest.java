package ar.com.qsy.src.test.unit.utils;

import ar.com.qsy.src.utils.Utils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsStaticTest {
	private static final String UNEXPECTED_LENGTH = "La cantidad de numeros no es la correcta";
	private static final String UNEXPECTED_VALUE = "El entero identificador obtenido es incorrecto";

	@Test
	public void fromInfixToPostfix() {
		int[] result = {1, 2, Utils.AND_INT_VALUE, 3, 4, Utils.AND_INT_VALUE, Utils.OR_INT_VALUE};
		int[] numbers = Utils.fromInfixToPostfix("(1&2)|(3&4)");
		assertAll("resultado",
			() -> {
				assertEquals(7, numbers.length, UNEXPECTED_LENGTH);

				for (int i = 0; i < result.length; i++)
					assertEquals(result[i], numbers[i], UNEXPECTED_VALUE);
			}
		);
	}
}
