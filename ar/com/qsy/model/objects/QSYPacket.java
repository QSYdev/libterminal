package ar.com.qsy.model.objects;

import java.awt.Color;
import java.net.InetAddress;
import java.util.Arrays;

public final class QSYPacket {

	public static final long QSY_PROTOCOL_VERSION = 130617;

	public static final byte PACKET_SIZE = 12;
	public static final int KEEP_ALIVE_MS = 1000;

	public static final String MULTICAST_ADDRESS = "224.0.0.12";
	public static final int MULTICAST_PORT = 3000;
	public static final int TCP_PORT = 3000;

	public static final int MIN_ID_SIZE = 0;
	public static final int MAX_ID_SIZE = (int) (Math.pow(2, 16) - 1);
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

	private static final byte TYPE_HELLO = 0x00;
	private static final byte TYPE_COMMAND = 0x01;
	private static final byte TYPE_TOUCHE = 0x02;
	private static final byte TYPE_KEEPALIVE = 0x03;

	private static final int RED_VALUE = 0xF000;
	private static final int GREEN_VALUE = 0x0F00;
	private static final int BLUE_VALUE = 0x00F0;
	private static final int WHITE_VALUE = 0x000F;
	private static final int NO_COLOR_VALUE = 0x0000;

	public enum PacketType {
		Hello, Command, Touche, Keepalive
	}

	private final InetAddress nodeAddress;
	private final int id;
	private final Color color;
	private final long delay;
	private final PacketType packetType;
	private final byte[] rawData;

	private QSYPacket(final InetAddress nodeAddress, final PacketType type, final int id, final Color color, final long delay) throws IllegalArgumentException {
		this.rawData = new byte[PACKET_SIZE];
		rawData[Q_INDEX] = 'Q';
		rawData[S_INDEX] = 'S';
		rawData[Y_INDEX] = 'Y';
		this.nodeAddress = nodeAddress;
		this.id = id;
		setDataIntoArray(id, (byte) 16, rawData, ID_INDEX);
		this.color = color;
		setDataIntoArray(getIntFromColor(color), (byte) 16, rawData, COLOR_RG_INDEX);
		this.delay = delay;
		setDataIntoArray(delay, (byte) 32, rawData, DELAY_INDEX);
		this.packetType = type;
		setDataIntoArray(getShortFromPacketType(packetType), (byte) 8, rawData, TYPE_INDEX);
	}

	public QSYPacket(final InetAddress nodeAddress, final byte[] data) throws IllegalArgumentException {
		if (data.length != PACKET_SIZE) {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El tamaño del QSYPacket debe ser de " + PACKET_SIZE + ".");
		} else if (data[Q_INDEX] != 'Q' || data[S_INDEX] != 'S' || data[Y_INDEX] != 'Y') {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket posee una firma inválida.");
		}

		this.nodeAddress = nodeAddress;
		this.packetType = getPacketTypeFromShort(((short) convertBytesToLong(data, TYPE_INDEX, TYPE_INDEX)));
		this.id = (int) convertBytesToLong(data, ID_INDEX, (byte) (ID_INDEX + 1));
		this.color = getColorFromInt((int) convertBytesToLong(data, COLOR_RG_INDEX, COLOR_BW_INDEX));
		this.delay = (long) convertBytesToLong(data, DELAY_INDEX, (byte) (DELAY_INDEX + 3));

		rawData = Arrays.copyOf(data, PACKET_SIZE);
	}

	private static Color getColorFromInt(final int typeColor) throws IllegalArgumentException {
		final Color color;
		switch (typeColor) {
		case RED_VALUE: {
			color = Color.red;
			break;
		}
		case GREEN_VALUE: {
			color = Color.green;
			break;
		}
		case BLUE_VALUE: {
			color = Color.blue;
			break;
		}
		case WHITE_VALUE: {
			color = Color.white;
			break;
		}
		case NO_COLOR_VALUE: {
			color = null;
			break;
		}
		default: {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket no posee el color correspondiente.");
		}
		}
		return color;
	}

	private static int getIntFromColor(final Color color) throws IllegalArgumentException {
		final int result;
		if (color == null) {
			result = NO_COLOR_VALUE;
		} else if (color.equals(Color.red)) {
			result = RED_VALUE;
		} else if (color.equals(Color.green)) {
			result = GREEN_VALUE;
		} else if (color.equals(Color.blue)) {
			result = BLUE_VALUE;
		} else {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket no posee el color correspondiente.");
		}
		return result;
	}

	private static PacketType getPacketTypeFromShort(final short type) throws IllegalArgumentException {
		final PacketType packetType;
		switch (type) {
		case TYPE_HELLO:
			packetType = PacketType.Hello;
			break;
		case TYPE_COMMAND:
			packetType = PacketType.Command;
			break;
		case TYPE_TOUCHE:
			packetType = PacketType.Touche;
			break;
		case TYPE_KEEPALIVE:
			packetType = PacketType.Keepalive;
			break;
		default:
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket posee un type invalido.");
		}

		return packetType;
	}

	private static short getShortFromPacketType(final PacketType packetType) {
		final short type;
		switch (packetType) {
		case Hello: {
			type = TYPE_HELLO;
			break;
		}
		case Command: {
			type = TYPE_COMMAND;
			break;
		}
		case Keepalive: {
			type = TYPE_KEEPALIVE;
			break;
		}
		case Touche: {
			type = TYPE_TOUCHE;
			break;
		}
		default: {
			throw new IllegalArgumentException("<< QSY_PACKET_ERROR >> El QSYPacket posee un type invalido.");
		}
		}
		return type;
	}

	public InetAddress getNodeAddress() {
		return nodeAddress;
	}

	public PacketType getType() {
		return packetType;
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

	public byte[] getRawData() {
		return rawData;
	}

	private static long convertBytesToLong(final byte[] data, final byte firstIndex, final byte lastIndex) {
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

	private static void setDataIntoArray(final long value, final byte bits, final byte[] data, final int firstIndex) {
		long val = value;

		long mul = 1;
		for (byte i = (byte) (bits - 1); i >= 0; i--) {
			data[i / 8 + firstIndex] += mul * (val % 2);
			mul <<= 1;
			if (mul == 256) {
				mul = 1;
			}
			val >>= 1;
		}
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		switch (getType()) {
		case Hello: {
			stringBuilder.append("QSYHelloPacket");
			break;
		}
		case Command: {
			stringBuilder.append("QSYCommandPacket");
			break;
		}
		case Touche: {
			stringBuilder.append("QSYTouchePacket");
			break;
		}
		case Keepalive: {
			stringBuilder.append("QSYKeepalivePacket");
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
			return new QSYPacket(nodeAddress, PacketType.Command, nodeId, color, delay);
		}
	}

}
