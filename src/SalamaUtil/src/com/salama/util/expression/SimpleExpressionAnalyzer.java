package com.salama.util.expression;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * Calculate simple expression(Only number and string, supported operators are "+ - * / ()" ) 
 * e.g.: (1+2*3), ('AFD' == 'BBB')
 * 
 * @author XingGu Liu
 * 
 */
public class SimpleExpressionAnalyzer {
	/** Log */
	private static final Logger log = Logger
			.getLogger(SimpleExpressionAnalyzer.class);

	private static final String regexExpressionStr = "'[^']*'|[a-zA-Z]+[a-zA-Z0-9]*|[0-9]+[.{1}][0-9]+|[0-9]+|\\(|\\)|&&|\\|\\||==|!=|<=|>=|!|=|\\+|\\-|\\*|/|<|>";
	private static final String regexLiteralStr = "'[^']*'";
	private static final String regexVariableStr = "[a-zA-Z]+[a-zA-Z0-9]*\\.[a-zA-Z]+[a-zA-Z0-9]*\\.[a-zA-Z]+[a-zA-Z0-9]*\\.[a-zA-Z]+[a-zA-Z0-9]*|[a-zA-Z]+[a-zA-Z0-9]*\\.[a-zA-Z]+[a-zA-Z0-9]*\\.[a-zA-Z]+[a-zA-Z0-9]*|[a-zA-Z]+[a-zA-Z0-9]*\\.[a-zA-Z]+[a-zA-Z0-9]*|[a-zA-Z]+[a-zA-Z0-9]*";
	private static final String regexDoubleStr = "[0-9]+[.{1}][0-9]+";
	private static final String regexIntegerStr = "[0-9]+";
	private static final String regexOperatorStr = "\\(|\\)|&&|\\|\\||==|!=|<=|>=|!|=|\\+|\\-|\\*|/|<|>";

	private Pattern regexExpression;
	private Pattern regexLiteral;
	private Pattern regexVariable;
	private Pattern regexDouble;
	private Pattern regexInteger;
	private Pattern regexOperator;

	// work variable
	private int expressionType;
	private List<String> tokenList = new ArrayList<String>();
	private List<String> operatorStack = new ArrayList<String>();
	private List<Object> paramStack = new ArrayList<Object>();
	int operatorStackPosIndex, paramStackPosIndex;

	// 0: Hashmap 1: ValueStack
	//private int variableType = 0;
	//private HashMap<String, Object> variableStack;
	private IValueStack valueStack;
	//private Object objStackParam;

	public SimpleExpressionAnalyzer() throws MalformedPatternException {
		log.debug("regexExpression:" + regexExpression);

		PatternCompiler compiler = new Perl5Compiler();

		regexExpression = compiler.compile(regexExpressionStr);
		regexLiteral = compiler.compile(regexLiteralStr);
		regexVariable = compiler.compile(regexVariableStr);
		regexDouble = compiler.compile(regexDoubleStr);
		regexInteger = compiler.compile(regexIntegerStr);
		regexOperator = compiler.compile(regexOperatorStr);
	}

	public Object calcuExpressionValue(String expression, IValueStack variableStack) {
		this.valueStack = variableStack;
		return calcuExpressionValue(expression);
	}
	
	/**
	 * Calculate boolean expression
	 * 
	 * @param expression
	 * @param variableStack
	 * @return boolean value
	 */
	public boolean calcuExpressionBooleanValue(String expression, IValueStack variableStack) {
		return toBoolean(calcuExpressionValue(expression, variableStack));
	}

	public String calcuExpressionStringValue(String expression, IValueStack variableStack) {
		return (String) calcuExpressionValue(expression, variableStack);
	}
	
	public int calcuExpressionIntValue(String expression, IValueStack variableStack) {
		return toInt(calcuExpressionValue(expression, variableStack));
	}

	public double calcuExpressionDoubleValue(String expression, IValueStack variableStack) {
		return toDouble(calcuExpressionValue(expression, variableStack));
	}
	
	/**
	 * Calculate expression
	 * 
	 * @param expression:式
	 * @return String or Number type
	 */
	private Object calcuExpressionValue(String expression) {
		log.debug("expression:" + expression);

		if (expression == null || expression.equals(""))
			return true;

		// clear work variable
		tokenList.clear();
		operatorStack.clear();
		paramStack.clear();

		// make token list
		MatchResult matchResult = null;
		PatternMatcherInput matcherInput = new PatternMatcherInput(expression);
		PatternMatcher matcher = new Perl5Matcher();
		int iStart = 0;
		int iEnd = 0;

		while (matcher.contains(matcherInput, regexExpression)) {
			matchResult = matcher.getMatch();

			iStart = matchResult.beginOffset(0);
			iEnd = matchResult.endOffset(0);

			tokenList.add(expression.substring(iStart, iEnd));
		}

		return getSimpleExpressionValue();
	}

	private Object getSimpleExpressionValue() {
		int i;
		PatternMatcher matcher = new Perl5Matcher();

		String strTmp;

		operatorStackPosIndex = 0;
		paramStackPosIndex = 0;
		expressionType = 0;

		for (i = 0; i < tokenList.size(); i++) {
			strTmp = tokenList.get(i);
			if (strTmp.equals("") || strTmp.equals(" "))
				continue;

			if (matcher.matches(strTmp, regexLiteral)) {
				// literal const
				expressionType = 1;
				if (strTmp.equals("")) {
					paramStack.add(strTmp);
				} else {
					paramStack.add(strTmp.substring(1, strTmp.length() - 1));
				}
			} else if (matcher.matches(strTmp, regexVariable)) {
				/*
				if (variableType == 0) {
					paramStack.add(getVariableValue(variableStack, strTmp));
				} else {
					paramStack.add(getVariableValue(valueStack, objStackParam,
							strTmp));
				}
				*/
				paramStack.add(getVariableValue(valueStack, strTmp));
				
			} else if (matcher.matches(strTmp, regexInteger)) {
				paramStack.add(strTmp);
			} else if (matcher.matches(strTmp, regexDouble)) {
				paramStack.add(strTmp);
			} else if (matcher.matches(strTmp, regexOperator)) {
				operatorStack.add(strTmp);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("operatorStack:" + operatorStack.toString());
			log.debug("paramStack:" + paramStack.toString());
		}
		operatorStackPosIndex = 0;
		paramStackPosIndex = 0;

		return calcuExpNotRecursive(expressionType);
	}

	private static String getVariableValue(IValueStack variableStack,
			String variableName) {
		Object objVal = variableStack.findValue(variableName);

		if (objVal == null) {
			return "";
		} else {
			return objVal.toString();
		}
	}

	/**
	 * not recursive method, because recursive method may make the stack overflow
	 * @param expressionType
	 * @return
	 */
	private Object calcuExpNotRecursive(int expressionType) {
		int nextOptIndex = -1;
		int nextParamIndex = -1;
		int prevOptIndex = -1;
		int prevParamIndex = -1;

		while (true) {
			// scan from the current position
			// for (i=paramStackPosIndex; i<operatorStack.length; i++) {
			{
				if ((operatorStackPosIndex == -1) || (paramStackPosIndex == -1)) {
					log.debug("calcuExpNotRecursive() result:"
							+ paramStack.get(0).toString());
					return paramStack.get(0);
				}

				nextOptIndex = getNextOperatorIndex();
				nextParamIndex = getNextParamIndex();
				prevOptIndex = getPrevOperatorIndex();
				prevParamIndex = getPrevParamIndex();

				if (nextOptIndex == -1) {
					if (expressionType == 1) {
						calcuStringExpValue(prevOptIndex, prevParamIndex,
								nextOptIndex, nextParamIndex);
					} else {
						calcuNumberExpValue(prevOptIndex, prevParamIndex,
								nextOptIndex, nextParamIndex);
					}
					continue;
				} else if (operatorStack.get(operatorStackPosIndex).equals("(")) {
					if (operatorStack.get(nextOptIndex).toString().equals(")")) {

						operatorStack.set(nextOptIndex, null);
						operatorStack.set(operatorStackPosIndex, null);

						nextOptIndex = getNextOperatorIndex();
						nextParamIndex = getNextParamIndex();
						prevOptIndex = getPrevOperatorIndex();
						prevParamIndex = getPrevParamIndex();

//						operatorStackPosIndex = prevOptIndex;
//						paramStackPosIndex = prevParamIndex;
						operatorStackMoveBack(prevOptIndex, nextOptIndex, prevParamIndex);
						
						continue;
					} else {
						operatorStackPosIndex = nextOptIndex;
						continue;
					}
				} else if (getOptPriority(operatorStack
						.get(operatorStackPosIndex)) <= getOptPriority(operatorStack
						.get(nextOptIndex))) {
					if (expressionType == 1) {
						calcuStringExpValue(prevOptIndex, prevParamIndex,
								nextOptIndex, nextParamIndex);
					} else {
						calcuNumberExpValue(prevOptIndex, prevParamIndex,
								nextOptIndex, nextParamIndex);
					}
					continue;
				} else {
					operatorStackPosIndex = nextOptIndex;
					paramStackPosIndex = nextParamIndex;
					continue;
				}

			}// for loop

		}

	}

	private void calcuStringExpValue(int prevOptIndex, int prevParamIndex,
			int nextOptIndex, int nextParamIndex) {
		// log.debug("curOptIndex:" + operatorStackPosIndex + " curParamIndex:"
		// + paramStackPosIndex);

		if (operatorStack.get(operatorStackPosIndex).equals("!")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(!toBoolean(paramStack.get(paramStackPosIndex))));
			operatorStack.set(operatorStackPosIndex, null);
			paramStackPosIndex = prevParamIndex;
			operatorStackPosIndex = prevOptIndex;
			return;
		}

		if (operatorStack.get(operatorStackPosIndex).equals("+")) {
			paramStack.set(paramStackPosIndex, paramStack.get(
					paramStackPosIndex).toString()
					+ paramStack.get(nextParamIndex).toString());
		} else if (operatorStack.get(operatorStackPosIndex).equals("==")) {
			paramStack.set(paramStackPosIndex, Boolean.toString(paramStack.get(
					paramStackPosIndex).toString().equals(
					paramStack.get(nextParamIndex).toString())));
		} else if (operatorStack.get(operatorStackPosIndex).equals("<=")) {
			paramStack.set(paramStackPosIndex, Boolean.toString(paramStack.get(
					paramStackPosIndex).toString().compareTo(
					paramStack.get(nextParamIndex).toString()) <= 0));
		} else if (operatorStack.get(operatorStackPosIndex).equals(">=")) {
			paramStack.set(paramStackPosIndex, Boolean.toString(paramStack.get(
					paramStackPosIndex).toString().compareTo(
					paramStack.get(nextParamIndex).toString()) >= 0));
		} else if (operatorStack.get(operatorStackPosIndex).equals(">")) {
			paramStack.set(paramStackPosIndex, Boolean.toString(paramStack.get(
					paramStackPosIndex).toString().compareTo(
					paramStack.get(nextParamIndex).toString()) > 0));
		} else if (operatorStack.get(operatorStackPosIndex).equals("<")) {
			paramStack.set(paramStackPosIndex, Boolean.toString(paramStack.get(
					paramStackPosIndex).toString().compareTo(
					paramStack.get(nextParamIndex).toString()) < 0));
		} else if (operatorStack.get(operatorStackPosIndex).equals("||")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(toBoolean(paramStack.get(paramStackPosIndex))
							|| toBoolean(paramStack.get(nextParamIndex))));
		} else if (operatorStack.get(operatorStackPosIndex).equals("&&")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(toBoolean(paramStack.get(paramStackPosIndex))
							&& toBoolean(paramStack.get(nextParamIndex))));
		}

		// log.debug("calcuStringExpValue() "
		// + "" + operatorStack[operatorStackPosIndex]
		// + "" + paramStack[nextParamIndex]
		// + "->" + paramStack[paramStackPosIndex] );

		operatorStack.set(operatorStackPosIndex, null);
		paramStack.set(nextParamIndex, null);

//		if (prevOptIndex == -1) {
//			if(nextOptIndex == -1) {
//				operatorStackPosIndex = prevOptIndex;
//			} else {
//				operatorStackPosIndex = nextOptIndex;
//			}
//		} else if (operatorStack.get(prevOptIndex).equals("(")) {
//			operatorStackPosIndex = prevOptIndex;
//		} else {
//			operatorStackPosIndex = prevOptIndex;
//			paramStackPosIndex = prevParamIndex;
//		}
		operatorStackMoveBack(prevOptIndex, nextOptIndex, prevParamIndex);

		return;
	}

	private void calcuNumberExpValue(int prevOptIndex, int prevParamIndex,
			int nextOptIndex, int nextParamIndex) {
		// log.debug("curOptIndex:" + operatorStackPosIndex + " curParamIndex:"
		// + paramStackPosIndex);

		if (operatorStack.get(operatorStackPosIndex).equals("!")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(!toBoolean(paramStack.get(paramStackPosIndex))));
			operatorStack.set(operatorStackPosIndex, null);
			paramStackPosIndex = prevParamIndex;
			operatorStackPosIndex = prevOptIndex;
			return;
		}

		if (operatorStack.get(operatorStackPosIndex).equals("+")) {
			paramStack.set(paramStackPosIndex, parseFloat(paramStack.get(
					paramStackPosIndex).toString())
					+ parseFloat(paramStack.get(nextParamIndex).toString()));
		} else if (operatorStack.get(operatorStackPosIndex).equals("-")) {
			paramStack.set(paramStackPosIndex, parseFloat(paramStack.get(
					paramStackPosIndex).toString())
					- parseFloat(paramStack.get(nextParamIndex).toString()));
		} else if (operatorStack.get(operatorStackPosIndex).equals("*")) {
			paramStack.set(paramStackPosIndex, parseFloat(paramStack.get(
					paramStackPosIndex).toString())
					* parseFloat(paramStack.get(nextParamIndex).toString()));
		} else if (operatorStack.get(operatorStackPosIndex).equals("/")) {
			paramStack.set(paramStackPosIndex, parseFloat(paramStack.get(
					paramStackPosIndex).toString())
					/ parseFloat(paramStack.get(nextParamIndex).toString()));
		} else if (operatorStack.get(operatorStackPosIndex).equals("==")) {
			paramStack.set(paramStackPosIndex, Boolean.toString(paramStack.get(
					paramStackPosIndex).toString().equals(
					paramStack.get(nextParamIndex).toString())));
		} else if (operatorStack.get(operatorStackPosIndex).equals("<=")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(parseFloat(paramStack.get(paramStackPosIndex)
							.toString()) <= parseFloat(paramStack.get(
							nextParamIndex).toString())));
		} else if (operatorStack.get(operatorStackPosIndex).equals(">=")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(parseFloat(paramStack.get(paramStackPosIndex)
							.toString()) >= parseFloat(paramStack.get(
							nextParamIndex).toString())));
		} else if (operatorStack.get(operatorStackPosIndex).equals(">")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(parseFloat(paramStack.get(paramStackPosIndex)
							.toString()) > parseFloat(paramStack.get(
							nextParamIndex).toString())));
		} else if (operatorStack.get(operatorStackPosIndex).equals("<")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(parseFloat(paramStack.get(paramStackPosIndex)
							.toString()) < parseFloat(paramStack.get(
							nextParamIndex).toString())));
		} else if (operatorStack.get(operatorStackPosIndex).equals("||")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(toBoolean(paramStack.get(paramStackPosIndex))
							|| toBoolean(paramStack.get(nextParamIndex))));
		} else if (operatorStack.get(operatorStackPosIndex).equals("&&")) {
			paramStack.set(paramStackPosIndex, Boolean
					.toString(toBoolean(paramStack.get(paramStackPosIndex))
							&& toBoolean(paramStack.get(nextParamIndex))));
		}

		// log.debug("calcuNumberExpValue() "
		// + "" + operatorStack[operatorStackPosIndex]
		// + "" + paramStack[nextParamIndex]
		// + "->" + paramStack[paramStackPosIndex] );

		operatorStack.set(operatorStackPosIndex, null);
		paramStack.set(nextParamIndex, null);

		operatorStackMoveBack(prevOptIndex, nextOptIndex, prevParamIndex);
		
		return;
	}

	private void operatorStackMoveBack(int prevOptIndex, int nextOptIndex, int prevParamIndex) {
		if (prevOptIndex == -1) {
			if(nextOptIndex == -1) {
				operatorStackPosIndex = prevOptIndex;
			} else {
				operatorStackPosIndex = nextOptIndex;
			}
		} else if (getOptParamCnt(operatorStack.get(prevOptIndex)) == 1) {
			//1元操作符号
			operatorStackPosIndex = prevOptIndex;
		} else {
			//2元操作符号
			operatorStackPosIndex = prevOptIndex;
			paramStackPosIndex = prevParamIndex;
		}
	}
	
	private static double parseFloat(String strNum) {
		try {
			return Double.parseDouble(strNum);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static boolean toBoolean(Object obj) {
		if (obj.toString().toLowerCase().equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	private static int toInt(Object obj) {
		try {
			return Integer.parseInt(obj.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static double toDouble(Object obj) {
		try {
			return Double.parseDouble(obj.toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private int getNextOperatorIndex() {
		int nextOptIndex = -1;
		// get next operator
		for (int i = operatorStackPosIndex + 1; i < operatorStack.size(); i++) {
			if (operatorStack.get(i) != null) {
				nextOptIndex = i;
				break;
			}
		}

		return nextOptIndex;

	}

	private int getNextParamIndex() {
		int nextParamIndex = -1;
		for (int i = paramStackPosIndex + 1; i < paramStack.size(); i++) {
			if (paramStack.get(i) != null) {
				nextParamIndex = i;
				break;
			}
		}

		return nextParamIndex;
	}

	private int getPrevOperatorIndex() {
		int prevOptIndex = -1;
		for (int i = operatorStackPosIndex - 1; i >= 0; i--) {
			if (operatorStack.get(i) != null) {
				prevOptIndex = i;
				break;
			}
		}

		return prevOptIndex;
	}

	private int getPrevParamIndex() {
		int prevParamIndex = -1;
		for (int i = paramStackPosIndex - 1; i >= 0; i--) {
			if (paramStack.get(i) != null) {
				prevParamIndex = i;
				break;
			}
		}

		return prevParamIndex;
	}

	private static int getOptPriority(String operator) {
		if (operator.equals("(")) {
			return 1;
		} else if (operator.equals("!") || operator.equals("*")
				|| operator.equals("/")) {
			return 2;
		} else if (operator.equals("+") || operator.equals("-")) {
			return 3;
		} else if (operator.equals("==") || operator.equals("<=")
				|| operator.equals(">=") || operator.equals(">")
				|| operator.equals("<")) {
			return 4;
		} else if (operator.equals("&&") || operator.equals("||")) {
			return 5;
		} else {
			return 6;
		}
	}

	private static int getOptParamCnt(String operator) {
		if (operator.equals("(") || operator.equals(")") ) {
			return 1;
		} else if (operator.equals("!")) {
			return 1;
		} else if (operator.equals("*") || operator.equals("/") 
				|| operator.equals("+") || operator.equals("-")
				|| operator.equals("==") || operator.equals("<=")
				|| operator.equals(">=") || operator.equals(">")
				|| operator.equals("<") 
				|| operator.equals("&&") || operator.equals("||")
				) {
			return 2;
		} else {
			return 1;
		}
	}

}
