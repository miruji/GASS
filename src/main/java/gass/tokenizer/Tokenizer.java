package gass.tokenizer;

import gass.io.log.Log;
import gass.io.log.LogType;

import java.util.ArrayList;
import java.util.Objects;

public class Tokenizer {
    public final ArrayList<Token> tokens; // tokenizer output tokens
    private int counter = 0;              // circle counter
    private final String input;           // tokenizer input text
    private final int inputLength;        // save input text length
    public Tokenizer(String input) {
        //
        tokens = new ArrayList<>();
        this.input = input;
        this.inputLength = input.length();

        // main read cycle
        while (counter < inputLength) {
            if ( !deleteComments() )
            if ( !addToken(getQuotes('`'), TokenType.BACK_QUOTE) )    // read ` quote
            if ( !addToken(getQuotes('"'), TokenType.DOUBLE_QUOTE) )  // read " quote
            if ( !addToken(getQuotes('\''), TokenType.SINGLE_QUOTE) ) // read ' quote
            if ( !addToken(getFloatNumber(), TokenType.FLOAT) )       // read float 0.0 or .0
            if ( !addToken(getNumber(), TokenType.NUMBER) )           // read number
            if ( !addToken(getWord(), TokenType.WORD) )               // read word
            if ( !addToken(getLogicalOperator(), TokenType.NONE) )    // read logical (double) -> next set new type
            if ( !addToken(getMathOperator(), TokenType.NONE) )       // read math (double) -> next set new type
                {
                // next read single chars
                char c = input.charAt(counter);
                if (c == '\u001F' && tokens.isEmpty()) counter++;
                else {
                    if (c == '\u001F' && tokens.get(tokens.size()-1).type != TokenType.ENDLINE)
                        addToken(" ", TokenType.ENDLINE);
                    else
                    // single math
                    if (c == '+') addToken(" ", TokenType.PLUS);
                    else
                    if (c == '-') addToken(" ", TokenType.MINUS);
                    else
                    if (c == '*') addToken(" ", TokenType.MULTIPLY);
                    else
                    if (c == '/') addToken(" ", TokenType.DEVIDE);
                    else
                    if (c == '=') addToken(" ", TokenType.EQUAL);
                    else
                    if (c == '%') addToken(" ", TokenType.MODULO);
                    else
                    // single logical
                    if (c == '?') addToken(" ", TokenType.QUESTION);
                    else
                    if (c == '!') addToken(" ", TokenType.NOT);
                    else
                    // brackets
                    if (c == '(') addToken(" ", TokenType.CIRCLE_BLOCK_BEGIN);
                    else
                    if (c == ')') addToken(" ", TokenType.CIRCLE_BLOCK_END);
                    else
                    if (c == '{') addToken(" ", TokenType.FIGURE_BLOCK_BEGIN);
                    else
                    if (c == '}') addToken(" ", TokenType.FIGURE_BLOCK_END);
                    else
                    if (c == '[') addToken(" ", TokenType.SQUARE_BLOCK_BEGIN);
                    else
                    if (c == ']') addToken(" ", TokenType.SQUARE_BLOCK_END);
                    else
                    //
                    if (c == ':') addToken(" ", TokenType.BLOCK_BEGIN);
                    else
                    if (c == ';') addToken(" ", TokenType.ENDLINE);
                    else
                    if (c == ',') addToken(" ", TokenType.COMMA);
                    else
                    if (c == '.') addToken(" ", TokenType.DOT);
                    counter++;
                }
            }
            //
        }
        new Log(LogType.info,"Tokens size: "+tokens.size());
    }
    /** add new tokens if no empty / if == " " then set new type */
    private boolean addToken(String data, TokenType type) {
        if (!data.isEmpty()) {
            if (data.equals(" ")) data = null;

            // preparser rename types
            if (type == TokenType.WORD) {
                if (Objects.equals(data, "end")) type = TokenType.END;
                else
                if (Objects.equals(data, "return")) type = TokenType.RETURN_VALUE;
                else
                if (Objects.equals(data, "func")) type = TokenType.FUNCTION;
                else
                if (Objects.equals(data, "proc")) type = TokenType.PROCEDURE;
                else
                if (Objects.equals(data, "private")) type = TokenType.PRIVATE;
                else
                if (Objects.equals(data, "public")) type = TokenType.PUBLIC;
                else
                if (Objects.equals(data, "enum")) type = TokenType.ENUM;

                if (type != TokenType.WORD) data = null;
            } else
            if (type == TokenType.NONE) {
                // double math
                if (Objects.equals(data, "++")) type = TokenType.INCREMENT;
                else
                if (Objects.equals(data, "+=")) type = TokenType.PLUS_EQUALS;
                else
                if (Objects.equals(data, "--")) type = TokenType.DECREMENT;
                else
                if (Objects.equals(data, "-=")) type = TokenType.MINUS_EQUALS;
                else
                if (Objects.equals(data, "*=")) type = TokenType.MULTIPLY_EQUALS;
                else
                if (Objects.equals(data, "/=")) type = TokenType.DIVIDE_EQUALS;
                else
                // double logical
                if (Objects.equals(data, "!=")) type = TokenType.NOT_EQUAL;
                else
                if (Objects.equals(data, "==")) type = TokenType.DOUBLE_EQUAL;
                else
                if (Objects.equals(data, "&&")) type = TokenType.AND;
                else
                if (Objects.equals(data, "||")) type = TokenType.OR;

                if (type != TokenType.NONE) data = null;
            }
            //
            tokens.add(new Token(data, type));
            return true;
        }
        return false;
    }
    /** delete \**\ (double) and \\ (single) comments */
    private boolean deleteComments() {
        if (input.charAt(counter) == '\\') {
            if (counter+1 >= inputLength) return true;

            if (input.charAt(counter+1) == '\\') { // single comment
                counter++;
                while (counter < inputLength) {
                    counter++;
                    if (input.charAt(counter) == '\u001F') {
                        return true;
                    }
                }
            } else
            if (input.charAt(counter+1) == '*') { // double comment
                counter++;
                while (counter < inputLength) {
                    counter++;
                    if (input.charAt(counter) == '*' && input.charAt(counter+1) == '\\') {
                        counter++;
                        return true;
                    }
                }
            }
            else return false;
        }
        return false;
    }
    /** get quote ` and " and ' */
    private String getQuotes(char quote) { // get quotes `/"/'
        if (input.charAt(counter) == quote) {
            if (counter+1 >= inputLength) new Log(LogType.error,"[Tokenizer]: Quote was not closed at the end");

            StringBuilder result = new StringBuilder();
            boolean openSingleComment = false;

            while (counter < inputLength) {
                char currentChar = input.charAt(counter);
                result.append(currentChar);
                if (currentChar == quote) {
                    boolean noSlash = true;
                    // check back slash of end quote
                    if (input.charAt(counter-1) == '\\') {
                        int backSlashCounter = 0;
                        for (int i = counter-1; i >= 0; i--) {
                            if (input.charAt(i) == '\\') backSlashCounter++;
                            else break;
                        }
                        if (backSlashCounter % 2 == 1)
                            noSlash = false;
                    }
                    //
                    if (openSingleComment && noSlash) {
                        counter++;
                        return result.toString();
                    } else openSingleComment = true;
                }
                counter++;
            }
            if (openSingleComment) new Log(LogType.error,"[Tokenizer]: Quote was not closed at the end");
        }
        return "";
    }
    /** get float 0.0 and .0 */
    private String getFloatNumber() {
        StringBuilder result = new StringBuilder();
        boolean dot = false;

        int counterBuffer = counter;
        while (counterBuffer < inputLength) {
            char currentChar = input.charAt(counterBuffer);
            char nextChar = (counterBuffer+1 < inputLength) ? input.charAt(counterBuffer+1) : '\0';

            if (Character.isDigit(currentChar)) {
                result.append(currentChar);
                counterBuffer++;
            } else if (currentChar == '.' && !dot && Character.isDigit(nextChar)) {
                dot = true;
                result.append(currentChar);
                counterBuffer++;
            } else break;
        }

        if (dot) {
            counter = counterBuffer;
            return result.toString();
        }
        return "";
    }
    /** get number */
    private String getNumber() {
        StringBuilder result = new StringBuilder();

        while (counter < inputLength) {
            char currentChar = input.charAt(counter);
            if (Character.isDigit(currentChar)) {
                result.append(currentChar);
                counter++;
            } else break;
        }

        return result.toString();
    }
    /** get word */
    private String getWord() {
        StringBuilder result = new StringBuilder();

        while (counter < inputLength) {
            char currentChar = input.charAt(counter);
            char nextChar = (counter+1 < inputLength) ? input.charAt(counter+1) : '\0';

            if (Character.isLetterOrDigit(currentChar) ||
                    (currentChar == '_' && !result.isEmpty() && Character.isLetterOrDigit(nextChar))) {
                result.append(currentChar);
                counter++;
            } else break;
        }

        return result.toString();
    }
    /** get double math operator */
    private String getMathOperator() {
        if (counter+1 >= inputLength) return "";

        String result;
        char nextChar = input.charAt(counter+1);
        switch (input.charAt(counter)) {
            case '+' -> result = nextChar == '=' ? "+=" : (nextChar == '+' ? "++" : "");
            case '-' -> result = nextChar == '=' ? "-=" : (nextChar == '-' ? "--" : "");
            case '*' -> result = nextChar == '=' ? "*=" : "";
            case '/' -> result = nextChar == '=' ? "/=" : "";
            default -> {
                return "";
            }
        }

        if (!result.isEmpty()) counter+=2;
        return result;
    }
    /** get double logical operator */
    private String getLogicalOperator() {
        if (counter+1 >= inputLength) return "";

        String result;
        char nextChar = input.charAt(counter+1);
        switch (input.charAt(counter)) {
            case '!' -> result = nextChar == '=' ? "!=" : "";
            case '=' -> result = nextChar == '=' ? "==" : "";
            case '&' -> result = nextChar == '&' ? "&&" : "";
            case '|' -> result = nextChar == '|' ? "||" : "";
            default -> {
                return "";
            }
        }

        if (!result.isEmpty()) counter+=2;
        return result;
    }
}