package gass.parser;

import gass.tokenizer.Token;
import gass.tokenizer.TokenType;
import java.util.ArrayList;

public class Expression {
    public final ArrayList<Token> value;
    public ExpressionObject valueResult;
    public Expression(final ArrayList<Token> value) {
        this.value = value;
    }
    /** TokenType to ExpressionType */
    public static ExpressionType getType(final TokenType type) {
        if (type == TokenType.PLUS) return ExpressionType.PLUS;
        if (type == TokenType.MINUS) return ExpressionType.MINUS;
        if (type == TokenType.MULTIPLY) return ExpressionType.MULTIPLY;
        if (type == TokenType.DIVIDE) return ExpressionType.DIVIDE;
        return ExpressionType.NONE;
    }
    /** calculate value */
    public static ExpressionObject calculate(final ArrayList<ExpressionObject> expressions) {
        for (int i = 1; i+1 < expressions.size();) {
            final ExpressionObject current = expressions.get(i);
            final ExpressionObject back = expressions.get(i-1);
            final ExpressionObject next = expressions.get(i+1);

            if (back.type == ExpressionType.NUMBER && next.type == ExpressionType.NUMBER) {
                final int backValue = Integer.parseInt(back.value.toString());
                final int nextValue = Integer.parseInt(next.value.toString());

                if (current.type == ExpressionType.PLUS)
                    back.value = backValue + nextValue;
                else if (current.type == ExpressionType.MINUS)
                    back.value = backValue - nextValue;
                else if (current.type == ExpressionType.MULTIPLY)
                    back.value = backValue * nextValue;
                else if (current.type == ExpressionType.DIVIDE)
                    back.value = backValue / nextValue;

                expressions.remove(i); // remove operator
                expressions.remove(i); // remove next
            } else i++; // skip
        }

        return expressions.get(0);
    }
    /** get expression value */
    public ExpressionObject getValue(ArrayList<Token> value, final Block block, final ArrayList<Block> blocks) {
        if (valueResult != null) return valueResult;
        // parse expression
        final ArrayList<ExpressionObject> expressions = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            final Token currentToken = value.get(i);
            // read circle bracket
            if (currentToken.type == TokenType.CIRCLE_BLOCK_BEGIN && currentToken.childrens != null && !currentToken.childrens.isEmpty()) {
                expressions.add(getValue(currentToken.childrens, block, blocks));
            } else
            // read block assign
            if (currentToken.type == TokenType.BLOCK_ASSIGN || currentToken.type == TokenType.FUNCTION_ASSIGN) {
                // global func with parameters
                if (i+1 < value.size() && value.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                    i++;
                    final Block blockAssign = Block.getBlock(currentToken.data, blocks);
                    if (blockAssign != null) expressions.add(blockAssign.result.value);
                // local func with no parameters
                } else {
                    final Block blockAssign = Block.getBlock(currentToken.data, block.localBlocks);
                    if (blockAssign != null) expressions.add(blockAssign.result.value);
                }
            } else
            // read variables
            if (currentToken.type == TokenType.VARIABLE_NAME) {
                final String[] variableAssign = currentToken.data.split(":");

                final Variable variable;
                if (variableAssign.length == 1) {
                    variable = block.getVariable(variableAssign[0], blocks);
                    if (variable.resultValue == null) variable.setValue(block, blocks);
                }
                else {
                    final Block b = block.getVariableBlock(variableAssign[0], blocks);
                    variable = b.variables.get( Integer.parseInt(variableAssign[1]) );
                    variable.setValue(b, blocks);
                }

                if (variable.resultValue != null) {
                    final ExpressionObject variableValue = new ExpressionObject(variable.resultValue.type, variable.resultValue.value);
                    expressions.add(variableValue);
                }
            } else
            // read numbers
            if (currentToken.type == TokenType.NUMBER || currentToken.type == TokenType.FLOAT)
                expressions.add(new ExpressionObject(ExpressionType.NUMBER, currentToken.data));
            else
            // read operators
            if (Token.checkOperator(currentToken.type))
                expressions.add(new ExpressionObject(getType(currentToken.type)));
        }
        // until this moment we could not know how many expression objects
        if (expressions.isEmpty())
            // null
            valueResult = null;
        else
        if (expressions.size() < 3)
            // < 3
            valueResult = expressions.get(0);
        else
            // parse expression > 3 objects
            valueResult = calculate(expressions);
        return valueResult;
    }
}
