package libterminal.api;

import libterminal.lib.network.MulticastReceiver;
import libterminal.lib.network.ReceiverSelector;
import libterminal.lib.network.Sender;
import libterminal.lib.protocol.CommandParameters;
import libterminal.lib.protocol.QSYPacket;
import libterminal.lib.routine.Color;
import libterminal.lib.routine.Routine;
import libterminal.lib.terminal.Terminal;
import libterminal.patterns.observer.EventListener;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.TreeMap;

public final class TerminalAPI {

	private Thread threadReceiveSelector;
	private Thread threadTerminal;
	private Thread threadSender;
	private Thread threadMulticastReceiver;

	private Terminal terminal;

	private Inet4Address multicastAddress;

	private boolean up;

	public TerminalAPI(Inet4Address multicastAddress) {
		this.multicastAddress = multicastAddress;
	}

	public void start() throws IOException {
		final MulticastReceiver multicastReceiver = new MulticastReceiver(
			multicastAddress,
			(InetAddress) InetAddress.getByName(QSYPacket.MULTICAST_ADDRESS),
			QSYPacket.MULTICAST_PORT
		);
		terminal = new Terminal();
		final ReceiverSelector receiverSelector = new ReceiverSelector();
		final Sender senderSelector = new Sender(terminal.getNodes());
		multicastReceiver.addListener(terminal);
		receiverSelector.addListener(terminal);
		terminal.addListener(receiverSelector);
		terminal.addListener(senderSelector);

		threadReceiveSelector = new Thread(receiverSelector, "Receive Selector");
		threadTerminal = new Thread(terminal, "Terminal");
		threadSender = new Thread(senderSelector, "Sender Selector");
		threadMulticastReceiver = new Thread(multicastReceiver, "Multicast Receiver");

		threadReceiveSelector.start();
		threadTerminal.start();
		threadSender.start();
		threadMulticastReceiver.start();

		up = true;
	}

	public boolean isUp() {
		return up;
	}

	public void startNodesSearch() {
		terminal.searchNodes();
	}

	public void stopNodesSearch() {
		terminal.finalizeNodesSearch();
	}

	public void executeCustom(final Routine routine, final TreeMap<Integer, Integer> nodesIdsAssociations,
	                          final int maxExecTime, boolean soundEnabled, boolean touchEnabled) {
		terminal.executeCustom(routine, nodesIdsAssociations, maxExecTime, soundEnabled, touchEnabled);
	}

	public void executePlayer(final TreeMap<Integer, Integer> nodesIdsAssociations, final int numberOfNodes,
	                          final ArrayList<Color> playersAndColors, final boolean waitForAllPlayers, final long timeOut,
	                          final long delay, final long maxExecTime, final int totalStep, final boolean stopOnTimeout,
	                          boolean soundEnabled, boolean touchEnabled) {
		terminal.executePlayer(nodesIdsAssociations, numberOfNodes, playersAndColors, waitForAllPlayers, timeOut,
			delay, maxExecTime, totalStep, stopOnTimeout, soundEnabled, touchEnabled);
	}

	public void addListener(final EventListener listener) {
		terminal.addListener(listener);
	}

	public void removeListener(EventListener listener) {
		terminal.removeListener(listener);
	}

	public void stop() throws InterruptedException {
		threadReceiveSelector.interrupt();
		threadTerminal.interrupt();
		threadSender.interrupt();
		threadMulticastReceiver.interrupt();
		threadReceiveSelector.join();
		threadTerminal.join();
		threadSender.join();
		threadMulticastReceiver.join();
		up = false;
	}

	public int connectedNodesAmount() {
		return terminal.getNodes().size();
	}

	public void stopExecution() {
		terminal.stopExecution();
	}

	public void sendPacket(Integer nodeId, CommandParameters commandParameters, boolean soundEnabled,
	                       boolean touchEnabled) {
		terminal.sendQSYPacket(QSYPacket.createCommandPacket(terminal.getNodeAddress(nodeId), commandParameters, touchEnabled, soundEnabled));
	}
}
