package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class BlockResult {
    public final Expression expression;
    public ExpressionObject value;
    public BlockResult(final ArrayList<Token> tokens) {
        this.expression = new Expression(tokens);
    }
    public void setValue(final Block block, final ArrayList<Block> blocks) {
        if (value == null)
            value = expression.getValue(expression.value, block, blocks);
    }
}
