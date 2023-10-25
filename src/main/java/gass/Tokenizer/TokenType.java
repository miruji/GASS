package gass.Tokenizer;

public enum TokenType {
    // Tokenizer types
        // basic
            NONE,    // none
            WORD,    // word
            NUMBER,  // number
            FLOAT,   // float number
            ENDLINE, // endline
        // quotes
            BACK_QUOTE,   // `
            DOUBLE_QUOTE, // "
            SINGLE_QUOTE, // '
        // single math
            PLUS,     // +
            MINUS,    // -
            MULTIPLY, // *
            DEVIDE,   // /
            EQUAL,    // =
            MODULO,   // %
        // double math
            INCREMENT,       // ++
            PLUS_EQUALS,     // +=
            DECREMENT,       // --
            MINUS_EQUALS,    // -=
            MULTIPLY_EQUALS, // *=
            DIVIDE_EQUALS,   // /=
        // single logical
            QUESTION, // ?
            NOT,      // !
        // double logical
            NOT_EQUAL,    // !=
            DOUBLE_EQUAL, // ==
            AND,          // &&
            OR,           // ||
    // Parser types
        // parameter
            PARAMETER_BEGIN, // (
            PARAMETER_END,   // )
        // array
            ARRAY_BEGIN, // [
            ARRAY_END,   // ]
        // class
            CLASS_BEGIN, // {
            CLASS_END,   // }
            PRIVATE,     // private
            PUBLIC,      // public
        // block
            BLOCK_BEGIN, // :
            END,         // end
    // ~
        COMMA, // ,
        RETURN_VALUE
}