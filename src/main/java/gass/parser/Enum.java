package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class Enum {
    public final String name;
    public ArrayList<Token> tokens;
    public Enum(String name, ArrayList<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }
}
