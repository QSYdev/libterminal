package ar.com.qsy.src.test.unit.routine;

import ar.com.qsy.src.app.routine.Color;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(JUnitPlatform.class)
public class ColorTest {
	private final byte RED = 15;
	private final byte GREEN = 15;
	private final byte BLUE = 15;

	@Test
	public void colorTest() {
		Color color;
		assertThrows(IllegalArgumentException.class, () -> new Color((byte) 17, (byte) 0, (byte) 0));
		assertThrows(IllegalArgumentException.class, () -> new Color((byte) 0, (byte) 17, (byte) 0));
		assertThrows(IllegalArgumentException.class, () -> new Color((byte) 0, (byte) 0, (byte) 17));

		color = new Color(RED, GREEN, BLUE);
		assertEquals(RED, color.getRed());
		assertEquals(GREEN, color.getGreen());
		assertEquals(BLUE, color.getBlue());

		Color red = new Color(RED, (byte) 0, (byte) 0);
		Color blue = new Color((byte) 0, (byte) 0, BLUE);
		assertNotEquals(red, blue);
		assertEquals(new Color(RED, (byte) 0, (byte) 0), red);
	}
}