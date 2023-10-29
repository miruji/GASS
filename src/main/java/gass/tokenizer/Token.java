package gass.tokenizer;

import java.util.ArrayList;

public class Token {
    public String data;                // word, block num ...
    public TokenType type;             // type
    public ArrayList<Token> childrens; // children tokens
    public Token(String data, TokenType type) {
        this.data = data;
        this.type = type;
    }
    public Token(String data, TokenType type, ArrayList<Token> childrens) {
        this.data = data;
        this.type = type;
        this.childrens = childrens;
    }
    public void addChildren(Token child) {
        if (childrens == null) childrens = new ArrayList<>();
        childrens.add(child);
    }
    public void addChildrens(ArrayList<Token> childrens) {
        for (Token children : childrens)
            addChildren(children);
    }
    /** tokens tree output */
    public static String outputChildrens(Token token, int depth) {
        StringBuilder output = new StringBuilder();
        output.append("\t".repeat(Math.max(0, depth)));

        if (token.data != null)
            output.append(token.type).append(" [").append(token.data).append("]\n");
        else
            output.append(token.type).append('\n');

        if (token.childrens != null) {
            for (Token child : token.childrens)
                output.append(outputChildrens(child, depth+1));
        }
        return output.toString();
    }
}
