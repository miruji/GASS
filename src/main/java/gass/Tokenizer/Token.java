package gass.Tokenizer;

public class Token {
    public String word;
    public TokenType type;
    public Token(String word, TokenType type) {
        this.word = word;
        this.type = type;
    }
}
