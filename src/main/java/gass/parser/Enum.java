package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;
import java.util.Objects;

public class Enum {
    public final String name;
    public ArrayList<Token> tokens;
    public Enum(String name, ArrayList<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }
    /** find enum by name */
    public static boolean find(ArrayList<Enum> enums, String findName) {
        for (Enum e : enums)
            if (Objects.equals(e.name, findName)) return true;
        return false;
    }
}
