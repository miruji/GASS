package gass.parser;

import gass.tokenizer.Token;
import gass.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    public ArrayList<Token> tokens;
    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;

        deleteBLockEnline();
        parseAllBracket();
        parseBlock(tokens, TokenType.BLOCK_BEGIN, TokenType.END);
    }
    void deleteBLockEnline() {
        for (int i = 0; i+1 < tokens.size(); i++) {
            TokenType type = tokens.get(i).type;
            if ((type == TokenType.BLOCK_BEGIN || type == TokenType.CIRCLE_BLOCK_BEGIN ||
                    type == TokenType.SQUARE_BLOCK_BEGIN || type == TokenType.FIGURE_BLOCK_BEGIN) &&
                tokens.get(i+1).type == TokenType.ENDLINE) {
                tokens.remove(i+1);
                i--;
            }
        }
    }
    void parseAllBracket() {
        parseBlock(tokens, TokenType.CIRCLE_BLOCK_BEGIN, TokenType.CIRCLE_BLOCK_END);
        parseArrayBracket(tokens);
        parseClassBracket(tokens);
    }
    void parseArrayBracket(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            if (token.childrens != null)
                parseArrayBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.SQUARE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_END);
    }
    void parseClassBracket(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            if (token.childrens != null)
                parseClassBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.FIGURE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_END);
    }
    void parseBlock(ArrayList<Token> tokens, TokenType beginType, TokenType endType) {
        Stack<Integer> blocks = new Stack<>();

        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == beginType) {
                blocks.push(i);
            } else if (tokens.get(i).type == endType) {
                if (!blocks.isEmpty())
                    blocks.pop();
                tokens.remove(i);
                i--;
            } else if (!blocks.isEmpty()) {
                int lastBlockIndex = blocks.peek();
                tokens.get(lastBlockIndex).addChildren(new Token(tokens.get(i).word, tokens.get(i).type, tokens.get(i).childrens));
                tokens.remove(i);
                i--;
            }
        }
        //
    }
}
