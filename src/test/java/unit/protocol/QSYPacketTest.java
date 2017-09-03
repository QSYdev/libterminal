package unit.protocol;

import libterminal.lib.protocol.CommandParameters;
import libterminal.lib.protocol.QSYPacket;
import libterminal.lib.routine.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@RunWith(JUnitPlatform.class)
public class QSYPacketTest {
	public static final byte PACKET_SIZE = 12;
	private static final byte Q_INDEX = 0x00;
	private static final byte S_INDEX = 0x01;
	private static final byte Y_INDEX = 0x02;
	private static final byte TYPE_INDEX = 0x03;
	private static final byte ID_INDEX = 0x04;
	private static final byte COLOR_RG_INDEX = 0x06;
	private static final byte COLOR_B_INDEX = 0x07;
	private static final byte DELAY_INDEX = 0x08;
	private static final byte TYPE_HELLO = 0x00;
	private static final byte TYPE_COMMAND = 0x01;
	private static final byte TYPE_TOUCHE = 0x02;
	private static final byte TYPE_KEEPALIVE = 0x03;

	private byte[] qsyPacketData;

	@Test
	public void qsyPacketConstruction() {
		final InetAddress nodeAddress = mock(InetAddress.class);
		qsyPacketData = new byte[PACKET_SIZE];
		qsyPacketData[Q_INDEX] = 's';
		assertThrows(IllegalArgumentException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				new QSYPacket(nodeAddress, qsyPacketData);
			}
		});
		qsyPacketData[Q_INDEX] = 'Q';
		qsyPacketData[S_INDEX] = 'q';
		assertThrows(IllegalArgumentException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				new QSYPacket(nodeAddress, qsyPacketData);
			}
		});
		qsyPacketData[S_INDEX] = 'S';
		qsyPacketData[Y_INDEX] = 'l';
		assertThrows(IllegalArgumentException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				new QSYPacket(nodeAddress, qsyPacketData);
			}
		});

		qsyPacketData[Q_INDEX] = 'Q';
		qsyPacketData[S_INDEX] = 'S';
		qsyPacketData[Y_INDEX] = 'Y';
		assertThrows(IllegalArgumentException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				new QSYPacket(null, qsyPacketData);
			}
		});
		assertNotNull(new QSYPacket(nodeAddress, qsyPacketData), "QSYPacket no deberia ser null");
	}

	@Test
	public void createCommandPacket() {
		final CommandParameters commandParameters = new CommandParameters(1,1000, new Color((byte)15, (byte)0, (byte)0),0);
		assertThrows(IllegalArgumentException.class, new Executable() {
			@Override
			public void execute() throws Throwable {
				QSYPacket.createCommandPacket(null,
					commandParameters, false, false);
			}
		});
		InetAddress nodeAddress = mock(InetAddress.class);
		assertNotNull(QSYPacket.createCommandPacket(nodeAddress, commandParameters, false, false),
			"El QSYPacket creado no deberia ser null");
	}
}
