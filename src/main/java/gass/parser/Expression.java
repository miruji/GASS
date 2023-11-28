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
        // logical
        if (type == TokenType.GREATER_THAN) return ExpressionType.GREATER_THAN;
        if (type == TokenType.LESS_THAN) return ExpressionType.LESS_THAN;
        // math
        if (type == TokenType.PLUS) return ExpressionType.PLUS;
        if (type == TokenType.MINUS) return ExpressionType.MINUS;
        if (type == TokenType.MULTIPLY) return ExpressionType.MULTIPLY;
        if (type == TokenType.DIVIDE) return ExpressionType.DIVIDE;
        return ExpressionType.NONE;
    }
    /** calculate value */
    public static ExpressionObject calculate(final ArrayList<ExpressionObject> expressions) {
        // logical
        for (int i = 1; i+1 < expressions.size();) {
            final ExpressionObject current = expressions.get(i);
            final ExpressionObject back = expressions.get(i-1);
            final ExpressionObject next = expressions.get(i+1);

            if (back.type == ExpressionType.NUMBER && next.type == ExpressionType.NUMBER) {
                final int backValue = Integer.parseInt(back.value.toString());
                final int nextValue = Integer.parseInt(next.value.toString());

                if (current.type == ExpressionType.GREATER_THAN)
                    back.value = (backValue > nextValue) ? 1 : 0;
                else if (current.type == ExpressionType.LESS_THAN)
                    back.value = (backValue < nextValue) ? 1 : 0;
                else {
                    i++;
                    continue;
                }
            } else {
                i++;
                continue;
            }

            expressions.remove(i); // remove operator
            expressions.remove(i); // remove next
        }
        // multiply and divide
        for (int i = 1; i+1 < expressions.size();) {
            final ExpressionObject current = expressions.get(i);
            final ExpressionObject back = expressions.get(i-1);
            final ExpressionObject next = expressions.get(i+1);

            if (back.type == ExpressionType.NUMBER && next.type == ExpressionType.NUMBER) {
                final int backValue = Integer.parseInt(back.value.toString());
                final int nextValue = Integer.parseInt(next.value.toString());

                if (current.type == ExpressionType.MULTIPLY)
                    back.value = backValue * nextValue;
                else if (current.type == ExpressionType.DIVIDE)
                    back.value = backValue / nextValue;
                else {
                    i++;
                    continue;
                }
            } else {
                i++;
                continue;
            }

            expressions.remove(i); // remove operator
            expressions.remove(i); // remove next
        }
        // plus and minus
        for (int i = 1; i+1 < expressions.size();) {
            final ExpressionObject current = expressions.get(i);
            final ExpressionObject back = expressions.get(i-1);
            final ExpressionObject next = expressions.get(i+1);

            // NUMBER - NUMBER
            if (back.type == ExpressionType.NUMBER && next.type == ExpressionType.NUMBER) {
                final int backValue = Integer.parseInt(back.value.toString());
                final int nextValue = Integer.parseInt(next.value.toString());

                if (current.type == ExpressionType.PLUS)
                    back.value = backValue + nextValue;
                else if (current.type == ExpressionType.MINUS)
                    back.value = backValue - nextValue;
                else {
                    i++;
                    continue;
                }
            } else
            // STRING - NUMBER
            if (back.type == ExpressionType.STRING && next.type == ExpressionType.NUMBER) {
                final String backValue = back.value.toString();
                final int nextValue = Integer.parseInt(next.value.toString());

                if (current.type == ExpressionType.PLUS)
                    back.value = backValue + nextValue;
                else {
                    i++;
                    continue;
                }
            } else
            // NUMBER - STRING
            if (back.type == ExpressionType.NUMBER && next.type == ExpressionType.STRING) {
                final int backValue = Integer.parseInt(back.value.toString());
                final String nextValue = next.value.toString();

                if (current.type == ExpressionType.PLUS)
                    back.value = backValue + nextValue;
                else {
                    i++;
                    continue;
                }
            } else {
                i++;
                continue;
            }

            expressions.remove(i); // remove operator
            expressions.remove(i); // remove next
        }

        return expressions.get(0);
    }
    /** get expression value */
    public ExpressionObject getValue(final ArrayList<Token> value, final Block block, final ArrayList<Block> blocks) {
        //if (valueResult != null) return valueResult;
        // parse expression
        final ArrayList<ExpressionObject> expressions = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            final Token currentToken = value.get(i);
            // read circle bracket
            if (currentToken.type == TokenType.CIRCLE_BLOCK_BEGIN && currentToken.childrens != null && !currentToken.childrens.isEmpty()) {
                expressions.add(getValue(currentToken.childrens, block, blocks));
            } else
            // read block assign
            if (currentToken.type == TokenType.PROCEDURE_CALL) {
                // TO:DO: exception
            } else
            if (currentToken.type == TokenType.BLOCK_CALL || currentToken.type == TokenType.FUNCTION_CALL) {
                // global func with parameters
                if (i+1 < value.size() && value.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                    i++;
                    final Block blockAssign = Block.getBlock(currentToken.data, blocks);
                    if (blockAssign != null) {
                        final ArrayList<ArrayList<Token>> parametersBuffer = Token.separateTokens(TokenType.COMMA, value.get(i).childrens);
                        for (int j = 0; j < parametersBuffer.size(); j++) {
                            final ArrayList<Token> parameter = parametersBuffer.get(j);
                            blockAssign.parameters.get(j).value = new Expression(parameter);
                            blockAssign.parameters.get(j).setValue(block, blocks);
                        }
                        blockAssign.parseBlock(blocks);
                        expressions.add(blockAssign.result.value);
                    }
                // local func with no parameters
                } else {
                    final Block blockAssign = Block.getBlock(currentToken.data, block.localBlocks);
                    if (blockAssign != null) {
                        blockAssign.parseBlock(blocks);
                        expressions.add(blockAssign.result.value);
                    }
                }
            } else
            // read parameters
            if (currentToken.type == TokenType.PARAMETER_NAME) {
                final String[] blockInfo = currentToken.data.split("~");
                final Block findBlock = Block.findBlock(blockInfo[0], blocks);
                Variable parameter;

                if (blockInfo.length == 1) parameter = block.findParameter(blockInfo[0]);
                else                       parameter = findBlock.findParameter(blockInfo[1]);
                parameter.resultValue = null; // if parameter -> then calculate new value everyone
                parameter.setValue(block, blocks);

                if (parameter.resultValue != null) {
                    final ExpressionObject variableValue = new ExpressionObject(parameter.resultValue.type, parameter.resultValue.value);
                    expressions.add(variableValue);
                }
            } else
            // read variables
            if (currentToken.type == TokenType.VARIABLE_NAME) {
                final String[] blockInfo = currentToken.data.split("~");
                final String[] variableInfo = blockInfo[1].split(":");

                Variable variable;
                if (variableInfo.length == 1) {
                    variable = block.getVariable(variableInfo[0], blocks);
                    if (variable.resultValue == null) variable.setValue(block, blocks);
                }
                else {
                    int variableIndex = Integer.parseInt(variableInfo[1]);
                    if (variableIndex == -1) {
                        variable = Block.findVariableInBlocks(variableInfo[0], block, blocks);
                    } else {
                        variable = block.variables.get(variableIndex);
                        variable.setValue(block, blocks);
                    }
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
            if (currentToken.type == TokenType.SINGLE_QUOTE)
                expressions.add(new ExpressionObject(ExpressionType.CHAR, currentToken.data));
            else
            if (currentToken.type == TokenType.DOUBLE_QUOTE || currentToken.type == TokenType.BACK_QUOTE)
                expressions.add(new ExpressionObject(ExpressionType.STRING, currentToken.data));
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
