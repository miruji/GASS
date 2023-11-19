package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class BlockResult {
    public final Expression expression;
    public ExpressionObject value;
    public BlockResult(final ArrayList<Token> tokens) {
        this.expression = new Expression(tokens);
    }
    public ExpressionObject getValue(final Block block, final ArrayList<Block> blocks) {
        if (value == null)
            value = expression.getValue(block, blocks);
        return value;
    }
}
