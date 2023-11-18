package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;
import java.util.Objects;

public class Class {
    public final String name;
    public final ClassType type;
    public ArrayList<Token> tokens;
    public Class(final String name, final ClassType type, final ArrayList<Token> tokens) {
        this.name = name;
        this.type = type;
        this.tokens = tokens;
    }
    /** find class by name */
    public static boolean find(final ArrayList<Class> classes, final String findName) {
        for (Class c : classes)
            if (Objects.equals(c.name, findName)) return true;
        return false;
    }
}
