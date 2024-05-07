package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;
import java.util.Objects;

public class Enum {
    public final String name;
    public ArrayList<Token> tokens;
    public Enum(final String name, final ArrayList<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }
    /** find enum by name */
    public static boolean find(final ArrayList<Enum> enums, final String findName) {
        for (Enum e : enums)
            if (Objects.equals(e.name, findName)) return true;
        return false;
    }
}
