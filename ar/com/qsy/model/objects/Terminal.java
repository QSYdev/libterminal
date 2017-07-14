package ar.com.qsy.model.objects;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.com.qsy.exceptions.NotEnoughConnectedNodesException;
import ar.com.qsy.model.patterns.observer.AsynchronousListener;
import ar.com.qsy.model.patterns.observer.Event;
import ar.com.qsy.model.patterns.observer.Event.EventType;
import ar.com.qsy.model.patterns.observer.EventListener;
import ar.com.qsy.model.patterns.observer.EventSource;

public final class Terminal extends EventSource implements Runnable, AutoCloseable, EventListener {

	private final TreeMap<Integer, Node> nodes;

	private final AsynchronousListener internalListener;
	private final KeepAlive keepAlive;

	private Executor executor;

	private final AtomicBoolean searchNodes;
	private final AtomicBoolean running;

	public Terminal() {
		this.executor = null;
		this.nodes = new TreeMap<>();
		this.internalListener = new AsynchronousListener();
		this.keepAlive = new KeepAlive(nodes);
		keepAlive.addListener(this);
		this.searchNodes = new AtomicBoolean(false);
		this.running = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		while (running.get()) {

			try {
				final Event event = internalListener.getEvent();
				switch (event.getEventType()) {
					case incomingQSYPacket: {
						final QSYPacket qsyPacket = (QSYPacket) event.getContent();

						switch (qsyPacket.getType()) {
							case Hello: {
								if (searchNodes.get()) {
									final boolean contains;
									synchronized (nodes) {
										contains = nodes.containsKey(qsyPacket.getId());
									}
									if (!contains) {
										final Node node = new Node(qsyPacket);
										synchronized (nodes) {
											nodes.put(node.getNodeId(), node);
										}
										keepAlive.newNodeCreated(node);
										sendEvent(new Event(EventType.newNode, node));
									}
								}
								break;
							}
							case Keepalive: {
								keepAlive.qsyKeepAlivePacketReceived(qsyPacket);
								break;
							}
							case Touche: {
								if (executor != null && executor.isRunning()) {
									Node node;
									synchronized (nodes) {
										node = nodes.get(qsyPacket.getId());
									}
									if (node != null) {
										executor.touche(node);
									}
									// TODO: agregar el guardado de informacion del delay
									// de los nodos para que una vez que termine la rutina
									// tener la informacion disponible para guardar lo que
									// queramos
//									long delay = qsyPacket.getDelay();
//									int logicId = executor.getLogicIdFromNodeId(node.getNodeId());
//									algunaEstructuraEnOrden.put(logicId, delay);
								}
								break;
							}
							default: {
								break;
							}
						}
						break;
					}
					case keepAliveError: {
						final Node node = (Node) event.getContent();
						synchronized (nodes) {
							nodes.remove(node.getNodeId());
						}
						node.close();
						sendEvent(new Event(EventType.disconnectedNode, node));
						System.err.println("Se ha desconectado el nodo id = " + node.getNodeId());
						break;
					}
					case executorStepTimeout: {
						if (executor != null && executor.isRunning()) {
							executor.stepTimeout();
						}
					}
					case executorDoneExecuting: {
						executor.stop();
						executor = null;
						// TODO: falta agregar si le decimos algo al usuario
					}
					default: {
						break;
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
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

	/*
	 * stopExecutor se llamaria desde la interfaz del cliente que decidamos
	 * usar. La idea de esto es poder cortar la ejecucion de la rutina a partir
	 * de una accion del usuario.
	 */
	public void stopExecutor() {
		if (executor != null) {
			executor.stop();
			System.out.println("Rutina stopeada");
		}
	}

	public void executePlayer(ArrayList<Color> playersAndColors, boolean soundEnabled, boolean touchEnabled,
	                          long maxExecTime, int totalSteps, int timeout) throws NotEnoughConnectedNodesException {

		HashMap<Integer, Node> nodesAddresses = getNodesAssociations(playersAndColors.size());
		if (nodesAddresses == null) {
			throw new NotEnoughConnectedNodesException();
		}

		executor = new PlayerExecutor(playersAndColors, nodesAddresses, soundEnabled, touchEnabled, maxExecTime,
			totalSteps, timeout);
		executor.start();
	}

	// TODO: asociacion nodos logicos y fisicos a rutinas, va dentro de routine por ahi?
	public void executeCustom(Routine routine, ArrayList<Integer> nodesIdsAssociations, boolean soundEnabled,
	                          boolean touchEnabled) throws NotEnoughConnectedNodesException {

		HashMap<Integer, Node> nodesAddresses = getNodesAssociations(routine.getNumberOfNodes());
		if (nodesAddresses == null) {
			throw new NotEnoughConnectedNodesException();
		}

		executor = new CustomExecutor(routine, nodesAddresses, soundEnabled, touchEnabled);
		executor.start();
	}

	/**
	 * getNodesAssociations genera una estructura donde se le asignan nodos fisicos a direcciones logicas
	 * empezando en la direccion 1.
	 *
	 * @param numberOfNodes: indica la cantidad de nodos que necesitamos
	 * @return HashMap<Integer, Node>: devuelve null si no se tiene la cantidad de nodos fisicos necesaria.
	 * En caso de que se tenga devuelve un HashMap donde para cada clave logica(empezando en 1 hasta numberOfNodes)
	 * se le asigna un nodo correspondiente. Esto seria en orden de conexion de los nodos
	 */
	private HashMap<Integer, Node> getNodesAssociations(int numberOfNodes) {
		if (numberOfNodes > nodes.size()) {
			return null;
		}

		HashMap<Integer, Node> nodesAddresses = new HashMap<>();
		int i = 1;
		for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
			nodesAddresses.put(i++, entry.getValue());
		}
		return nodesAddresses;
	}

	public void sendQSYPacket(final QSYPacket qsyPacket) throws Exception {
		sendEvent(new Event(EventType.commandPacketSent, qsyPacket));
	}

	@Override
	public void close() throws Exception {
		running.set(false);
		keepAlive.close();
	}

	@Override
	public void receiveEvent(final Event event) throws InterruptedException {
		internalListener.receiveEvent(event);
	}

}
