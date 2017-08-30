package libterminal.utils;

import java.util.Map.Entry;
import java.util.TreeMap;

public final class BiMap {

	private final Integer[] logicalIds;
	private final TreeMap<Integer, Integer> physicalIds;

	private final int capacity;

	public BiMap(final int numLogicIds) {
		this(numLogicIds, new TreeMap<Integer, Integer>());
	}

	public BiMap(final int numLogicalIds, final TreeMap<Integer, Integer> nodesAddresses) {
		if (numLogicalIds > 0) {
			this.capacity = numLogicalIds;
			this.logicalIds = new Integer[capacity];
			this.physicalIds = new TreeMap<>();
			for (final Entry<Integer, Integer> entry : nodesAddresses.entrySet()) {
				addEntry(entry.getKey(), entry.getValue());
			}
		} else {
			throw new IllegalArgumentException("<< BiMap >> numLogicIds debe ser mayor a 0");
		}
	}

	public void addEntry(final int logicalId, final int physicId) {
		if (logicalId > 0 && logicalId <= capacity) {
			logicalIds[logicalId - 1] = physicId;
			physicalIds.put(physicId, logicalId);
		} else {
			throw new IllegalArgumentException("<< BiMap >> logicId incorrecto el valor debe estar entre 0 y " + capacity);
		}
	}

	public void removeByLogicalId(final int logicalId) {
		if (logicalId > 0 && logicalId <= capacity) {
			final Integer physicId = logicalIds[logicalId - 1];
			if (physicId != null) {
				logicalIds[logicalId - 1] = null;
				physicalIds.remove(physicId);
			}
		} else {
			throw new IllegalArgumentException("<< BiMap >> logicId incorrecto el valor debe estar entre 0 y " + capacity);
		}
	}

	public void removeByPhysicalId(final int physicalId) {
		final Integer logicalId = physicalIds.remove(physicalId);
		if (logicalId != null) {
			logicalIds[logicalId - 1] = null;
		}
	}

	public void clear() {
		for (int i = 0; i < capacity; i++) {
			logicalIds[i] = null;
		}
		physicalIds.clear();
	}

	public Integer getPhysicalId(final int logicalId) {
		return logicalIds[logicalId - 1];
	}

	public Integer getLogicalId(final int physicalId) {
		return physicalIds.get(physicalId);
	}

}
