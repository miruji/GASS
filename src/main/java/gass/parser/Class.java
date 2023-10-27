package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class Class {
    public final String name;
    public final ClassType type;
    public ArrayList<Token> tokens;
    public Class(String name, ClassType type, ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.tokens = tokens;
    }
}
