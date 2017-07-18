package ar.com.qsy.model.objects;

public final class Color {

	private final short red;
	private final short green;
	private final short blue;

	public Color(final short red, final short green, final short blue) {
		if (checkAmountOfColor(red) && checkAmountOfColor(green) && checkAmountOfColor(blue)) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		} else {
			throw new IllegalArgumentException("<< COLOR >> La cantidad de color ingresada debe ser un valor entre 0 y 255");
		}
	}

	private boolean checkAmountOfColor(final short color) {
		return color >= 0 && color < 256;
	}

	public short getRed() {
		return red;
	}

	public short getGreen() {
		return green;
	}

	public short getBlue() {
		return blue;
	}

	@Override
	public String toString() {
		return "RED = " + red + " || GREEN = " + green + " || BLUE = " + blue;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Color) {
			final Color c = (Color) obj;
			return red == c.red && green == c.green && blue == c.blue;
		} else {
			return false;
		}
	}

}
