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
    public static ExpressionType getType(final TokenType type) {
        if (type == TokenType.PLUS) return ExpressionType.PLUS;
        if (type == TokenType.MINUS) return ExpressionType.MINUS;
        if (type == TokenType.MULTIPLY) return ExpressionType.MULTIPLY;
        if (type == TokenType.DIVIDE) return ExpressionType.DIVIDE;
        return ExpressionType.NONE;
    }
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
    public ExpressionObject getValue(final Block block, final ArrayList<Block> blocks) {
        if (valueResult != null) return valueResult;
        // parse expression
        final ArrayList<ExpressionObject> expressions = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            final Token currentToken = value.get(i);
            // read block assign
            if (currentToken.type == TokenType.BLOCK_ASSIGN || currentToken.type == TokenType.FUNCTION_ASSIGN) {
                if (i+1 < value.size() && value.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) { // global func with parameters
                    final Block blockAssign = Block.getBlock(blocks, currentToken.data);
                    if (blockAssign != null)
                        expressions.add(blockAssign.result.value);
                } else { // local func with no parameters
                    final Block blockAssign = Block.getBlock(block.localBlocks, currentToken.data);
                    if (blockAssign != null)
                        expressions.add(blockAssign.result.value);
                }
            } else
            // read variables
            if (currentToken.type == TokenType.VARIABLE_NAME) {
                String[] variableAssign = currentToken.data.split(":");

                final Variable variable;
                if (variableAssign.length == 1)
                    variable = block.getVariable(variableAssign[0]);
                else
                    variable = block.variables.get( Integer.parseInt(variableAssign[1]) );

                if (variable != null && variable.getValue() != null) {
                    final ExpressionObject variableValue = new ExpressionObject(variable.getValue().type, variable.getValue().value);
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
            //
            //System.out.println(currentToken.type);
        }
        // until this moment we could not know how many expression objects
        if (expressions.isEmpty()) {
            valueResult = null;
            return null;
        }
        if (expressions.size() < 3) { // < 3 ok?
            valueResult = expressions.get(0);
            return valueResult;
        }
        // parse expression > 3 objects
        valueResult = calculate(expressions);
        return valueResult;
    }
}
