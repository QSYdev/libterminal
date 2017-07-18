package ar.com.qsy.model.objects;

import java.util.ArrayList;

public final class Routine {

	private final byte numberOfNodes;
	private final ArrayList<Step> steps;

	public Routine(final byte numberOfNodes, final ArrayList<Step> steps) {
		this.numberOfNodes = numberOfNodes;
		this.steps = steps;
	}

	public byte getNumberOfNodes() {
		return numberOfNodes;
	}

	public ArrayList<Step> getSteps() {
		return steps;
	}

}
