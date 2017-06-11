package ar.com.qsy;

import java.awt.Color;

public final class QSYPacketTools {

	/**
	 * El periodo con el que se envian paquetes KeepAlive. La terminal espera
	 * por el doble del tiempo para saber si un nodo ha perdido su conexion.
	 */
	public static final int KEEP_ALIVE_MS = 10000;

	/**
	 * La longitud de cada paquete QSY.
	 */
	public static final byte PACKET_SIZE = 16;
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

	private static final short MIN_ID_SIZE = 0;
	private static final short MAX_ID_SIZE = (short) (Math.pow(2, 8) - 1);
	private static final long MIN_DELAY_SIZE = 0;
	private static final long MAX_DELAY_SIZE = (long) (Math.pow(2, 32) - 1);

	/*
	 * Los indices del paquete.
	 */
	private static final byte Q_INDEX = 0x00;
	private static final byte S_INDEX = 0x01;
	private static final byte Y_INDEX = 0x02;
	private static final byte TYPE_INDEX = 0x03;
	@SuppressWarnings("unused")
	private static final byte PHASE_INDEX = 0x04;
	private static final byte COLOR_RG_INDEX = 0x06;
	private static final byte COLOR_BW_INDEX = 0x07;
	private static final byte DELAY_INDEX = 0x08;
	private static final byte ID_INDEX = 0x0C;
	@SuppressWarnings("unused")
	private static final byte RESERVED_INDEX = 0x0D;

	/*
	 * Las posibilidades para el campo Type.
	 */
	private static final byte TYPE_HELLO = 0x00;
	private static final byte TYPE_COMMAND = 0x01;
	private static final byte TYPE_TOUCHE = 0x02;
	private static final byte TYPE_RENAME = 0x03;
	private static final byte TYPE_KEEP_ALIVE = 0x04;

	/**
	 * Analiza la informacion en data para determinar si se trata de un paquete
	 * QSY.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve true si se trata de un paquete QSY y false en caso
	 *         contrario.
	 */
	public static boolean isQSYPacket(final byte[] data) {
		return data.length == PACKET_SIZE && data[Q_INDEX] == 'Q' && data[S_INDEX] == 'S' && data[Y_INDEX] == 'Y';
	}

	/**
	 * Crea un paquete a modo de plantilla para poder rellenar los campos del
	 * mensaje.
	 * 
	 * @return Devuelve un arreglo de bytes con la plantilla de como deben ser
	 *         los paquetes QSY.
	 */
	public static byte[] createQSYPacket() {
		final byte[] data = new byte[PACKET_SIZE];

		data[Q_INDEX] = 'Q';
		data[S_INDEX] = 'S';
		data[Y_INDEX] = 'Y';

		return data;
	}

	/**
	 * Analiza la informacion en data para determinar si se trata de un paquete
	 * QSY Hello.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve true si se trata de un paquete QSY Hello y false en caso
	 *         contrario.
	 */
	public static boolean isHelloPacket(final byte[] data) {
		return isQSYPacket(data) && data[TYPE_INDEX] == TYPE_HELLO;
	}

	/**
	 * Crea un paquete QSY Hello.
	 * 
	 * @param id
	 *            El id del nodo que envia el paquete hello. Se castea a byte.
	 * 
	 * @throws IllegalArgumentException
	 *             si el id especificado no cae dentro del rango de 1 byte sin
	 *             singo.
	 * 
	 * @return Devuelve un arreglo de bytes con la informacion a transmitir
	 *         hacia la terminal.
	 */
	public static byte[] createHelloPacket(final short id) throws IllegalArgumentException {
		if (id < MIN_ID_SIZE || id > MAX_ID_SIZE) {
			throw new IllegalArgumentException("El id debe estar entre [" + MIN_ID_SIZE + " ; " + MAX_ID_SIZE + "]");
		}

		final byte[] data = createQSYPacket();
		data[TYPE_INDEX] = TYPE_HELLO;
		data[ID_INDEX] = (byte) id;

		return data;
	}

	/**
	 * Analiza la informacion en data para determinar si se trata de un paquete
	 * QSY Command.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve true si se trata de un paquete QSY Command y false en
	 *         caso contrario.
	 */
	public static boolean isCommandPacket(final byte[] data) {
		return isQSYPacket(data) && data[TYPE_INDEX] == TYPE_COMMAND;
	}

	/**
	 * Crea un paquete QSY Command.
	 * 
	 * @param color
	 *            El color a cambiar. Si se recibe null entonces se interpreta
	 *            como apagado.
	 * @param delay
	 *            El tiempo que tiene que esperar el nodo, desde que recibe el
	 *            comando hasta que debe encender su luz. Se castea a int.
	 * 
	 * @throws IllegalArgumentException
	 *             si el delay especificado no cae dentro del rango de 4 bytes
	 *             sin signo.
	 * 
	 * @return Devuelve un arreglo de bytes con la informacion a transmitir
	 *         hacia el nodo.
	 */
	public static byte[] createCommandPacket(final Color color, final long delay) throws IllegalArgumentException {
		if (delay < MIN_DELAY_SIZE || delay > MAX_DELAY_SIZE) {
			throw new IllegalArgumentException("El delay debe estar entre [" + MIN_DELAY_SIZE + " ; " + MAX_DELAY_SIZE + "]");
		}

		final byte[] data = createQSYPacket();
		data[TYPE_INDEX] = TYPE_COMMAND;

		if (color != null) {
			if (color == Color.red) {
				data[COLOR_RG_INDEX] = (byte) 0xF0;
			} else if (color == Color.green) {
				data[COLOR_RG_INDEX] = (byte) 0x0F;
			} else if (color == Color.blue) {
				data[COLOR_BW_INDEX] = (byte) 0xF0;
			} else if (color == Color.white) {
				data[COLOR_BW_INDEX] = (byte) 0x0F;
			}
		}

		for (int i = 3; i >= 0; i--) {
			data[DELAY_INDEX + (3 - i)] = (byte) (delay >> i * 8);
		}

		return data;
	}

	/**
	 * Analiza la informacion en data para determinar si se trata de un paquete
	 * QSY Touche.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve true si se trata de un paquete QSY Touche y false en
	 *         caso contrario.
	 */
	public static boolean isTouchePacket(final byte[] data) {
		return isQSYPacket(data) && data[TYPE_INDEX] == TYPE_TOUCHE;
	}

	/**
	 * Analiza la informacion en data para determinar si se trata de un paquete
	 * QSY Rename.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve true si se trata de un paquete QSY Rename y false en
	 *         caso contrario.
	 */
	public static boolean isRenamePacket(final byte[] data) {
		return isQSYPacket(data) && data[TYPE_INDEX] == TYPE_RENAME;
	}

	/**
	 * Crea un paquete QSY Rename.
	 * 
	 * @param newId
	 *            El nuevo id del nodo cuyo id se quiere cambiar. Se castea a
	 *            byte.
	 * 
	 * @throws IllegalArgumentException
	 *             si el id especificado no cae dentro del rango de 1 byte sin
	 *             singo.
	 * 
	 * @return Devuelve un arreglo de bytes con la informacion a transmitir
	 *         hacia el nodo.
	 */
	public static byte[] createRenamePacket(final short newId) throws IllegalArgumentException {
		if (newId < MIN_ID_SIZE || newId > MAX_ID_SIZE) {
			throw new IllegalArgumentException("El nuevo id debe estar entre [" + MIN_ID_SIZE + " ; " + MAX_ID_SIZE + "]");
		}

		final byte[] data = createQSYPacket();
		data[TYPE_INDEX] = TYPE_RENAME;
		data[ID_INDEX] = (byte) newId;

		return data;
	}

	/**
	 * Analiza la informacion en data para determinar si se trata de un paquete
	 * QSY KeepAlive.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve true si se trata de un paquete QSY KeepAlive y false en
	 *         caso contrario.
	 */
	public static boolean isKeepAlivePacket(final byte[] data) {
		return isQSYPacket(data) && data[TYPE_INDEX] == TYPE_KEEP_ALIVE;
	}

	/**
	 * Crea un paquete QSY KeepAlive.
	 * 
	 * @return Devuelve un arreglo de bytes con la informacion a transmitir
	 *         hacia el nodo.
	 */
	public static byte[] createKeepAlivePacket() {
		final byte data[] = createQSYPacket();

		data[TYPE_INDEX] = TYPE_KEEP_ALIVE;

		return data;
	}

	/**
	 * Devuelve el nodeId de la informacion que se recibe.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve el nodeId de la informacion que se recibe.
	 */
	public static short getNodeId(final byte[] data) {
		return data[ID_INDEX];
	}

	/**
	 * Devuelve el delay de la informacion que se recibe.
	 * 
	 * @param data
	 *            La informacion.
	 * @return Devuelve el delay de la informacion que se recibe.
	 */
	public static long getNodeDelay(final byte[] data) {
		long delay = 0;

		long mul = 1;
		for (byte i = 3; i >= 0; i--) {
			long b = data[DELAY_INDEX + i];
			for (byte j = 0; j < 8; j++) {
				delay += mul * (0x1 & b);
				b >>>= 1;
				mul <<= 1;
			}
		}
		return delay;
	}

	public static void main(String[] args) {
		final byte[] data = { 0, 0, 0, 0, 0, 0, 0, 0, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, 0, 0, 0, 0 };
		System.out.println(getNodeDelay(data));
	}

}
