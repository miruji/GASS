package gass.parser;

public enum ExpressionType {
    NONE,
    // type
        NUMBER,
        STRING,
        CHAR,
    // logical
        GREATER_THAN,
        LESS_THAN,
    // math
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
}
