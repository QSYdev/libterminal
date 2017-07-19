package ar.com.qsy.model.objects;

import java.util.Set;
import java.util.Stack;

import ar.com.qsy.model.utils.Utils;

public final class ExpressionTree {

	private final ExpressionNode expressionRootNode;

	public ExpressionTree(final String expression) throws IllegalArgumentException {
		this.expressionRootNode = buildExpressionTree(expression);
	}

	private static ExpressionNode buildExpressionTree(final String expression) throws IllegalArgumentException {
		final Stack<ExpressionNode> stack = new Stack<>();
		final int[] exp = Utils.fromInfixToPostfix(expression);
		for (final int value : exp) {
			if (value == Utils.AND_INT_VALUE || value == Utils.OR_INT_VALUE) {
				stack.push(new ExpressionNode(value, stack.pop(), stack.pop()));
			} else {
				stack.push(new ExpressionNode(value));
			}
		}
		return stack.pop();
	}

	public boolean evaluateExpressionTree(boolean[] nodeIds) {
		return evaluateExpressionTree(expressionRootNode, nodeIds);
	}

	private boolean evaluateExpressionTree(ExpressionNode node, boolean[] nodeIds) {
		if (node.isLeaf()) {
			return nodeIds[node.getValue()];
		} else {
			switch (node.getValue()) {
			case Utils.AND_INT_VALUE: {
				return evaluateExpressionTree(node.getLeft(),nodeIds) && evaluateExpressionTree(node.getRight(),nodeIds);
			}
			case Utils.OR_INT_VALUE: {
				return evaluateExpressionTree(node.getLeft(),nodeIds) || evaluateExpressionTree(node.getRight(),nodeIds);
			}
			default: {
				return false;
			}
			}
		}
	}

	private static class ExpressionNode {

		private final int value;
		private final ExpressionNode right;
		private final ExpressionNode left;

		public ExpressionNode(final int value) {
			this(value, null, null);
		}

		public ExpressionNode(final int value, final ExpressionNode right, final ExpressionNode left) {
			this.value = value;
			this.right = right;
			this.left = left;
		}

		public int getValue() {
			return value;
		}

		public ExpressionNode getRight() {
			return right;
		}

		public ExpressionNode getLeft() {
			return left;
		}

		public boolean isLeaf() {
			return right == null && left == null;
		}

	}

}
