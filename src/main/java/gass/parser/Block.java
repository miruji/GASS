package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Character.isDigit;

public class Block {
    public String name;                        // block name
    public BlockType type;                     // block type
    public ArrayList<Token> parameters;        // block parameters
    public ArrayList<Token> tokens;            // block tokens
    public ArrayList<Block> localBlocks;       // local blocks
    public ArrayList<String> dependencyBlocks; // global dependency blocks
    public ArrayList<Variable> variables;      // block variables
    public BlockResult result;
    /** global block with parameters */
    public Block(final String name, final BlockType type, final ArrayList<Token> parameters, final ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.tokens = tokens;
    }
    /** global block with no parameters */
    public Block(final String name, final BlockType type, final ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.tokens = tokens;
    }
    /** add local block */
    public void addLocalBlock(final Block localBlock) {
        if (localBlock == null) return;
        if (localBlocks == null) localBlocks = new ArrayList<>();
        localBlocks.add(localBlock);
    }
    /** add dependency block */
    public void addDependencyBlock(final String dependencyBlock) {
        if (dependencyBlock == null || dependencyBlock.isEmpty()) return;
        if (dependencyBlocks == null) dependencyBlocks = new ArrayList<>();

        // check exist
        for (final String dependency : dependencyBlocks) {
            if (Objects.equals(dependency, dependencyBlock))
                return;
        }

        dependencyBlocks.add(dependencyBlock);
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

        final boolean type = isDigit(findName.charAt(0));
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
    public void addVariable(final String name, final ArrayList<Token> value) {
        if (name == null || name.isEmpty() || value == null || value.isEmpty()) return;
        if (variables == null) variables = new ArrayList<>();

        // no check exist
        // each variable has its own declaration order

        variables.add( new Variable(name, ExpressionType.NUMBER, value) );
    }
    /** find variable */
    public Variable findVariable(final String findName, final ArrayList<Variable> variables) {
        if (findName != null && !findName.isEmpty() && variables != null && !variables.isEmpty())
            for (int i = variables.size()-1; i >= 0; i--) {
                final Variable findVariable = variables.get(i);
                if (Objects.equals(findVariable.name, findName))
                    return findVariable;
            }
        return null;
    }
    public int findVariableIndex(final String findName, final ArrayList<Variable> variables) {
        if (findName != null && !findName.isEmpty() && variables != null && !variables.isEmpty())
            for (int i = variables.size()-1; i >= 0; i--) {
                if (Objects.equals(variables.get(i).name, findName))
                    return i;
            }
        return -1;
    }
    /** get variable num by name */
    public int getVariableIndex(final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty()) return -1;

        int index = findVariableIndex(findName, variables);
        if (index != -1) return index;

        final Block upBlock = getBlock(name.split(":")[0], blocks);
        index = findVariableIndex(findName, upBlock.variables);
        if (index != -1) return index;

        if (upBlock.localBlocks != null && !upBlock.localBlocks.isEmpty())
            for (Block localBlock : upBlock.localBlocks) {
                if (localBlock.variables == null) break;
                index = findVariableIndex(findName, localBlock.variables);
                if (index != -1) return index;
            }

        return -1;
    }
    /** get variable block by name */
    public Block getVariableBlock(final String findName, final ArrayList<Block> blocks) {
        if (findName == null || findName.isEmpty()) return null;

        Variable variable = findVariable(findName, variables);
        if (variable != null) return this;

        final Block upBlock = getBlock(name.split(":")[0], blocks);
        variable = findVariable(findName, upBlock.variables);
        if (variable != null) return upBlock;

        if (upBlock.localBlocks != null && !upBlock.localBlocks.isEmpty())
            for (Block localBlock : upBlock.localBlocks) {
                if (localBlock.variables == null) break;
                variable = findVariable(findName, localBlock.variables);
                if (variable != null) return localBlock;
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
    /** local blocks tree output */
    public static String outputLocalBlocks(final Block block, final int depth) {
        final StringBuilder output = new StringBuilder();

        String repeat = "\t".repeat(Math.max(0, depth));
        String repeat2 = repeat+"\t";
        String repeat3 = repeat2+"\t";

        // block info
        output.append(repeat).append(block.type).append(" [").append(block.name).append("]:\n");

        // block tokens info
        if (block.tokens != null && !block.tokens.isEmpty()) {
            output.append(repeat2).append("Tokens:\n");
            for (final Token t : block.tokens)
                output.append(Token.outputChildrens(t, depth+2));
            output.append(repeat2).append("-\n");
        }

        // local blocks info
        if (block.localBlocks != null && !block.localBlocks.isEmpty()) {
            output.append(repeat2).append("Local blocks:\n");
            for (int i = 0; i < block.localBlocks.size(); i++)
                output.append(outputLocalBlocks(block.localBlocks.get(i), depth+2));
            output.append(repeat2).append("-\n");
        }

        if (block.dependencyBlocks != null && !block.dependencyBlocks.isEmpty()) {
            output.append(repeat2).append("Dependency blocks:\n");
            for (final String d : block.dependencyBlocks)
                output.append(repeat3).append(d).append('\n');
            output.append(repeat2).append("-\n");
        }

        // dependency blocks info
        if (block.variables != null && !block.variables.isEmpty()) {
            output.append(repeat2).append("Variables:\n");
            for (int i = 0; i < block.variables.size(); i++) {
                final Variable v = block.variables.get(i);
                output.append(repeat3).append(i).append(": [").append(v.name).append("] =")
                      .append(" [").append( Token.tokensToString(v.value.value, true) ).append("]")
                      .append(" -> [").append( v.resultValue != null ? v.resultValue.value : "" ).append("]\n");
            }
            output.append(repeat2).append("-\n");
        }

        // block parameters
        if (block.parameters != null && !block.parameters.isEmpty()) {
            output.append(repeat2).append("Parameters: ")
                  .append("[").append( Token.tokensToString(block.parameters, true) ).append("]\n");
        }

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
}
