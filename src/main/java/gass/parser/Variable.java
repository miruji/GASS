package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class Variable {
    public final String name;
    public ArrayList<Token> value;
    public Variable(String name, ArrayList<Token> value) {
        this.name = name;
        this.value = value;
    }
}
