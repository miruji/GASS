package gass.parser;

import gass.tokenizer.Token;
import java.util.ArrayList;

public class Variable {
    public final String name;
    public ExpressionType type;
    public Expression value;
    public ExpressionObject resultValue;
    public final String blockName;
    public Variable(final String name, final ExpressionType type, final ArrayList<Token> value, final String blockName) {
        this.name = name;
        this.type = type;
        this.value = new Expression(value);
        this.blockName = blockName;
    }
    public Variable(final String name, final ExpressionType type, final String blockName) {
        this.name = name;
        this.type = type;
        this.blockName = blockName;
    }
    public void setValue(final Block block, final ArrayList<Block> blocks) {
        if (resultValue == null && value != null) // if you need -> set resulValue to null for new calculation
            resultValue = value.getValue(value.value, block, blocks);
    }
}
