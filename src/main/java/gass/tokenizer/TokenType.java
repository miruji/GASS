package gass.tokenizer;

/** Token types */
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
            CIRCLE_BLOCK_BEGIN, // (
            CIRCLE_BLOCK_END,   // )
        // array
            SQUARE_BLOCK_BEGIN, // [
            SQUARE_BLOCK_END,   // ]
        // class
            FIGURE_BLOCK_BEGIN, // {
            FIGURE_BLOCK_END,   // }
            PRIVATE,     // private
            PUBLIC,      // public
        // enum
            ENUM,
        // block
            BLOCK_BEGIN, // :
            END,         // end
        // function & procedure
            FUNCTION,
            FUNCTION_ASSIGN,
            PROCEDURE,
            PROCEDURE_ASSIGN,
            NONE_ASSIGN,
    // ~
        COMMA, // ,
        DOT,   // .
        RETURN_VALUE // return
}