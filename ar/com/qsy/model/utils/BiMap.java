package ar.com.qsy.model.utils;

import java.util.Map.Entry;
import java.util.TreeMap;

import ar.com.qsy.model.objects.Node;

public final class BiMap {

	private final Integer[] logicIds;
	private final TreeMap<Integer, Integer> physicalIds;

	private final int capacity;

	public BiMap(final int numLogicIds) {
		this(numLogicIds, new TreeMap<>());
	}

	public BiMap(final int numLogicIds, final TreeMap<Integer, Node> nodesAddresses) {
		if (numLogicIds > 0) {
			this.capacity = numLogicIds;
			this.logicIds = new Integer[capacity];
			this.physicalIds = new TreeMap<>();
			for (final Entry<Integer, Node> entry : nodesAddresses.entrySet()) {
				addEntry(entry.getKey(), entry.getValue().getNodeId());
			}
		} else {
			throw new IllegalArgumentException("<< BiMap >> numLogicIds debe ser mayor a 0");
		}
	}

	public void addEntry(final int logicId, final int physicId) {
		if (logicId > 0 && logicId <= capacity) {
			logicIds[logicId - 1] = physicId;
			physicalIds.put(physicId, logicId);
		} else {
			throw new IllegalArgumentException("<< BiMap >> logicId incorrecto el valor debe estar entre 0 y " + capacity);
		}
	}

	public void removeByLogicId(final int logicId) {
		if (logicId > 0 && logicId <= capacity) {
			final Integer physicId = logicIds[logicId - 1];
			if (physicId != null) {
				logicIds[logicId - 1] = null;
				physicalIds.remove(physicId);
			}
		} else {
			throw new IllegalArgumentException("<< BiMap >> logicId incorrecto el valor debe estar entre 0 y " + capacity);
		}
	}

	public void removeByPhysicalId(final int physicId) {
		final Integer logicId = physicalIds.remove(physicId);
		if (logicId != null) {
			logicIds[logicId - 1] = null;
		}
	}

	public void clear() {
		for (int i = 0; i < capacity; i++) {
			logicIds[i] = null;
		}
		physicalIds.clear();
	}

	public Integer getPhysicalId(final int logicId) {
		return logicIds[logicId - 1];
	}

	public Integer getLogicId(final int physicId) {
		return physicalIds.get(physicId);
	}

	public static void main(String[] args) {
		final BiMap biMap = new BiMap(10);
		biMap.addEntry(1, 1592);
		biMap.addEntry(2, 58741);
		biMap.addEntry(4, 1515);
		for (int i = 0; i < 10; i++) {
			System.out.println("Logic : " + (i + 1) + " \t || value = " + biMap.getPhysicalId(i + 1));
		}
		biMap.clear();
		for (int i = 0; i < 10; i++) {
			System.out.println("Logic : " + (i + 1) + " \t || value = " + biMap.getPhysicalId(i + 1));
		}
	}

}
