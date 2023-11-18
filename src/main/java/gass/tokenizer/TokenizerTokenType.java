package gass.tokenizer;

public enum TokenizerTokenType {
    // basic
        NONE,    // none
        WORD,    // word
        NUMBER,  // number
        FLOAT,   // float number
        ENDLINE, // endline
    // math & logical
        SINGLE_MATH, // single math op
        DOUBLE_MATH, // double math op
        SINGLE_LOGICAL, // single logical op
        DOUBLE_LOGICAL, // double logical op
    // quote
        BACK_QUOTE,   // `
        SINGLE_QUOTE, // '
        DOUBLE_QUOTE, // "
    // block
        BLOCK_BEGIN, // :
        CIRCLE_BLOCK, // ()
        FIGURE_BLOCK, // {}
        SQUARE_BLOCK, // []
    // ~
        COMMA, // ,
        DOT,   // .
}
