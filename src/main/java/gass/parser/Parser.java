package gass.parser;

import gass.tokenizer.Token;
import gass.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.Stack;

public class Parser {
    public ArrayList<Token> tokens;
    public ArrayList<Enum> enums = new ArrayList<>();
    public ArrayList<Class> classes = new ArrayList<>();
    public ArrayList<Block> blocks = new ArrayList<>();
    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;

        deleteBlockEndline(); // :e (e [e {e ENDe
        parseAllBracket();    // () ->> [] ->> {}
        parseBlock(tokens, TokenType.BLOCK_BEGIN, TokenType.END); // : end

        parseEnum();  // enum
        parseClass(); // public/private class
        parseGlobalBlock();   // func/proc/none global block
        parseAllLocalBlock(); // func/proc/name local block
    }
    /** delete e (endline token) :e (e [e {e ENDe */
    void deleteBlockEndline() {
        for (int i = 0; i+1 < tokens.size(); i++) {
            if (tokens.get(i+1).type == TokenType.ENDLINE) {
                TokenType type = tokens.get(i).type;
                if (type == TokenType.END) {
                    tokens.remove(i+1);
                    i--;
                } else
                if (type == TokenType.BLOCK_BEGIN || type == TokenType.CIRCLE_BLOCK_BEGIN ||
                        type == TokenType.SQUARE_BLOCK_BEGIN || type == TokenType.FIGURE_BLOCK_BEGIN) {
                    tokens.remove(i+1);
                    i--;
                }
            }
            //
        }
    }
    /** parse () ->> [] ->> {} brackets */
    void parseAllBracket() {
        parseBlock(tokens, TokenType.CIRCLE_BLOCK_BEGIN, TokenType.CIRCLE_BLOCK_END);
        parseFigureBracket(tokens);
        parseSquareBracket(tokens);
    }
    /** parse [] brackets */
    void parseSquareBracket(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            if (token.childrens != null)
                parseSquareBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.SQUARE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_END);
    }
    /** parse {} brackets */
    void parseFigureBracket(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            if (token.childrens != null)
                parseFigureBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.FIGURE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_END);
    }
    /** parse block BEGIN -> END */
    void parseBlock(ArrayList<Token> tokens, TokenType beginType, TokenType endType) {
        Stack<Integer> blocks = new Stack<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == beginType) {      // begin
                blocks.push(i);
            } else if (tokens.get(i).type == endType) { // end
                int lastBlock = blocks.size()-2;
                if (lastBlock >= 0) {
                    tokens.get( blocks.get(lastBlock) ).addChildren( tokens.get(blocks.peek()) );
                    tokens.remove( blocks.peek().intValue() );
                    i--;
                }
                blocks.pop();
                tokens.remove(i);
                i--;
            } else if (!blocks.isEmpty()) { // add new childrens to token
                tokens.get(blocks.peek()).addChildren(new Token(tokens.get(i).data, tokens.get(i).type, tokens.get(i).childrens));
                tokens.remove(i);
                i--;
            }
        }
    }
    /** parse enum block */
    void parseEnum() {
        for (int i = 0; i+2 < tokens.size(); i++) {
            Token token2 = tokens.get(i+1);
            Token token3 = tokens.get(i+2);
            if (tokens.get(i).type == TokenType.ENUM && token2.type == TokenType.WORD && token3.type == TokenType.BLOCK_BEGIN) {
                enums.add(new Enum(token2.data, token3.childrens));
                tokens.remove(i); // enum
                tokens.remove(i); // name
                tokens.remove(i); // block
                i--;
            }
        }
    }
    /** parse class block (public/private) */
    void parseClass() {
        for (int i = 0; i+2 < tokens.size(); i++) {
            Token token2 = tokens.get(i+1);
            Token token3 = tokens.get(i+2);
            if (tokens.get(i).type == TokenType.PRIVATE && token2.type == TokenType.WORD && token3.type == TokenType.BLOCK_BEGIN) {
                classes.add(new Class(token2.data, ClassType.PRIVATE, token3.childrens));
                tokens.remove(i); // private/public
                tokens.remove(i); // name
                tokens.remove(i); // block
                i--;
            } else
            if (tokens.get(i).type == TokenType.PUBLIC && token2.type == TokenType.WORD && token3.type == TokenType.BLOCK_BEGIN) {
                classes.add(new Class(token2.data, ClassType.PUBLIC, token3.childrens));
                tokens.remove(i); // private/public
                tokens.remove(i); // name
                tokens.remove(i); // block
                i--;
            }
        }
    }
    /** parse global block func/proc/none */
    void parseGlobalBlock() {
        for (int i = 0; i+1 < tokens.size(); i++) {
            // type
            BlockType type = BlockType.NONE;
            if (i-1 >= 0) {
                if (tokens.get(i-1).type == TokenType.FUNCTION) {
                    type = BlockType.FUNCTION;
                    tokens.remove(i-1);
                    i--;
                } else
                if (tokens.get(i-1).type == TokenType.PROCEDURE) {
                    type = BlockType.PROCEDURE;
                    tokens.remove(i-1);
                    i--;
                }
            }

            // declaration
            Token token2 = tokens.get(i+1);
            if (tokens.get(i).type == TokenType.WORD && token2.type == TokenType.CIRCLE_BLOCK_BEGIN && i+2 < tokens.size()) {
                Token token3 = tokens.get(i+2);
                if (token3.type == TokenType.BLOCK_BEGIN) {
                    // block with parameters
                    blocks.add(new Block(tokens.get(i).data, type, token2.childrens, token3.childrens));
                    tokens.remove(i); // name
                    tokens.remove(i); // parameters
                    tokens.remove(i); // block
                    i--;
                }
            } else
            if (tokens.get(i).type == TokenType.WORD && token2.type == TokenType.BLOCK_BEGIN) {
                // block with no parameters
                blocks.add(new Block(tokens.get(i).data, type, token2.childrens));
                tokens.remove(i); // name
                tokens.remove(i); // block
                i--;
            }
        }
        //
    }
    /** parse all local block proc/func/none */
    void parseAllLocalBlock() {
        for (Block block : blocks)
            parseLocalBlock(block, 1);
    }
    /** cycle parse local block proc/func/none  */
    void parseLocalBlock(Block block, int depth) {
        if (block.tokens != null) { // if no tokens => no local block
            int assignNum = 0;

            for (int i = 0; i < block.tokens.size(); i++) {
                if (block.tokens.get(i).type == TokenType.BLOCK_BEGIN) {
                    BlockType newBlockType = BlockType.NONE;
                    if (block.tokens.get(i-1).type == TokenType.PROCEDURE)
                        newBlockType = BlockType.PROCEDURE;
                    else
                    if (block.tokens.get(i-1).type == TokenType.FUNCTION)
                        newBlockType = BlockType.FUNCTION;

                    Block newBlock = new Block(newBlockType, block.tokens.get(i).childrens);

                    if (newBlockType == BlockType.NONE) {
                        block.tokens.get(i).type = TokenType.NONE_ASSIGN;
                        block.tokens.get(i).data = "0";
                        block.tokens.get(i).childrens = null;
                    } else {
                        if (newBlockType == BlockType.PROCEDURE)
                            block.tokens.get(i).type = TokenType.PROCEDURE_ASSIGN;
                        else
                            block.tokens.get(i).type = TokenType.FUNCTION_ASSIGN;
                        block.tokens.remove(i-1);
                        i--;

                        block.tokens.get(i).data = String.valueOf(assignNum);
                        assignNum++;
                        block.tokens.get(i).childrens = null;
                    }

                    parseLocalBlock(newBlock, depth+1);
                    block.addLocalBlock(newBlock);
                }
            }

        }
        //
    }
}
