package gass.tokenizer;

import java.util.ArrayList;

public class Token {
    public String word;
    public TokenType type;
    public ArrayList<Token> childrens;
    public Token(String word, TokenType type) {
        this.word = word;
        this.type = type;
    }
    public Token(String word, TokenType type, ArrayList<Token> childrens) {
        this.word = word;
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

        if (token.word != null)
            output.append(token.type).append(" [").append(token.word).append("]\n");
        else
            output.append(token.type).append('\n');

        if (token.childrens != null) {
            for (Token child : token.childrens)
                output.append(outputChildrens(child, depth+1));
        }
        return output.toString();
    }
}
