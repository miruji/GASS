package gass.parser;

import gass.tokenizer.Token;
import gass.tokenizer.TokenType;
import java.util.ArrayList;

public class Expression {
    public final ArrayList<Token> value;
    public Expression(final ArrayList<Token> value) {
        this.value = value;
    }
    public ExpressionObject getValue(final Block block, final ArrayList<Block> blocks) {
        // parse expression
        final ArrayList<ExpressionObject> expressions = new ArrayList<>();
//        System.out.println(Token.tokensToString(value, true));
        for (int i = 0; i < value.size(); i++) {
            final Token currentToken = value.get(i);
            // read block assign
            if ((currentToken.type == TokenType.BLOCK_ASSIGN || currentToken.type == TokenType.FUNCTION_ASSIGN) && i+1 < value.size()) {
                if (value.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                    //
                    final Block blockAssign = Block.getBlock(blocks, currentToken.data);
                    if (blockAssign != null)
                        expressions.add(blockAssign.result.value);
                }
            } else
            // read variables
            if (currentToken.type == TokenType.VARIABLE_NAME) {
                final Variable variable = Block.getVariable(block, currentToken.data);
                final ExpressionObject variableValue = variable.getValue();
                //System.out.println("!>> "+variableValue);
                if (variableValue != null)
                    expressions.add(variableValue);
            } else
            // read numbers
            if (currentToken.type == TokenType.NUMBER || currentToken.type == TokenType.FLOAT)
                expressions.add(new ExpressionObject(ExpressionType.NUMBER, currentToken.data));
            else
            // read operators
            if (Token.checkOperator(currentToken.type))
                expressions.add(new ExpressionObject(ExpressionType.PLUS));
            //
//            System.out.println(currentToken.type);
        }
        // until this moment we could not know how many expression objects
        if (expressions.isEmpty())
            return null;
        if (expressions.size() >= 1 && expressions.size() < 3) // < 3 ok?
            return expressions.get(0);
        // parse expression > 3 objects
        for (int i = 1; i+1 < expressions.size();) {
            ExpressionObject current = expressions.get(i);
            if (current.type == ExpressionType.PLUS) {
                final ExpressionObject back = expressions.get(i-1);
                final ExpressionObject next = expressions.get(i+1);
                if (back.type == ExpressionType.NUMBER && next.type == ExpressionType.NUMBER) {
                    back.value = Integer.parseInt(back.value.toString()) + Integer.parseInt(next.value.toString());
                    expressions.remove(i);
                    expressions.remove(i);
                }
            }
        }
        // output
//        for (final ExpressionObject expression : expressions) {
//            System.out.println(">>> "+expression.type+": "+expression.value);
//        }
        return expressions.get(0);
    }
}
