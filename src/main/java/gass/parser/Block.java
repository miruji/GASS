package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class Block {
    public final String name;
    public BlockType type;
    public ArrayList<Token> parameters;
    public ArrayList<Token> tokens;
    public Block(String name, BlockType type, ArrayList<Token> parameters, ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.tokens = tokens;
    }
    public Block(String name, BlockType type, ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.tokens = tokens;
    }
}
