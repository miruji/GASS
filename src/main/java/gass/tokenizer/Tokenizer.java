package gass.tokenizer;

import gass.io.log.Log;
import gass.io.log.LogType;

import java.util.ArrayList;

public class Tokenizer {
    public final ArrayList<Token> tokens; // tokenizer output tokens
    private int counter = 0;              // circle counter
    private final String input;           // tokenizer input text
    private final int inputLength;        // save input text length
    public Tokenizer(final String input) {
        //
        tokens = new ArrayList<>();
        this.input = input;
        this.inputLength = input.length();

        // main read cycle
        while (counter < inputLength) {
            if ( !deleteComments() )
            if ( !addToken(getQuotes('`'), TokenizerTokenType.BACK_QUOTE, false) )    // read ` quote
            if ( !addToken(getQuotes('"'), TokenizerTokenType.DOUBLE_QUOTE, false) )  // read " quote
            if ( !addToken(getQuotes('\''), TokenizerTokenType.SINGLE_QUOTE, false) ) // read ' quote
            if ( !addToken(getFloatNumber(), TokenizerTokenType.FLOAT, false) )       // read float 0.0 or .0
            if ( !addToken(getNumber(), TokenizerTokenType.NUMBER, false) )           // read number
            if ( !addToken(getWord(), TokenizerTokenType.WORD, false) )               // read word
            if ( !addToken(getLogicalOperator(), TokenizerTokenType.DOUBLE_LOGICAL, false) ) // read logical (double) -> next set new type
            if ( !addToken(getMathOperator(), TokenizerTokenType.DOUBLE_MATH, false) )       // read math (double) -> next set new type
                {
                // next read single chars and endline only
                final char c = input.charAt(counter);
                if (c == '\u001F' && tokens.isEmpty()) counter++;
                else {
                    // endline
                    if (c == '\u001F' && tokens.get(tokens.size()-1).type != TokenType.ENDLINE)
                        addToken(" ", TokenizerTokenType.ENDLINE, true);
                    else
                    // single math
                    if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '%')
                        addToken(c+"", TokenizerTokenType.SINGLE_MATH, true);
                    else
                    // single logical
                    if (c == '?' || c == '!')
                        addToken(c+"", TokenizerTokenType.SINGLE_LOGICAL, true);
                    else
                    // brackets
                    if (c == '(' || c == ')')
                        addToken(c+"", TokenizerTokenType.CIRCLE_BLOCK, true);
                    else
                    if (c == '{' || c == '}')
                        addToken(c+"", TokenizerTokenType.FIGURE_BLOCK, true);
                    else
                    if (c == '[' || c == ']')
                        addToken(c+"", TokenizerTokenType.SQUARE_BLOCK, true);
                    else
                    // ~
                    if (c == ':')
                        addToken(":", TokenizerTokenType.BLOCK_BEGIN, true);
                    else
                    if (c == ';')
                        addToken(";", TokenizerTokenType.ENDLINE, true);
                    else
                    if (c == ',')
                        addToken(",", TokenizerTokenType.COMMA, true);
                    else
                    if (c == '.')
                        addToken(".", TokenizerTokenType.DOT, true);
                    //
                    counter++;
                }
            }
            //
        }
        new Log(LogType.info,"Tokens size: "+tokens.size());
    }
    /** add new tokens if no empty / if == " " then set new type */
    private boolean addToken(String data, final TokenizerTokenType type, final boolean clearData) {
        if (data != null && !data.isEmpty()) {
            // preparser rename types
            final TokenType newType  = Token.stringToType(data, type);
            if (clearData) data = null;
            //
            tokens.add(new Token(data, newType));
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
                    if (input.charAt(counter) == '\u001F')
                        return true;
                }
            } else
            if (input.charAt(counter+1) == '*') { // double comment (TO:DO: \* *\ \** **\ and more ... )
                counter++;
                while (counter < inputLength) {
                    counter++;
                    if (input.charAt(counter) == '*' && input.charAt(counter+1) == '\\') { // TO:DO: -> fix double \* \* error
                        counter++;
                        return true;
                    }
                }
            } else return false;
        }
        return false;
    }
    /** get quote ` and " and ' */
    private String getQuotes(char quote) { // get quotes `/"/'
        if (input.charAt(counter) == quote) {
            if (counter+1 >= inputLength) new Log(LogType.error,"[Tokenizer]: Quote was not closed at the end");

            final StringBuilder result = new StringBuilder();
            boolean openSingleComment = false;

            while (counter < inputLength) {
                final char currentChar = input.charAt(counter);
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
                        return result.deleteCharAt(0).deleteCharAt(result.length()-1).toString();
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
        final StringBuilder result = new StringBuilder();
        boolean dot = false;

        int counterBuffer = counter;
        while (counterBuffer < inputLength) {
            final char currentChar = input.charAt(counterBuffer);
            final char nextChar = (counterBuffer+1 < inputLength) ? input.charAt(counterBuffer+1) : '\0';

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
        final StringBuilder result = new StringBuilder();

        while (counter < inputLength) {
            final char currentChar = input.charAt(counter);
            if (Character.isDigit(currentChar)) {
                result.append(currentChar);
                counter++;
            } else break;
        }

        return result.toString();
    }
    /** get word */
    private String getWord() {
        final StringBuilder result = new StringBuilder();

        while (counter < inputLength) {
            final char currentChar = input.charAt(counter);
            final char nextChar = (counter+1 < inputLength) ? input.charAt(counter+1) : '\0';

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
        final char nextChar = input.charAt(counter+1);
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
        final char nextChar = input.charAt(counter+1);
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