package ar.com.qsy.model.utils;

import java.awt.Color;
import java.net.InetAddress;

public final class QSYPacket {

	/**
	 * El periodo con el que se envian paquetes KeepAlive. La terminal espera
	 * por el doble del tiempo para saber si un nodo ha perdido su conexion.
	 */
	public static final int KEEP_ALIVE_MS = 10000;

	/**
	 * La longitud de cada paquete QSY.
	 */
	public static final byte PACKET_SIZE = 12;
	/**
	 * La direccion multicast donde se envian los paquetes multicast.
	 */
	public static final String MULTICAST_ADDRESS = "224.0.0.12";
	/**
	 * El puerto donde se envian los paquetes multicast.
	 */
	public static final int MULTICAST_PORT = 3000;

	/**
	 * El puerto del nodo hacia donde se tiene que conectar la aplicacion.
	 */
	public static final int TCP_PORT = 3000;

	private static final int MIN_ID_SIZE = 0;
	private static final int MAX_ID_SIZE = (int) (Math.pow(2, 16) - 1);
	private static final long MIN_DELAY_SIZE = 0;
	private static final long MAX_DELAY_SIZE = (long) (Math.pow(2, 32) - 1);

	private static final byte Q_INDEX = 0x00;
	private static final byte S_INDEX = 0x01;
	private static final byte Y_INDEX = 0x02;
	private static final byte TYPE_INDEX = 0x03;
	private static final byte ID_INDEX = 0x04;
	private static final byte COLOR_RG_INDEX = 0x06;
	private static final byte COLOR_BW_INDEX = 0x07;
	private static final byte DELAY_INDEX = 0x08;

	public static final byte TYPE_HELLO = 0x00;
	public static final byte TYPE_COMMAND = 0x01;
	public static final byte TYPE_TOUCHE = 0x02;
	public static final byte TYPE_KEEP_ALIVE = 0x03;

	private static final int RED_COLOR = 0xF000;
	private static final int GREEN_COLOR = 0x0F00;
	private static final int BLUE_COLOR = 0x00F0;
	private static final int WHITE_COLOR = 0x000F;
	private static final int NO_COLOR = 0x0000;

	private final InetAddress nodeAddress;
	private final short type;
	private final int id;
	private final Color color;
	private final long delay;

	private QSYPacket(final InetAddress nodeAddress, final short type, final int id, final Color color, final long delay) {
		this.nodeAddress = nodeAddress;
		this.type = type;
		this.id = id;
		this.color = color;
		this.delay = delay;
	}

	public QSYPacket(final InetAddress nodeAddress, final byte[] data) throws IllegalArgumentException {
		if (data.length != PACKET_SIZE) {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El tamaño del QSYPacket debe ser de " + PACKET_SIZE + " .");
		} else if (data[Q_INDEX] != 'Q' || data[S_INDEX] != 'S' || data[Y_INDEX] != 'Y') {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket posee una firma invalida.");
		}
		this.nodeAddress = nodeAddress;
		this.type = (short) getUnsignedValue(data, TYPE_INDEX, TYPE_INDEX);
		if (type != TYPE_HELLO && type != TYPE_COMMAND && type != TYPE_KEEP_ALIVE && type != TYPE_TOUCHE) {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket posee un type invalido.");
		}
		this.id = (int) getUnsignedValue(data, ID_INDEX, (byte) (ID_INDEX + 1));
		this.color = getColor((int) getUnsignedValue(data, COLOR_RG_INDEX, COLOR_BW_INDEX));
		this.delay = (long) getUnsignedValue(data, DELAY_INDEX, (byte) (DELAY_INDEX + 3));
	}

	private Color getColor(final int typeColor) throws IllegalArgumentException {
		final Color color;
		switch (typeColor) {
		case RED_COLOR: {
			color = Color.red;
			break;
		}
		case GREEN_COLOR: {
			color = Color.green;
			break;
		}
		case BLUE_COLOR: {
			color = Color.blue;
			break;
		}
		case WHITE_COLOR: {
			color = Color.white;
			break;
		}
		case NO_COLOR: {
			color = null;
			break;
		}
		default: {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket no posee el color correspondiente.");
		}
		}
		return color;
	}

	public InetAddress getNodeAddress() {
		return nodeAddress;
	}

	public short getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public Color getColor() {
		return color;
	}

	public long getDelay() {
		return delay;
	}

	private static long getUnsignedValue(final byte[] data, final byte firstIndex, final byte lastIndex) {
		long value = 0;

		long mul = 1;
		for (byte i = lastIndex; i >= firstIndex; i--) {
			long b = data[i];
			for (byte j = 0; j < 8; j++) {
				value += mul * (0x1 & b);
				b >>>= 1;
				mul <<= 1;
			}
		}

		return value;
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		switch (getType()) {
		case TYPE_HELLO: {
			stringBuilder.append("QSYHelloPacket");
			break;
		}
		case TYPE_COMMAND: {
			stringBuilder.append("QSYCommandPacket");
			break;
		}
		case TYPE_TOUCHE: {
			stringBuilder.append("QSYTouchePacket");
			break;
		}
		case TYPE_KEEP_ALIVE: {
			stringBuilder.append("QSYKeepAlivePacket");
			break;
		}
		}
		stringBuilder.append(" From = " + getNodeAddress() + "\n");
		stringBuilder.append("ID = " + getId() + "\t");
		stringBuilder.append("COLOR = ");
		if (color == null) {
			stringBuilder.append("No Color");
		} else if (color.equals(Color.red)) {
			stringBuilder.append("Red");
		} else if (color.equals(Color.green)) {
			stringBuilder.append("Green");
		} else if (color.equals(Color.blue)) {
			stringBuilder.append("Blue");
		} else if (color.equals(Color.white)) {
			stringBuilder.append("White");
		}
		stringBuilder.append("\tDELAY = " + getDelay());

		return stringBuilder.toString();
	}

	public static QSYPacket createCommandPacket(final InetAddress nodeAddress, final Color color, final long delay) throws IllegalArgumentException {
		return createCommandPacket(nodeAddress, 0, color, delay);
	}

	public static QSYPacket createCommandPacket(final InetAddress nodeAddress, final int nodeId, final Color color, final long delay) throws IllegalArgumentException {
		if (nodeId < MIN_ID_SIZE || nodeId > MAX_ID_SIZE) {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El id debe estar entre [" + MIN_ID_SIZE + " ; " + MAX_ID_SIZE + "]");
		} else if (color != null && !color.equals(Color.red) && !color.equals(Color.green) && !color.equals(Color.blue) && !color.equals(Color.white)) {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket no posee el color correspondiente.");
		} else if (delay < MIN_DELAY_SIZE || delay > MAX_DELAY_SIZE) {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El delay debe estar entre [" + MIN_DELAY_SIZE + " ; " + MAX_DELAY_SIZE + "]");
		} else {
			return new QSYPacket(nodeAddress, TYPE_COMMAND, nodeId, color, delay);
		}
	}

}
