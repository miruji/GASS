package gass.Tokenizer;

public enum TokenType {
    // Tokenizer types
        // basic
        word,
        number,
        floatNumber,
        endline,
        quote,
        // operator
        singleMathOperator,
        doubleMathOperator,
        singleLogicalOperator,
        doubleLogicalOperator,
    // Parser types
        // parameter
        parameterBegin,
        parameterEnd,
        // array
        arrayBegin,
        arrayEnd,
        // class
        classBegin,
        classEnd,
        // block
        blockBegin,
        blockEnd,
    // ~
        comma,
        returnValue
}