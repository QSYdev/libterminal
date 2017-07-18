package ar.com.qsy.model.objects;

import java.util.LinkedList;

public final class Step {

	private final String expression;
	private final long timeOut;
	private final LinkedList<NodeConfiguration> nodesConfiguration;

	public Step(final LinkedList<NodeConfiguration> nodesConfiguration, final long timeOut, final String expression) {
		this.expression = expression;
		this.timeOut = timeOut;
		this.nodesConfiguration = nodesConfiguration;
	}

	public String getExpression() {
		return expression;
	}

	public long getTimeOut() {
		return timeOut;
	}

	public LinkedList<NodeConfiguration> getNodesConfiguration() {
		return nodesConfiguration;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("EXPRESSION = " + expression + " || TIMEOUT = " + timeOut + "\n");
		for (final NodeConfiguration nodeConfig : nodesConfiguration) {
			sb.append(nodeConfig + "\n");
		}
		return sb.toString();
	}

}
