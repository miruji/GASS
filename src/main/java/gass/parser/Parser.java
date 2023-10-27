package gass.parser;

import gass.tokenizer.Token;
import gass.tokenizer.TokenType;

import java.util.ArrayList;

public class Parser {
    public ArrayList<Token> tokens;
    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;

        parseAllBracket();
        parseBlock(tokens, TokenType.BLOCK_BEGIN, TokenType.END);
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
        ArrayList<Integer> blocks = new ArrayList<>();
        //int length = 0;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == beginType) {
                //length++;
                //System.out.println("BEG "+i+" LEN "+length);

                blocks.add(i);
            } else
            if (tokens.get(i).type == endType) {
                //length--;
                //System.out.println("END "+i+" LEN "+length+" BLO "+blocks.toString());

                int lastBlock = blocks.size()-1;
                if (lastBlock-1 >= 0) {
                    //System.out.println( blocks.get(lastBlock-1) + ": " + blocks.get(lastBlock) );
                    tokens.get( blocks.get(lastBlock-1) ).addChildren( tokens.get(blocks.get(lastBlock)) );
                    tokens.remove(blocks.get(lastBlock).intValue());
                    i--;
                }
                blocks.remove(lastBlock);

                tokens.remove(i);
                i--;
            } else
            if (!blocks.isEmpty()) {
                tokens.get( blocks.get(blocks.size()-1) ).addChildren( new Token(tokens.get(i).word, tokens.get(i).type, tokens.get(i).childrens) );
                tokens.remove(i);
                i--;
            }
        }
        //
    }
}
