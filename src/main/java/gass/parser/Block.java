package gass.parser;

import gass.io.log.Log;
import gass.io.log.LogType;
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
    public  ArrayList<Variable> variables;     // block variables
    /** global block with parameters */
    public Block(String name, BlockType type, ArrayList<Token> parameters, ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.tokens = tokens;
    }
    /** global block with no parameters */
    public Block(String name, BlockType type, ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.tokens = tokens;
    }
    /** local block */
    public Block(BlockType type, ArrayList<Token> tokens) {
        this.type = type;
        this.tokens = tokens;
    }
    /** add local block */
    public void addLocalBlock(Block localBlock) {
        if (localBlocks == null) localBlocks = new ArrayList<>();
        localBlocks.add(localBlock);
    }
    /** add dependency block */
    public void addDependencyBlock(String dependencyBlock) {
        if (dependencyBlocks == null) dependencyBlocks = new ArrayList<>();

        // check exist
        for (String d : dependencyBlocks) {
            if (Objects.equals(d, dependencyBlock))
                return;
        }

        dependencyBlocks.add(dependencyBlock);
    }
    /** add variable */
    public void addVariable(String name, ArrayList<Token> value) {
        if (variables == null) variables = new ArrayList<>();

        // check exist
        for (Variable v : variables) {
            if (Objects.equals(v.name, name))
                new Log(LogType.error,"The ["+name+"] variable could not be created because it was already declared");
        }

        variables.add( new Variable(name, value) );
    }
    /** find block by name */
    public static boolean find(ArrayList<Block> blocks, String findName) {
        for (Block b : blocks)
            if (Objects.equals(b.name, findName)) return true;
        return false;
    }
    /** local blocks tree output */
    public static String outputLocalBlocks(Block block, int depth, int assignNum) {
        StringBuilder output = new StringBuilder();

        // block info
        output.append("\t".repeat(Math.max(0, depth)));
        if (block.name == null)
            output.append(block.type).append(" [").append(assignNum).append("]:\n");
        else
            output.append(block.type).append(" [").append(block.name).append("]:\n");

        // dependency blocks info
        String repeat = "\t".repeat(Math.max(0, depth+1));
        String repeat2 = repeat+"\t";

        if (block.dependencyBlocks != null) {
            output.append(repeat).append("Dependency blocks:\n");
            for (String d : block.dependencyBlocks)
                output.append(repeat2).append(d).append('\n');
            output.append(repeat).append("-\n");
        }

        // dependency blocks info
        if (block.variables != null) {
            output.append(repeat).append("Variables :\n");
            for (Variable v : block.variables)
                output.append(repeat2).append(v.name).append(": ").append( Token.tokensToString(v.value, true) ).append('\n');
            output.append(repeat).append("-\n");
        }

        // block tokens info
        if (block.tokens != null) {
            output.append(repeat).append("Tokens:\n");
            for (Token t : block.tokens)
                output.append(Token.outputChildrens(t, depth+2));
            output.append(repeat).append("-\n");
        }

        // local blocks info
        if (block.localBlocks != null) {
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
