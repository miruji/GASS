package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class Variable {
    public final String name;
    public ExpressionType type;
    public Expression value;
    public ExpressionObject resultValue;
    public Variable(final String name, final ExpressionType type, final ArrayList<Token> value) {
        this.name = name;
        this.type = type;
        this.value = new Expression(value);
    }
    public void setValue(final Block block, final ArrayList<Block> blocks) {
        if (resultValue == null)
            resultValue = value.getValue(value.value, block, blocks);
    }
    public ExpressionObject getValue() {
        return resultValue;
    }
}
