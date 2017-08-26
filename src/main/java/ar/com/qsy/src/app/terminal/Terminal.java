package ar.com.qsy.src.app.terminal;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.src.app.executor.CustomExecutor;
import ar.com.qsy.src.app.executor.Executor;
import ar.com.qsy.src.app.executor.PlayerExecutor;
import ar.com.qsy.src.app.keepalive.KeepAlive;
import ar.com.qsy.src.app.node.Node;
import ar.com.qsy.src.app.protocol.CommandParameters;
import ar.com.qsy.src.app.protocol.QSYPacket;
import ar.com.qsy.src.app.routine.Color;
import ar.com.qsy.src.app.routine.Routine;
import ar.com.qsy.src.patterns.observer.AsynchronousListener;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.Event.EventType;
import ar.com.qsy.src.patterns.observer.EventListener;
import ar.com.qsy.src.patterns.observer.EventSource;

public final class Terminal extends EventSource implements Runnable, EventListener {

	private final TreeMap<Integer, Node> nodes;

	private final AsynchronousListener internalListener;
	private final KeepAlive keepAlive;
	private volatile Executor executor;
	private final Object executorLock;

	private final AtomicBoolean searchNodes;
	private final AtomicBoolean running;

	private final AtomicBoolean touchEnabled;
	private final AtomicBoolean soundEnabled;

	public Terminal() {
		this.nodes = new TreeMap<>();
		this.internalListener = new AsynchronousListener();
		this.keepAlive = new KeepAlive(nodes);
		this.keepAlive.addListener(this);
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
		this.executor = null;
		this.executorLock = new Object();
		this.touchEnabled = new AtomicBoolean(false);
		this.soundEnabled = new AtomicBoolean(false);
	}

	@Override
	public void run() {
		while (running.get()) {
			try {
				final Event event = internalListener.getEvent();
				processEvent(event);
			} catch (final ClosedByInterruptException | InterruptedException e) {
				try {
					this.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void processEvent(Event event) throws IOException, ClosedByInterruptException {
		switch (event.getEventType()) {
		case incomingQSYPacket:
			processPacket((QSYPacket) event.getContent());
			break;
		case keepAliveError:
			final Node node = (Node) event.getContent();
			synchronized (nodes) {
				nodes.remove(node.getNodeId());
			}
			node.close();

			sendEvent(new Event(EventType.disconnectedNode, node));
			System.err.println("Se ha desconectado el nodo id = " + node.getNodeId());
			break;
		case commandRequest:
			final CommandParameters parameters = (CommandParameters) event.getContent();
			final InetAddress nodeAddress;
			synchronized (nodes) {
				nodeAddress = nodes.get(parameters.getPhysicalId()).getNodeAddress();
			}
			final QSYPacket commandPacket = QSYPacket.createCommandPacket(nodeAddress, parameters, touchEnabled.get(), soundEnabled.get());
			sendQSYPacket(commandPacket);
			break;
		case executorStepTimeout:
			System.out.println("Timeout de step.");
			break;
		case executorDoneExecuting:
			synchronized (executorLock) {
				if (executor != null) {
					executor.stop();
					executor.removeListener(this);
					executor = null;
				}
			}
			System.out.println("Se terminó la rutina");
			break;
		default:
			break;
		}
	}

	private void processPacket(QSYPacket qsyPacket) {
		switch (qsyPacket.getType()) {
		case Hello:
			if (searchNodes.get()) {
				final boolean contains;
				synchronized (nodes) {
					contains = nodes.containsKey(qsyPacket.getId());
				}
				if (!contains) {
					Node node;
					try {
						node = new Node(qsyPacket);
						synchronized (nodes) {
							nodes.put(node.getNodeId(), node);
						}
						keepAlive.newNodeCreated(node);
						sendEvent(new Event(EventType.newNode, node));
					} catch (IllegalArgumentException e) {
						System.err.println(e.getMessage());
					} catch (IOException e) {
						this.running.set(false);
						e.printStackTrace();
					}
				}
			}
			break;
		case Keepalive:
			keepAlive.qsyKeepAlivePacketReceived(qsyPacket);
			break;
		case Touche:
			synchronized (executorLock) {
				if (executor != null) {
					executor.touche(qsyPacket.getId());
				}
			}
			break;
		default:
			break;
		}
	}


	public TreeMap<Integer, Node> getNodes() {
		return nodes;
	}

	public void searchNodes() {
		searchNodes.set(true);
	}

	public void finalizeNodesSearch() {
		searchNodes.set(false);
	}

	public void executeCustom(final Routine routine, final TreeMap<Integer, Integer> nodesIdsAssociations) {
		synchronized (executorLock) {
			if (executor == null) {
				final TreeMap<Integer, Integer> associations = associateNodes(nodesIdsAssociations,
						routine.getNumberOfNodes());
				executor = new CustomExecutor(routine, associations);
				executor.addListener(this);
				executor.start();
			} else {
				throw new IllegalStateException("<< Terminal >> Hay una rutina activa. Finalizala antes de inciar otra.");
			}
		}
	}

	public void executePlayer(final TreeMap<Integer, Integer> nodesIdsAssociations, final int numberOfNodes,
			final ArrayList<Color> playersAndColors, final boolean waitForAllPlayers, final long timeOut,
			final long delay, final long maxExecTime, final int totalStep, final boolean stopOnTimeout) {

		synchronized (executorLock) {
			if (executor == null) {
				if (timeOut < 0 || delay < 0 || maxExecTime < 0 || totalStep < 0 || (maxExecTime == 0 && totalStep == 0)
						|| playersAndColors.size() > numberOfNodes) {
					throw new IllegalArgumentException();
				}
				final TreeMap<Integer, Integer> associations = associateNodes(nodesIdsAssociations, numberOfNodes);
				executor = new PlayerExecutor(associations, numberOfNodes, playersAndColors, waitForAllPlayers, timeOut,
						delay, maxExecTime, totalStep, stopOnTimeout);
				executor.addListener(this);
				executor.start();
			} else {
				throw new IllegalStateException("<< Terminal >> Hay una rutina activa. Finalizala antes de iniciar otra.");
			}
		}
	}

	public void stopExecution() {
		synchronized (executorLock) {
			if (executor != null) {
				executor.stop();
				executor.removeListener(this);
				executor = null;
			}
		}
	}

	private TreeMap<Integer, Integer> associateNodes(final TreeMap<Integer, Integer> nodesIdsAssociations,
			final int numberOfNodes) {
		TreeMap<Integer, Integer> nodesAddresses;
		if (nodesIdsAssociations == null) {
			synchronized (nodes) {
				nodesAddresses = getNodesAssociationsInOrder(numberOfNodes);
			}
		} else {
			synchronized (nodes) {
				nodesAddresses = getNodesAssociationsFromIds(nodesIdsAssociations);
			}
		}
		return nodesAddresses;
	}

	private TreeMap<Integer, Integer> getNodesAssociationsInOrder(final int numberOfNodes) {
		if (nodes.size() >= numberOfNodes) {
			final TreeMap<Integer, Integer> nodesAddresses = new TreeMap<>();
			int i = 1;
			for (final Entry<Integer, Node> entry : nodes.entrySet()) {
				nodesAddresses.put(i++, entry.getValue().getNodeId());
				if (i > numberOfNodes) {
					break;
				}
			}
			return nodesAddresses;
		} else {
			throw new IllegalArgumentException("<< Terminal >> No hay suficientes nodos para hacer la asociacion");
		}
	}

	private TreeMap<Integer, Integer> getNodesAssociationsFromIds(
			final TreeMap<Integer, Integer> nodesIdsAssociations) {
		if (nodesIdsAssociations.size() <= nodes.size()) {
			final TreeMap<Integer, Integer> nodesAddresses = new TreeMap<>();
			for (final Entry<Integer, Integer> entry : nodesIdsAssociations.entrySet()) {
				if (nodes.containsKey(entry.getValue())) {
					nodesAddresses.put(entry.getKey(), entry.getValue());
				} else {
					throw new IllegalStateException(
							"<< Terminal >> No hay suficientes nodos registrados para hacer la asociación.");
				}
			}
			return nodesAddresses;
		} else {
			throw new IllegalStateException("<< Terminal >> No hay suficientes nodos registrados para hacer la asociación.");
		}

	}

	public void sendQSYPacket(final QSYPacket qsyPacket) {
		sendEvent(new Event(EventType.commandPacketSent, qsyPacket));
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		keepAlive.close();
		synchronized (nodes) {
			nodes.forEach((key, node) -> {
			try {
				node.close();
			} catch (IOException e) {
				e.printStackTrace();
			}});
		}
		super.close();
	}

	@Override
	public void receiveEvent(final Event event) {
		internalListener.receiveEvent(event);
	}

}
