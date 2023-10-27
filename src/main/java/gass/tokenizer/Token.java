package gass.tokenizer;

import java.util.ArrayList;
import java.util.List;

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
}
