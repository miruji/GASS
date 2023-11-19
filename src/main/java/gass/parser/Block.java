package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;
import java.util.Objects;

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
    public Block(final String name, final BlockType type, ArrayList<Token> parameters, final ArrayList<Token> tokens) {
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
    /** local block */
    public Block(final BlockType type, final ArrayList<Token> tokens) {
        this.type = type;
        this.tokens = tokens;
    }
    /** add local block */
    public void addLocalBlock(final Block localBlock) {
        if (localBlocks == null) localBlocks = new ArrayList<>();
        localBlocks.add(localBlock);
    }
    /** add dependency block */
    public void addDependencyBlock(final String dependencyBlock) {
        if (dependencyBlocks == null) dependencyBlocks = new ArrayList<>();

        // check exist
        for (final String d : dependencyBlocks) {
            if (Objects.equals(d, dependencyBlock))
                return;
        }

        dependencyBlocks.add(dependencyBlock);
    }
    /** get dependency blocks */
    public static ArrayList<Block> getDependencyBlocks(final ArrayList<Block> blocks, final ArrayList<String> findNames) {
        ArrayList<Block> result = new ArrayList<>();
        if (findNames != null)
            for (final String findName : findNames) {
                final Block b = getBlock(blocks, findName);
                if (b != null) result.add(b);
            }
        return result;
    }
    /** get block by name */
    public static Block getBlock(final ArrayList<Block> blocks, final String findName) {
        for (final Block b : blocks)
            if (Objects.equals(b.name, findName)) return b;
        return null;
    }
    /** add variable */
    public void addVariable(final String name, final ArrayList<Token> value) {
        if (variables == null) variables = new ArrayList<>();

        // no check exist
        // each variable has its own declaration order

        variables.add( new Variable(name, ExpressionType.NUMBER, value) );
    }
    /** get variable by name */
    public static Variable getVariable(Block block, final String findName) {
        for (final Variable v : block.variables)
            if (Objects.equals(v.name, findName)) return v;
        return null;
    }
    /** local blocks tree output */
    public static String outputLocalBlocks(final Block block, final int depth, final int assignNum) {
        final StringBuilder output = new StringBuilder();

        // block info
        output.append("\t".repeat(Math.max(0, depth)));
        if (block.name == null)
            output.append(block.type).append(" [").append(assignNum).append("]:\n");
        else
            output.append(block.type).append(" [").append(block.name).append("]:\n");

        // dependency blocks info
        String repeat = "\t".repeat(Math.max(0, depth+1));
        String repeat2 = repeat+"\t";

        if (block.dependencyBlocks != null)
            if (!block.dependencyBlocks.isEmpty()) {
                output.append(repeat).append("Dependency blocks:\n");
                for (final String d : block.dependencyBlocks)
                    output.append(repeat2).append(d).append('\n');
                output.append(repeat).append("-\n");
            }

        // dependency blocks info
        if (block.variables != null)
            if (!block.variables.isEmpty()) {
                output.append(repeat).append("Variables:\n");
                for (final Variable v : block.variables)
                    output.append(repeat2).append(v.name)
                          .append(": [").append( Token.tokensToString(v.value.value, true) ).append("]")
                          .append(" -> [").append( v.getValue() != null ? v.getValue().value : "" ).append("]\n");
                output.append(repeat).append("-\n");
            }

        // block return
        if (block.result != null) {
            if (block.result.value != null)
                output.append(repeat).append("Return: ")
                      .append(": [").append( Token.tokensToString(block.result.expression.value, true) ).append("]")
                      .append(" -> [").append(block.result.value.value).append("]\n");
        }

        // block tokens info
        if (block.tokens != null)
            if (!block.tokens.isEmpty()) {
                output.append(repeat).append("Tokens:\n");
                for (final Token t : block.tokens)
                    output.append(Token.outputChildrens(t, depth+2));
                output.append(repeat).append("-\n");
            }

        // local blocks info
        if (block.localBlocks != null)
            if (!block.localBlocks.isEmpty()) {
                output.append(repeat).append("Local blocks:\n");
                for (int i = 0; i < block.localBlocks.size(); i++)
                    output.append(outputLocalBlocks(block.localBlocks.get(i), depth+2, i));
                output.append(repeat).append("-\n");
            }

        //
        output.append("\t".repeat(Math.max(0, depth))).append("eb\n");

        return output.toString();
    }
}
