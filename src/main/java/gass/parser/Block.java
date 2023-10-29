package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;
import java.util.Objects;

public class Block {
    public String name;
    public BlockType type;
    public ArrayList<Token> parameters;
    public ArrayList<Token> tokens;
    public ArrayList<Block> localBlocks;
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

        // block tokens info
        if (block.tokens != null) {
            output.append("\t".repeat(Math.max(0, depth+1))).append("Tokens:\n");
            for (Token t : block.tokens)
                output.append(Token.outputChildrens(t, depth+2));
            output.append("\t".repeat(Math.max(0, depth+1))).append("-\n");
        }

        // local blocks info
        if (block.localBlocks != null) {
            output.append("\t".repeat(Math.max(0, depth+1))).append("Local blocks:\n");
            for (int i = 0; i < block.localBlocks.size(); i++)
                output.append(outputLocalBlocks(block.localBlocks.get(i), depth+2, i));
            output.append("\t".repeat(Math.max(0, depth+1))).append("-\n");
        }

        //
        output.append("\t".repeat(Math.max(0, depth))).append("eb\n");

        return output.toString();
    }
}
