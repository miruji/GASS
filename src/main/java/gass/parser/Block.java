package gass.parser;

import gass.io.log.Log;
import gass.io.log.LogType;
import gass.tokenizer.Token;
import gass.tokenizer.TokenType;

import java.io.*;
import java.util.*;

public class Block implements Serializable {
    <T extends Object> T copyObject(T sourceObject) {

        T copyObject = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(sourceObject);
            objectOutputStream.flush();
            objectOutputStream.close();
            byteArrayOutputStream.close();
            byte[] byteData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteData);
            try {
                copyObject = (T) new ObjectInputStream(byteArrayInputStream).readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyObject;
    }
    public String name;                       // block name
    public BlockType type;                    // block type
    public ArrayList<Variable> parameters;    // block parameters
    public ArrayList<ArrayList<Token>> lines; // block tokens
    public ArrayList<Block> localBlocks;      // local blocks
    public ArrayList<Variable> variables;     // block variables
    public BlockResult result;                // result
    /** global block with parameters */
    public Block(final String name, final BlockType type, final ArrayList<Token> parameters, final ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        addParameters(parameters);

        lines = new ArrayList<>();
        lines.add(tokens);
    }
    /** global block with no parameters */
    public Block(final String name, final BlockType type, final ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;

        lines = new ArrayList<>();
        lines.add(tokens);
    }
    /** find block by name */
    public static Block findBlock(final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty() || blocks == null || blocks.isEmpty()) return null;

        // find main block
        final String[] findNameBuffer = findName.split(":");
        Block findBlock = null;
        for (final Block block : blocks) {
            if (Objects.equals(findNameBuffer[0], block.name)) {
                if (findNameBuffer.length == 1) return block; // if name only
                findBlock = block;
                break;
            }
        }

        // find by name
        if (findBlock == null) return null;
        for (int i = 1; i < findNameBuffer.length; i++) {
            findBlock = findBlock.localBlocks.get( Integer.parseInt(findNameBuffer[i]) );
            if (Objects.equals(findBlock.name, findName))
                return findBlock; // return local block
        }

        return null;
    }
    /** find varible by name */
    public static Variable findVariable(final String findName, final Block block) {
        if (findName == null || findName.isEmpty() || block == null || block.variables == null || block.variables.isEmpty())
            return null;

        for (int i = block.variables.size()-1; i >= 0; i--){
            final Variable variable = block.variables.get(i);
            if (Objects.equals(variable.name, findName))
                return variable;
        }

        return null;
    }
    /** find varible by name in blocks */
    public static Variable findVariableInBlocks(final String findName, final Block currentBlock, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty() || currentBlock == null || blocks == null || blocks.isEmpty()) return null;
        String[] findNameBuffer = currentBlock.name.split(":");

        // find current block -> up to max global
        final ArrayList<Block> buffer = new ArrayList<>();
        for (int i = 0; i < findNameBuffer.length; i++) {
            buffer.add( findBlock(String.join(":", findNameBuffer), blocks) );
            findNameBuffer = Arrays.copyOfRange(findNameBuffer, 0, findNameBuffer.length-1);
        }
        Collections.reverse(buffer);
        if (buffer.size() > 1)
            for (final Block b : buffer) {
                final Variable result = findVariable(findName, b);
                if (result != null) return result;
            }

        // find in others global blocks
        for (final Block b : blocks) {
            final Variable result = findVariable(findName, b);
            if (result != null) return result;
        }

        return null;
    }
    /** find blocks by names */
    public static ArrayList<Block> findBlocks(final ArrayList<String> findNames, final ArrayList<Block> blocks) {
        if (findNames == null || findNames.isEmpty() || blocks == null || blocks.isEmpty()) return null;

        final ArrayList<Block> result = new ArrayList<>();
        for (final String findName : findNames)
            result.add( findBlock(findName, blocks) );

        return result;
    }
    /** add parameters */
    private void addParameters(final ArrayList<Token> parameters) {
        if (parameters == null || parameters.isEmpty()) return;

        this.parameters = new ArrayList<>();
        for (Token parameter : parameters) {
            // if there are more advanced parameter designs,
            // then change the search here to separate by commas
            // -> use Token.separateTokens()
            if (parameter.type == TokenType.WORD) {
                this.parameters.add(new Variable(parameter.data, ExpressionType.NONE, name));
            }
        }
    }
    /** find parameter */
    public Variable findParameter(final String findName) {
        if (findName == null || findName.isEmpty()) return null;
        // parameters
        if (parameters != null && !parameters.isEmpty())
            for (int i = 0; i < parameters.size(); i++) {
                final Variable findParameter = parameters.get(i);
                if (Objects.equals(findParameter.name, findName))
                    return findParameter;
            }
        return null;
    }
    public int findParameterIndex(final String findName) {
        if (findName == null || findName.isEmpty()) return -1;
        // parameters
        if (parameters != null && !parameters.isEmpty())
            for (int i = 0; i < parameters.size(); i++) {
                if (Objects.equals(parameters.get(i).name, findName))
                    return i;
            }
        return -1;
    }
    /** add local block */
    public void addLocalBlock(final Block localBlock) {
        if (localBlock == null) return;
        if (localBlocks == null) localBlocks = new ArrayList<>();
        localBlocks.add(localBlock);
    }
    /** get blocks */
    public static ArrayList<Block> getBlocks(final ArrayList<String> findNames, final ArrayList<Block> blocks) {
        if (findNames == null || findNames.isEmpty() || blocks == null || blocks.isEmpty()) return null;

        final ArrayList<Block> result = new ArrayList<>();
        for (final String findName : findNames) {
            final Block findBlock = getBlock(findName, blocks);
            if (findBlock != null) result.add(findBlock);
        }
        return result;
    }
    /** get block by name */
    public static Block getBlock(final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty() || blocks == null || blocks.isEmpty()) return null;

        final boolean type = Character.isDigit(findName.charAt(0));
        for (int i = 0; i < blocks.size(); i++) {
            final Block findBlock = blocks.get(i);
            if (!type && Objects.equals(findBlock.name, findName)) // find global block by name
                return findBlock;
            else if (Objects.equals(String.valueOf(i), findName)) // find noname local block
                return findBlock;
        }
        return null;
    }
    /** add variable */
    public void addVariable(final Variable variable) {
        if (variable == null) return;
        if (variables == null) variables = new ArrayList<>();

        // no check exist
        // each variable has its own declaration order

        variables.add( variable );
    }
    /** find variable */
    public Variable findVariable(final String findName, final ArrayList<Variable> variables) {
        if (findName == null || findName.isEmpty()) return null;
        // variables
        if (variables != null && !variables.isEmpty())
            for (int i = variables.size()-1; i >= 0; i--) {
                final Variable findVariable = variables.get(i);
                if (Objects.equals(findVariable.name, findName))
                    return findVariable;
            }
        return null;
    }
    public int findVariableIndex(final boolean endStart, final String findName, final ArrayList<Variable> variables) {
        if (findName == null || findName.isEmpty()) return -1;
        // variables
        if (variables != null && !variables.isEmpty()) {
            if (endStart)
                for (int i = variables.size()-1; i >= 0; i--) {
                    if (Objects.equals(variables.get(i).name, findName))
                        return i;
                }
            else
                for (int i = 0; i < variables.size(); i++) {
                    if (Objects.equals(variables.get(i).name, findName))
                        return i;
                }
        }
        return -1;
    }
    /** get variable num by name */
    public int getVariableIndex(final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty()) return -1;

        int index = findVariableIndex(true, findName, variables);
        if (index != -1) return index;

        final Block upBlock = getBlock(name.split(":")[0], blocks);
        index = findVariableIndex(true, findName, upBlock.variables);
        if (index != -1) return index;

        if (upBlock.localBlocks != null && !upBlock.localBlocks.isEmpty())
            for (Block localBlock : upBlock.localBlocks) {
                if (localBlock.variables == null) break;
                index = findVariableIndex(true, findName, localBlock.variables);
                if (index != -1) return index;
            }

        return -1;
    }
    /** get variable block by name */
    public Block getVariableBlock(final boolean firstReadThis, final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty()) return null;

        Variable variable;
        if (firstReadThis) {
            variable = findVariable(findName, variables);
            if (variable != null) return this;
        }

        final Block upBlock = getBlock(name.split(":")[0], blocks);
        variable = findVariable(findName, upBlock.variables);
        if (variable != null) return upBlock;

        if (upBlock.localBlocks != null && !upBlock.localBlocks.isEmpty())
            for (Block localBlock : upBlock.localBlocks) {
                if (localBlock.variables == null) break;
                variable = findVariable(findName, localBlock.variables);
                if (variable != null) return localBlock;
            }

        if (!firstReadThis) {
            variable = findVariable(findName, variables);
            if (variable != null) return this;
        }

        return null;
    }
    /** get variable by name */
    public Variable getVariable(final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty()) return null;

        Variable variable = findVariable(findName, variables);
        if (variable != null) return variable;

        final Block upBlock = getBlock(name.split(":")[0], blocks);
        variable = findVariable(findName, upBlock.variables);
        if (variable != null) return variable;

        if (upBlock.localBlocks != null && !upBlock.localBlocks.isEmpty())
            for (Block localBlock : upBlock.localBlocks) {
                if (localBlock.variables == null) break;
                variable = findVariable(findName, localBlock.variables);
                if (variable != null) return variable;
            }

        return null;
    }
    public Variable getVariable(final String findName) {
        if (findName == null || findName.isEmpty() || variables == null || variables.isEmpty()) return null;

        for (final Variable findVariable : variables)
            if (Objects.equals(findVariable.name, findName))
                return findVariable;

        return null;
    }
    /** local blocks tree output */
    public static String outputLocalBlocks(final Block block, final int depth) {
        final StringBuilder output = new StringBuilder();

        final String repeat = "\t".repeat(Math.max(0, depth));
        final String repeat2 = repeat+"\t";
        final String repeat3 = repeat2+"\t";

        // block info
        output.append(repeat).append(block.type).append(" [").append(block.name).append("]:\n");

        // block tokens info
        if (block.lines != null && !block.lines.isEmpty()) {
            output.append(repeat2).append("Lines:\n");
            for (final ArrayList<Token> line : block.lines) {
                if (line == null || line.isEmpty()) continue;
                for (final Token token : line) {
                    output.append(Token.outputChildrens(token, depth+2));
                }
                output.append(repeat3).append(".\n");
            }
            output.append(repeat2).append("~\n");
        }

        // local blocks info
        if (block.localBlocks != null && !block.localBlocks.isEmpty()) {
            output.append(repeat2).append("Local blocks:\n");
            for (int i = 0; i < block.localBlocks.size(); i++)
                output.append(outputLocalBlocks(block.localBlocks.get(i), depth+2));
            output.append(repeat2).append("~\n");
        }

        // dependency blocks info
        if (block.variables != null && !block.variables.isEmpty()) {
            output.append(repeat2).append("Variables:\n");
            for (int i = 0; i < block.variables.size(); i++) {
                final Variable v = block.variables.get(i);
                if (v.value == null)
                    output.append(repeat3).append("[").append(v.blockName).append('~').append(v.name).append(':').append(i).append("]\n");
                else
                    output.append(repeat3).append("[").append(v.blockName).append('~').append(v.name).append(':').append(i).append("] =")
                          .append(" [").append( Token.tokensToString(v.value.value, true) ).append("]")
                          .append(" -> [").append( v.resultValue != null ? v.resultValue.value : "" ).append("]\n");
            }
            output.append(repeat2).append("~\n");
        }

        // block parameters
        if (block.parameters != null && !block.parameters.isEmpty()) {
            output.append(repeat2).append("Parameters:\n");
            for (int i = 0; i < block.parameters.size(); i++) {
                final Variable p = block.parameters.get(i);
                if (p.value == null)
                    output.append(repeat3).append("[").append(p.blockName).append('~').append(p.name).append(':').append(i).append("]\n");
                else
                    output.append(repeat3).append("[").append(p.blockName).append('~').append(p.name).append(':').append(i).append("] =")
                            .append(" [").append( Token.tokensToString(p.value.value, true) ).append("]")
                            .append(" -> [").append( p.resultValue != null ? p.resultValue.value : "" ).append("]\n");
            }
            output.append(repeat2).append("~\n");
        }

        // block parameters
        /*
        if (block.stack != null && !block.stack.isEmpty()) {
            output.append(repeat2).append("Stack:\n");
            for (int i = 0; i < block.stack.size(); i++) {
                output.append( block.stack.get(i).toString(String.valueOf(i), repeat3) );
            }
            output.append(repeat2).append("~\n");
        }
        */

        // block return
        if (block.result != null && block.result.value != null) {
            output.append(repeat2).append("Return: ")
                  .append("[").append( Token.tokensToString(block.result.expression.value, true) ).append("]")
                  .append(" -> [").append(block.result.value.value).append("]\n");
        }

        //
        output.append(repeat).append("eb\n");

        return output.toString();
    }
    /** parse block */
    public void parseBlock(final ArrayList<Block> blocks) {
        // read lines and parse
        for (int i = 0; i < lines.size();) {
            final ArrayList<Token> line = lines.get(i);
            parseLine(line, blocks);

            System.out.println( "line: "+Token.tokensToString(line, true) );
            lines.remove(line);
        }
    }
    /** parse line */
    private void parseLine(final ArrayList<Token> line, final ArrayList<Block> blocks) {
        // rename block call | rename variables and parameters
        for (int i = 0; i < line.size(); i++) {
            final Token currentToken = line.get(i);
            if (currentToken.type == TokenType.WORD) {
                if (i+1 < line.size() && line.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN)
                    currentToken.type = TokenType.BLOCK_CALL;
                else {
                    final Variable parameter = findParameter(currentToken.data);
                    if (parameter != null) currentToken.type = TokenType.PARAMETER_NAME;
                    else                   currentToken.type = TokenType.VARIABLE_NAME;
                }
            }
            //
        }

        // read block assign | read variable | read result
        for (int i = 0; i < line.size(); i++) {
            final Token currentToken = line.get(i);
            // variable increment & decrement
            if ((currentToken.type == TokenType.VARIABLE_NAME || currentToken.type == TokenType.PARAMETER_NAME) &&
                 i+1 < line.size() && (line.get(i+1).type == TokenType.INCREMENT || line.get(i+1).type == TokenType.DECREMENT)) {

                renameVariable(line, blocks);
                // TO:DO: pre increment/decrement (++i --i)

                final String[] blockInfo = currentToken.data.split("~");
                final String[] variableInfo = blockInfo[1].split(":");

                final Variable variable;
                final Variable parameter = findParameter(currentToken.data);
                if (parameter != null) {
                    variable = parameter;
                } else {
                    int variableIndex = Integer.parseInt(variableInfo[1]);
                    if (variableIndex == -1) {
                        variable = Block.findVariableInBlocks(variableInfo[0], this, blocks);
                    } else {
                        variable = this.variables.get(variableIndex);
                        variable.setValue(this, blocks);
                    }
                }

                if (variable.resultValue.type == ExpressionType.NUMBER) {
                    if (line.get(i+1).type == TokenType.INCREMENT)
                        variable.resultValue.value = Integer.parseInt( variable.resultValue.value.toString() ) + 1;
                    else
                        variable.resultValue.value = Integer.parseInt( variable.resultValue.value.toString() ) - 1;
                }
            } else
            // variable assign
            if ((currentToken.type == TokenType.VARIABLE_NAME || currentToken.type == TokenType.PARAMETER_NAME) &&
                 i+1 < line.size() && line.get(i+1).type == TokenType.EQUAL) {
                final String variableName = currentToken.data;
                line.remove(0); // remove name
                line.remove(0); // remove =

                renameVariable(line, blocks);
                final Variable variable = new Variable(variableName, ExpressionType.NUMBER, new ArrayList<>(line), name);
                variable.setValue(this, blocks); // calculate variable value now

                final int parameterIndex = findParameterIndex(variableName);
                if (currentToken.type == TokenType.VARIABLE_NAME || parameterIndex == -1)
                    addVariable(variable);
                else
                    parameters.set(parameterIndex, variable);
            } else
            // if
            if (currentToken.type == TokenType.IF && i+1 < line.size() && line.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN &&
                i+2 < line.size() && List.of(TokenType.BLOCK_CALL, TokenType.FUNCTION_CALL, TokenType.PROCEDURE_CALL).contains(line.get(i+2).type) ) {
                // check condition
                final ArrayList<Token> conditionTokens = line.get(i+1).childrens;
                renameVariable(conditionTokens, blocks);
                final Expression conditionExpression = new Expression(conditionTokens);
                conditionExpression.getValue(conditionExpression.value, this, blocks);
                if (Integer.parseInt(conditionExpression.valueResult.value.toString()) == 1) {
                    // parse local block
                    findBlock(line.get(i+2).data, blocks).parseBlock(blocks);
                }
            } else
            // while
            if (currentToken.type == TokenType.WHILE && i+1 < line.size() && line.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN &&
                    i+2 < line.size() && List.of(TokenType.BLOCK_CALL, TokenType.FUNCTION_CALL, TokenType.PROCEDURE_CALL).contains(line.get(i+2).type) ) {
                // check condition
                final ArrayList<Token> conditionTokens = line.get(i+1).childrens;
                renameVariable(conditionTokens, blocks);
                final Expression conditionExpression = new Expression(conditionTokens);

                final Block findBlock = findBlock(line.get(i+2).data, blocks);
                while (true) {
                    conditionExpression.getValue(conditionExpression.value, this, blocks);
                    if (Integer.parseInt(conditionExpression.valueResult.value.toString()) == 1) {
                        // parse local block
                        copyObject(findBlock).parseBlock(blocks);
                    } else break; // cycle end
                }
            } else
            // block call
            if (currentToken.type == TokenType.BLOCK_CALL && i+1 < line.size() && line.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                renameVariable(line, blocks);
                System.out.println( Token.tokensToString(line, true) );

                final ArrayList<ArrayList<Token>> lineSeparate = Token.separateTokens(TokenType.COMMA, line.get(i+1).childrens);
                final ArrayList<ExpressionObject> expressions = new ArrayList<>();
                expressions.add(new ExpressionObject(ExpressionType.NONE, currentToken.data));
                for (final ArrayList<Token> parameter : lineSeparate) {
                    final Expression expression = new Expression(parameter);
                    expression.getValue(expression.value, this, blocks);
                    expressions.add(expression.valueResult);
                }

                Parser.stack.add( new Stack(StackType.CALL, expressions) );
            } else
            // return
            if (currentToken.type == TokenType.RETURN_VALUE) {
                line.remove(0); // remove return

                renameVariable(line, blocks);
                final BlockResult result = new BlockResult(new ArrayList<>(line));
                result.setValue(this, blocks);
                this.result = result;
            }
        }
    }
    /** rename variables */
    private void renameVariable(final ArrayList<Token> tokens, final ArrayList<Block> blocks) {
        if (tokens == null || tokens.isEmpty()) return;

        final int tokensSize = tokens.size();
        for (int i = 0; i < tokensSize; i++) {
            final Token token = tokens.get(i);
            if (token.type == TokenType.WORD || token.type == TokenType.VARIABLE_NAME || token.type == TokenType.PARAMETER_NAME) {
                // block assign
                if (i+1 < tokensSize && tokens.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                    token.type = TokenType.BLOCK_CALL;
                    continue;
                }
                // parameter
                final int checkParameter = findParameterIndex(token.data);
                if (checkParameter >= 0) {
                    token.type = TokenType.PARAMETER_NAME;
                    token.data = name + '~' + token.data;
                    continue;
                }
                // variable
                final int checkVariable = getVariableIndex(token.data, blocks);
                if (checkVariable >= 0) {
                    token.type = TokenType.VARIABLE_NAME;
                    if (findVariableIndex(false, token.data, variables) == -1)
                        token.data = name + '~' + token.data + ":-1";
                    else
                        token.data = name + '~' + token.data + ':' + String.valueOf(checkVariable); // set variable name + num in variables ArrayList
                } //else
                //    new Log(LogType.error, "Expected existing variable ["+token.data+"] in block ["+name+']');
            } else if (token.type == TokenType.CIRCLE_BLOCK_BEGIN && token.childrens != null && !token.childrens.isEmpty())
                renameVariable(token.childrens, blocks);
        }
        //
    }
}
