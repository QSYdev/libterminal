package ar.com.qsy.src.app.routine;

public final class Color {

	private static final byte MAX_AMOUNT_COLOR = 16;

	private final byte red;
	private final byte green;
	private final byte blue;

	public Color(final byte red, final byte green, final byte blue) {
		if (checkAmountOfColor(red) && checkAmountOfColor(green) && checkAmountOfColor(blue)) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		} else {
			throw new IllegalArgumentException("<< COLOR >> La cantidad de color ingresada debe ser un valor entre 0 y " + MAX_AMOUNT_COLOR + "exclusive");
		}
	}

	private boolean checkAmountOfColor(final byte color) {
		return color >= 0 && color < MAX_AMOUNT_COLOR;
	}

	public byte getRed() {
		return red;
	}

	public byte getGreen() {
		return green;
	}

	public byte getBlue() {
		return blue;
	}

	@Override
	public String toString() {
		return "RED = " + red + " || GREEN = " + green + " || BLUE = " + blue;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Color) {
			final Color c = (Color) obj;
			return red == c.red && green == c.green && blue == c.blue;
		} else {
			return false;
		}
	}

}
