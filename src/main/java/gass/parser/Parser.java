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
    public Parser(final ArrayList<Token> tokens) {
        this.tokens = tokens;

        // check func     => check code exceptions
        // rename func    => rename
        // delete func    => delete
        // declarate func => declarate
        // parse func     => parse code

        checkAssign(); // check (= ENDLINE)

        deleteBlockEndline(); // :e (e [e {e ENDe
        parseAllBracket();    // () ->> [] ->> {}
        parseBlock(tokens, TokenType.BLOCK_BEGIN, TokenType.END); // : end

        parseEnum();  // enum
        parseClass(); // public/private class

        parseGlobalBlock();                      // func/proc/none global block
        for (final Block block : blocks)
            declarateLocalBlock(block, 1); // func/proc/none local block
        checkProcedureAssign();                  // check (= PROCEDURE_ASSIGN)
        for (final Block block : blocks)
            parseGlobalBlockAssign(block);       // global assign to func/proc/none


        for (final Block block : blocks) {
            declarateVariable(block); // variables <- in global and local blocks
            declarateResult(block);   // return <- in global and local block
        }

        parseAllBlockDependency();
    }
    /** get error line tokens output */
    private String getErrorLineOutput(final int errorToken, final ArrayList<Token> tokens) {
        final ArrayList<Token> result = new ArrayList<>();

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
            final TokenType tokenType = tokens.get(i).type;
            if (Token.checkOperator(tokenType) && tokens.get(i).data == null) {
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
                final TokenType type = tokens.get(i).type;
                //if (type == TokenType.END) {
                //    tokens.remove(i+1);
                //    i--;
                //} else
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
    private void parseSquareBracket(final ArrayList<Token> tokens) {
        for (final Token token : tokens) {
            if (token.childrens != null)
                parseSquareBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.SQUARE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_END);
    }
    /** parse {} brackets */
    private void parseFigureBracket(final ArrayList<Token> tokens) {
        for (final Token token : tokens) {
            if (token.childrens != null)
                parseFigureBracket(token.childrens);
        }
        parseBlock(tokens, TokenType.FIGURE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_END);
    }
    /** parse block BEGIN -> END */
    private void parseBlock(final ArrayList<Token> tokens, final TokenType beginType, final TokenType endType) {
        final Stack<Integer> blocks = new Stack<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == beginType) {      // begin
                blocks.push(i);
            } else if (tokens.get(i).type == endType) { // end
                final int lastBlock = blocks.size()-2;
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
            final Token token2 = tokens.get(i+1);
            final Token token3 = tokens.get(i+2);
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
            final Token token2 = tokens.get(i+1);
            final Token token3 = tokens.get(i+2);
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
    private void addGlobalBlockWithParameters(final String name, final BlockType type, final ArrayList<Token> parameters, final ArrayList<Token> tokens) {
        for (final Block b : blocks) { // check exist
            if (Objects.equals(b.name, name))
                new Log(LogType.error,"The global ["+b.name+"] block has been re-declared");
        }
        blocks.add(new Block(name, type, parameters, tokens));
    }
    /** add new global block with no parameters and check exist */
    private void addGlobalBlockWithNoParameters(final String name, final BlockType type, final ArrayList<Token> tokens) {
        for (final Block b : blocks) { // check exist
            if (Objects.equals(b.name, name))
                new Log(LogType.error,"The global ["+b.name+"] block has been re-declared");
        }
        blocks.add(new Block(name, type, tokens));
    }
    /** parse global block func/proc/none */
    private void parseGlobalBlock() {
        for (int i = 0; i+1 < tokens.size(); i++) {
            // type
            final BlockType type;
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
                } else type = BlockType.NONE;
            } else type = BlockType.NONE;


            // declaration
            final Token token2 = tokens.get(i+1);
            if (tokens.get(i).type == TokenType.WORD && token2.type == TokenType.CIRCLE_BLOCK_BEGIN && i+2 < tokens.size()) {
                final Token token3 = tokens.get(i+2);
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
    /** cycle parse local block proc/func/none  */
    private void declarateLocalBlock(final Block block, final int depth) {
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

                    final String localBlockName = block.name+':'+assignNum;
                    final Block newBlock = new Block(localBlockName, newBlockType, block.tokens.get(i).childrens);

                    if (newBlockType == BlockType.NONE) {
                        block.tokens.get(i).type = TokenType.BLOCK_ASSIGN;
                        block.tokens.get(i).data = localBlockName;
                        block.tokens.get(i).childrens = null;
                    } else {
                        if (newBlockType == BlockType.PROCEDURE)
                            block.tokens.get(i).type = TokenType.PROCEDURE_ASSIGN;
                        else
                            block.tokens.get(i).type = TokenType.FUNCTION_ASSIGN;
                        block.tokens.remove(i-1);
                        i--;

                        block.tokens.get(i).data = localBlockName;
                        block.tokens.get(i).childrens = null;
                    }
                    assignNum++;

                    declarateLocalBlock(newBlock, depth+1);
                    block.addLocalBlock(newBlock);
                }
            }

        }
        //
    }
    /** check (a = PROCEDURE_ASSIGN) */
    private void checkProcedureAssign() {
        for (final Block block : blocks) {
            final ArrayList<Token> tokens = block.tokens;
            if (tokens == null || tokens.isEmpty()) continue;

            for (int i = 1; i < tokens.size(); i++) {
                if (tokens.get(i).type == TokenType.PROCEDURE_ASSIGN && tokens.get(i-1).type == TokenType.EQUAL)
                    new Log(LogType.error,"The result from the procedure in the block ["+block.name+"] is expected ["+getErrorLineOutput(i, tokens)+"]");
            }
        }
        //
    }
    /** parse global assign to func/proc/none */
    private void parseGlobalBlockAssign(final Block block) {
        final ArrayList<Token> tokens = block.tokens;
        if (tokens == null || tokens.isEmpty()) return;

        for (int i = 0; i+1 < tokens.size(); i++) {
            final Token currentToken = tokens.get(i);
            if (currentToken.type == TokenType.WORD && tokens.get(i+1).type == TokenType.CIRCLE_BLOCK_BEGIN) {
                currentToken.type = TokenType.BLOCK_ASSIGN;
                block.addDependencyBlock(currentToken.data);
            }
        }
        //
        if (block.localBlocks != null) {
            for (final Block localBlock : block.localBlocks)
                parseGlobalBlockAssign(localBlock);
        }
    }
    /** declarate variables */
    private void renameVariable(final ArrayList<Token> tokens, final Block block) {
        for (Token token : tokens) {
            if (token.type == TokenType.WORD) {
                token.type = TokenType.VARIABLE_NAME;
                System.out.println(">> "+(token.data != null? token.data : ""));
                final int checkVariable = block.getVariableNum(token.data, blocks);
                if (checkVariable >= 0)
                    token.data += ':' + String.valueOf(checkVariable); // set variable name + num in variables ArrayList
                else
                    new Log(LogType.error, "Expected existing variable ["+token.data+"] in block ["+block.name+']');
            } else
            if (token.type == TokenType.CIRCLE_BLOCK_BEGIN && token.childrens != null && !token.childrens.isEmpty())
                renameVariable(token.childrens, block);
            else
            //
            if (token.type == TokenType.BLOCK_ASSIGN || token.type == TokenType.FUNCTION_ASSIGN) {
                System.out.println("rename: "+token.data);
                final String[] localBlockName = token.data.split(":");
                final Block localBlock = block.localBlocks.get( Integer.parseInt(localBlockName[localBlockName.length-1]) );
                declarateVariable(localBlock);
                declarateResult(localBlock);
                parseLocalBlock(localBlock);
            }
        }
    }
    private void declarateVariable(final Block block) {
        final ArrayList<Token> tokens = block.tokens;
        if (tokens == null || tokens.isEmpty()) return;

        for (int i = 0; i+1 < tokens.size(); i++) {
            final Token currentToken = tokens.get(i);
            if (currentToken.type == TokenType.WORD) {
                currentToken.type = TokenType.VARIABLE_NAME;
                // TO:DO: check left public/private or const/final

                // add new variable
                final ArrayList<Token> variableValue = new ArrayList<>();
                if (tokens.get(i+1).type == TokenType.EQUAL && tokens.get(i+2).type != TokenType.ENDLINE) {
                    tokens.remove(i); // remove variable name
                    tokens.remove(i); // remove =

                    // next read right tokens
                    while (i < tokens.size()) {
                        final Token nextToken = tokens.get(i);
                        if (nextToken.type == TokenType.ENDLINE) {
                            tokens.remove(i);
                            i--;
                            break;
                        } else {
                            // checkVariable
                            variableValue.add(nextToken);
                            tokens.remove(i);
                        }
                    }
                    renameVariable(variableValue, block);
                    block.addVariable(currentToken.data, variableValue);
                }
                //
            } else
            if (currentToken.type == TokenType.BLOCK_ASSIGN || currentToken.type == TokenType.FUNCTION_ASSIGN) {
                System.out.println("no rename: "+currentToken.data);
                final String[] localBlockName = currentToken.data.split(":");
                final Block localBlock = block.localBlocks.get( Integer.parseInt(localBlockName[localBlockName.length-1]) );
                declarateVariable(localBlock);
                declarateResult(localBlock);
                parseLocalBlock(localBlock);
            }
        }
        // local blocks
        //if (block.localBlocks != null) {
        //    for (final Block localBlock : block.localBlocks)
        //        declarateVariable(localBlock, height+1);
        //}
    }
    /** declarete return */
    private void declarateResult(final Block block) {
        if (block.result != null) return;

        final ArrayList<Token> tokens = block.tokens;
        if (tokens != null && !tokens.isEmpty()) {
            for (int i = 0; i < tokens.size(); i++) {
                final Token currnetToken = tokens.get(i);
                if (currnetToken.type == TokenType.RETURN_VALUE) {
                    tokens.remove(i); // remove return

                    // add result
                    ArrayList<Token> resultValue = new ArrayList<>();
                    // next read right tokens
                    // TO:DO: in this parse assigns
                    while (i < tokens.size()) {
                        final Token nextToken = tokens.get(i);
                        if (nextToken.type == TokenType.ENDLINE) {
                            tokens.remove(i);
                            i--;
                            break;
                        } else {
                            resultValue.add(nextToken);
                            tokens.remove(i);
                        }
                    }
                    renameVariable(resultValue, block);
                    block.result = new BlockResult(resultValue);
                }
            }
            //
        }
        // local blocks
        if (block.localBlocks != null) {
            for (final Block localBlock : block.localBlocks)
                declarateResult(localBlock);
        }
    }
    /** parse variables */
    private void parseVariable(final Block block) {
        final ArrayList<Variable> variables = block.variables;
        if (variables != null && !variables.isEmpty())
            for (final Variable variable : variables)
                variable.setValue(block, blocks);
    }
    /** parse block dependency */
    private void parseBlockDependency(final int depth, final Block dependencyBlock) {
        if (dependencyBlock == null) return;

        final ArrayList<Block> dependencyBlocksBuffer = Block.getBlocks(dependencyBlock.dependencyBlocks, blocks);
        if (dependencyBlocksBuffer == null) return;

        final String depthTabs1 = "\t".repeat(depth);
        final String depthTabs2 = depthTabs1+'\t';
        for (final Block dependency : dependencyBlocksBuffer) {
            System.out.println(depthTabs1+dependency.name+':');
            parseBlockDependency(depth+1, dependency);

            // parse block
            // TO:DO: check block type
            parseVariable(dependency);
            if (dependency.result != null) {
                final ExpressionObject result = dependency.result.getValue(dependency, blocks);
                if (result != null && result.value != null)
                    System.out.println(depthTabs2 + "return: "+result.value.toString());
            }
        }
        //
    }
    /** parse local block */
    private void parseLocalBlock(final Block localBlock) {
        if (localBlock == null) return;

        // parse block
        parseBlockDependency(0, localBlock);
        parseVariable(localBlock);
        if (localBlock.result != null)
            localBlock.result.getValue(localBlock, blocks);
    }
    /** parse local block */
    private void parseLocalBlock(final int depth, final ArrayList<Block> localBlocks) {
        if (localBlocks == null || localBlocks.isEmpty()) return;

        final String depthTabs1 = "\t".repeat(depth);
        final String depthTabs2 = depthTabs1+'\t';

        for (final Block dependency : localBlocks) {
            System.out.println(depthTabs1+dependency.name+':');
            parseLocalBlock(depth+1, dependency.localBlocks);

            // parse block
            // TO:DO: check block type
            parseBlockDependency(depth+1, dependency);
            parseVariable(dependency);
            if (dependency.result != null) {
                final ExpressionObject result = dependency.result.getValue(dependency, blocks);
                if (result != null && result.value != null)
                    System.out.println(depthTabs2 + "return: "+result.value.toString());
            }
        }
        //
    }
    /** parse all block dependency */
    private void parseAllBlockDependency() {
        for (final Block dependencyBlock : blocks) {
            System.out.println("> "+dependencyBlock.name+':');

            parseBlockDependency(1, dependencyBlock);
            //parseLocalBlock(1, dependencyBlock.localBlocks);

            // parse block
            // TO:DO: fix bottom
            parseVariable(dependencyBlock);
            if (dependencyBlock.result != null) {
                ExpressionObject result = dependencyBlock.result.getValue(dependencyBlock, blocks);
                if (result != null && result.value != null)
                    System.out.println("\treturn: "+result.value.toString());
            }
        }
        //
    }
}
