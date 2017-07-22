package ar.com.qsy.model.objects;

public final class Color {

	private static final byte MAX_AMOUNT_COLOR = 15;

	private final byte redAmount;
	private final byte greenAmount;
	private final byte blueAmount;

	public Color(final byte redAmount, final byte greenAmount, final byte blueAmount) {
		if (checkAmountOfColor(redAmount) && checkAmountOfColor(greenAmount) && checkAmountOfColor(blueAmount)) {
			this.redAmount = redAmount;
			this.greenAmount = greenAmount;
			this.blueAmount = blueAmount;
		} else {
			throw new IllegalArgumentException("<< COLOR >> La cantidad de color ingresada debe ser un valor entre 0 y " + MAX_AMOUNT_COLOR);
		}
	}

	private boolean checkAmountOfColor(final byte color) {
		return color >= 0 && color < MAX_AMOUNT_COLOR + 1;
	}

	public byte getRed() {
		return redAmount;
	}

	public byte getGreen() {
		return greenAmount;
	}

	public byte getBlue() {
		return blueAmount;
	}

	@Override
	public String toString() {
		return "RED = " + redAmount + " || GREEN = " + greenAmount + " || BLUE = " + blueAmount;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Color) {
			final Color c = (Color) obj;
			return redAmount == c.redAmount && greenAmount == c.greenAmount && blueAmount == c.blueAmount;
		} else {
			return false;
		}
	}

}
