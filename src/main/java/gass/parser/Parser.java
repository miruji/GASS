package gass.parser;

import gass.io.log.Log;
import gass.io.log.LogType;
import gass.tokenizer.Token;
import gass.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class Parser {
    public ArrayList<Token> tokens;
    public ArrayList<Enum> enums = new ArrayList<>();
    public ArrayList<Class> classes = new ArrayList<>();
    public ArrayList<Block> blocks = new ArrayList<>();
    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;

        // check func => check code exceptions
        // parse func => parse code

        checkAssign(); // check (= ENDLINE)

        deleteBlockEndline(); // :e (e [e {e ENDe
        parseAllBracket();    // () ->> [] ->> {}
        parseBlock(tokens, TokenType.BLOCK_BEGIN, TokenType.END); // : end

        parseEnum();  // enum
        parseClass(); // public/private class

        parseGlobalBlock();          // func/proc/none global block
        parseAllLocalBlock();        // func/proc/none local block
        checkProcedureAssign();      // check (= PROCEDURE_ASSIGN)
        parseAllGlobalBlockAssign(); // global assign to func/proc/none

        parseAllVariables(); // variables in global and local blocks
    }
    /** get error line tokens output */
    private String getErrorLineOutput(int errorToken, ArrayList<Token> tokens, boolean searchRightEndline) {
        int endlinePos = errorToken+1;
        if (searchRightEndline)
            for (int j = errorToken; j < tokens.size(); j++) {
                if (tokens.get(j).type == TokenType.ENDLINE || j+1 == tokens.size()) {
                    endlinePos = j;
                    break;
                }
            }
        // get line tokens
        StringBuilder stringBuffer = new StringBuilder();
        for (int j = endlinePos-1; j >= 0; j--) {
            Token token = tokens.get(j);
            if (token.type == TokenType.ENDLINE || j-1 <= 0) break;
            if (token.data != null && token.type != TokenType.PROCEDURE_ASSIGN)
                stringBuffer.insert(0, token.data).insert(0,' ');
            else {
                String stringType = Token.typeToString(token.type);
                if (stringType.isEmpty())
                    stringBuffer.insert(0, token.type.toString()).insert(0,' ');
                else
                    stringBuffer.insert(0, stringType).insert(0,' ');
            }
        }
        stringBuffer.deleteCharAt(0); // delete first ' '
        return stringBuffer.toString();
    }
    private String getErrorLineOutput(int errorToken, ArrayList<Token> tokens) {
        ArrayList<Token> result = new ArrayList<Token>();

        int lineBegin = 0;
        for (int i = errorToken-1; i > 0; i--) {
            if (tokens.get(i).type == TokenType.ENDLINE) {
                lineBegin = i;
                break;
            }
        }
        for (int i = lineBegin+1; i < tokens.size() && tokens.get(i).type != TokenType.ENDLINE; i++)
            result.add(tokens.get(i));

        return Token.tokensToString(result, false);
    }
    /** check (= ENDLINE) */
    private void checkAssign() {
        for (int i = 0; i < tokens.size(); i++) {
            TokenType tokenType = tokens.get(i).type;
            if (!Token.typeToString(tokenType).isEmpty() && tokens.get(i).data == null &&
                tokenType != TokenType.CIRCLE_BLOCK_BEGIN && tokenType != TokenType.FIGURE_BLOCK_BEGIN && tokenType != TokenType.SQUARE_BLOCK_BEGIN &&
                tokenType != TokenType.CIRCLE_BLOCK_END && tokenType != TokenType.FIGURE_BLOCK_END && tokenType != TokenType.SQUARE_BLOCK_END) {
                if (i-1 >= 0 && tokenType != TokenType.NOT) // NOT no have left value (!a)
                    if (tokens.get(i-1).type == TokenType.ENDLINE) {
                        new Log(LogType.error, "Expected a left-hand value to assign it: ["+getErrorLineOutput(i,tokens)+"]");
                    }
                if (i+1 < tokens.size())
                    if (tokens.get(i+1).type == TokenType.ENDLINE) {
                        new Log(LogType.error, "Expected a right-hand value to assign it: ["+getErrorLineOutput(i,tokens)+"]");
                    }
            }
        }
        //
    }
    /** delete e (endline token) :e (e [e {e ENDe */
    private void deleteBlockEndline() {
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
    private void parseAllBracket() {
        parseBlock(tokens, TokenType.CIRCLE_BLOCK_BEGIN, TokenType.CIRCLE_BLOCK_END);
        parseFigureBracket(tokens);
        parseSquareBracket(tokens);
    }
    /** parse [] brackets */
    private void parseSquareBracket(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            if (token.childrens != null)
                parseSquareBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.SQUARE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_END);
    }
    /** parse {} brackets */
    private void parseFigureBracket(ArrayList<Token> tokens) {
        for (Token token : tokens) {
            if (token.childrens != null)
                parseFigureBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.FIGURE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_END);
    }
    /** parse block BEGIN -> END */
    private void parseBlock(ArrayList<Token> tokens, TokenType beginType, TokenType endType) {
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
    private void parseEnum() {
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
    private void parseClass() {
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
    /** add new global block with parameters and check exist */
    private void addGlobalBlockWithParameters(String name, BlockType type, ArrayList<Token> parameters, ArrayList<Token> tokens) {
        for (Block b : blocks) { // check exist
            if (Objects.equals(b.name, name))
                new Log(LogType.error,"The global ["+b.name+"] block has been re-declared");
        }
        blocks.add(new Block(name, type, parameters, tokens));
    }
    /** add new global block with no parameters and check exist */
    private void addGlobalBlockWithNoParameters(String name, BlockType type, ArrayList<Token> tokens) {
        for (Block b : blocks) { // check exist
            if (Objects.equals(b.name, name))
                new Log(LogType.error,"The global ["+b.name+"] block has been re-declared");
        }
        blocks.add(new Block(name, type, tokens));
    }
    /** parse global block func/proc/none */
    private void parseGlobalBlock() {
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
                    addGlobalBlockWithParameters(tokens.get(i).data, type, token2.childrens, token3.childrens);
                    tokens.remove(i); // name
                    tokens.remove(i); // parameters
                    tokens.remove(i); // block
                    i--;
                }
            } else
            if (tokens.get(i).type == TokenType.WORD && token2.type == TokenType.BLOCK_BEGIN) {
                // block with no parameters
                addGlobalBlockWithNoParameters(tokens.get(i).data, type, token2.childrens);
                tokens.remove(i); // name
                tokens.remove(i); // block
                i--;
            }
        }
        //
    }
    /** parse all local block proc/func/none */
    private void parseAllLocalBlock() {
        for (Block block : blocks)
            parseLocalBlock(block, 1);
    }
    /** cycle parse local block proc/func/none  */
    private void parseLocalBlock(Block block, int depth) {
        if (block.tokens != null) { // if no tokens in global block => no local blocks
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
                        block.tokens.get(i).type = TokenType.BLOCK_ASSIGN;
                        block.tokens.get(i).data = String.valueOf(assignNum);
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
    /** check (a = PROCEDURE_ASSIGN) */
    private void checkProcedureAssign() {
        for (Block block : blocks) {
            ArrayList<Token> tokens = block.tokens;
            for (int i = 1; i < tokens.size(); i++) {
                if (tokens.get(i).type == TokenType.PROCEDURE_ASSIGN && tokens.get(i-1).type == TokenType.EQUAL)
                    new Log(LogType.error,"The result from the procedure in the block ["+block.name+"] is expected: ["+getErrorLineOutput(i, tokens, false)+"]");
            }
        }
        //
    }
    /** parse global assign to func/proc/none */
    private void parseGlobalBlockAssign(Block block) {
        ArrayList<Token> tokens = block.tokens;
        for (int i = 0; i+1 < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            if (currentToken.type == TokenType.WORD && tokens.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                currentToken.type = TokenType.BLOCK_ASSIGN;
                block.addDependencyBlock(currentToken.data);
            }
        }
        //
        if (block.localBlocks != null) {
            for (Block localBlock : block.localBlocks)
                parseGlobalBlockAssign(localBlock);
        }
    }
    /**  parse all global assign to func/proc/none */
    private void parseAllGlobalBlockAssign() {
        for (Block block : blocks)
            parseGlobalBlockAssign(block);
    }
    /** parser variables */
    private void parseVariables(Block block) {
        final ArrayList<Token> tokens = block.tokens;
        for (int i = 0; i+1 < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            if (currentToken.type == TokenType.WORD) {
                currentToken.type = TokenType.VARIABLE_NAME;
                // TO:DO: check left public/private or const/final

                // add new variable
                ArrayList<Token> variableValue = new ArrayList<Token>();
                if (tokens.get(i+1).type == TokenType.EQUAL && tokens.get(i+2).type != TokenType.ENDLINE) {
                    tokens.remove(i); // remove variable name
                    tokens.remove(i); // remove =
                    // next read right tokens
                    while (i < tokens.size()) {
                        if (tokens.get(i).type == TokenType.ENDLINE) {
                            tokens.remove(i);
                            break;
                        } else {
                            variableValue.add(tokens.get(i));
                            tokens.remove(i);
                        }
                    }
                }
                block.addVariable(currentToken.data, variableValue);
            }
        }
        //
        if (block.localBlocks != null) {
            for (Block localBlock : block.localBlocks)
                parseVariables(localBlock);
        }
    }
    /** parser all variables */
    private void parseAllVariables() {
        for (Block block : blocks) {
            parseVariables(block);
        }
        //
    }
}
