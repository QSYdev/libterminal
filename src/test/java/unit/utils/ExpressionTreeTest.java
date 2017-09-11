package unit.utils;

import libterminal.utils.ExpressionTree;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnitPlatform.class)
public class ExpressionTreeTest {
	private ExpressionTree expressionTree;
	private final String UNEXPECTED_FALSE = "La evaluacion de la expresion deberia indicar que termino";
	private final String UNEXPECTED_TRUE = "La evaluacion de la expresion deberia indicar que no termino";
	private final String SIMPLE_EXPRESSION = "1";
	private final String OR_COMPOSED_EXPRESSION = "(1&2)|(3&4)";
	private final String AND_COMPOSED_EXPRESSION = "(1|3)&(2|4)";
	private boolean[] touchedNodes;
	private final int MAX_NODES = 4;

	@ClassTest
	public void evaluateExpressionTree() {
		// SIMPLE STEP
		touchedNodes = new boolean[2];
		expressionTree = new ExpressionTree(SIMPLE_EXPRESSION);
		assertEquals(false, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_TRUE);
		touchedNodes[1] = true;
		assertEquals(true, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_FALSE);

		// OR COMPOSED STEP
		touchedNodes = new boolean[MAX_NODES+1];
		expressionTree = new ExpressionTree(OR_COMPOSED_EXPRESSION);
		assertEquals(false, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_TRUE);
		touchedNodes[1] = true; touchedNodes[3] = true;
		assertEquals(false, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_TRUE);
		touchedNodes[2] = true;
		assertEquals(true, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_FALSE);

		// AND COMPOSED STEP
		touchedNodes = new boolean[MAX_NODES+1];
		expressionTree = new ExpressionTree(AND_COMPOSED_EXPRESSION);
		assertEquals(false, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_TRUE);
		touchedNodes[3] = true;
		assertEquals(false, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_TRUE);
		touchedNodes[1] = true;
		assertEquals(false, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_TRUE);
		touchedNodes[2] = true;
		assertEquals(true, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_FALSE);
		touchedNodes[4] = true;
		assertEquals(true, expressionTree.evaluateExpressionTree(touchedNodes), UNEXPECTED_FALSE);
	}

}