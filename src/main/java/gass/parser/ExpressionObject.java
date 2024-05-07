package gass.parser;

public class ExpressionObject {
    public Object value;
    public final ExpressionType type;
    public ExpressionObject(ExpressionType type, Object value) {
        this.value = value;
        this.type = type;
    }
    public ExpressionObject(ExpressionType type) {
        this.type = type;
    }
}
