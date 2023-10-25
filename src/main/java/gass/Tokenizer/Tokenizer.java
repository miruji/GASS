package gass.Tokenizer;

import gass.io.log.Log;
import gass.io.log.LogType;

import java.util.ArrayList;
import java.util.Objects;

public class Tokenizer {
    public final ArrayList<Token> tokens;
    private int counter = 0;
    private final String input;
    private final int inputLength;
    public Tokenizer(String input) {
        // read tokens
        tokens = new ArrayList<>();
        this.input = input;
        this.inputLength = input.length();

        while (counter < inputLength) {
            if ( !deleteComments() )
            if ( !addToken(getQuotes('`'), TokenType.BACK_QUOTE) )
            if ( !addToken(getQuotes('"'), TokenType.DOUBLE_QUOTE) )
            if ( !addToken(getQuotes('\''), TokenType.SINGLE_QUOTE) )
            if ( !addToken(getFloatNumber(), TokenType.FLOAT) )
            if ( !addToken(getNumber(), TokenType.NUMBER) )
            if ( !addToken(getWord(), TokenType.WORD) )
            if ( !addToken(getLogicalOperator(), TokenType.NONE) )
            if ( !addToken(getMathOperator(), TokenType.NONE) )
                {
                char c = input.charAt(counter);
                //
                if (c == '\u001F' && tokens.isEmpty())
                    counter++;
                else {
                    if (c == '\u001F' && tokens.get(tokens.size()-1).type != TokenType.ENDLINE)
                        addToken(" ", TokenType.ENDLINE);
                    else
                    // single math
                    if (c == '+')
                        addToken(" ", TokenType.PLUS);
                    else
                    if (c == '-')
                        addToken(" ", TokenType.MINUS);
                    else
                    if (c == '*')
                        addToken(" ", TokenType.MULTIPLY);
                    else
                    if (c == '/')
                        addToken(" ", TokenType.DEVIDE);
                    else
                    if (c == '=')
                        addToken(" ", TokenType.EQUAL);
                    else
                    if (c == '%')
                        addToken(" ", TokenType.MODULO);
                    else
                    // single logical
                    if (c == '?')
                        addToken(" ", TokenType.QUESTION);
                    else
                    if (c == '!')
                        addToken(" ", TokenType.NOT);
                    else
                    // brackets
                    if (c == '(')
                        addToken(" ", TokenType.PARAMETER_BEGIN);
                    else
                    if (c == ')')
                        addToken(" ", TokenType.PARAMETER_END);
                    else
                    if (c == '{')
                        addToken(" ", TokenType.CLASS_BEGIN);
                    else
                    if (c == '}')
                        addToken(" ", TokenType.CLASS_END);
                    else
                    if (c == '[')
                        addToken(" ", TokenType.ARRAY_BEGIN);
                    else
                    if (c == ']')
                        addToken(" ", TokenType.ARRAY_END);
                    else
                    //
                    if (c == ':')
                        addToken(" ", TokenType.BLOCK_BEGIN);
                    else
                    if (c == ',')
                        addToken(" ", TokenType.COMMA);
                    counter++;
                }
            }
            //
        }
        new Log(LogType.info,"Tokens size: "+tokens.size());
    }
    private boolean addToken(String token, TokenType type) {
        if (!token.isEmpty()) {
            if (token.equals(" ")) token = null;

            // preparser rename types
            if (type == TokenType.WORD) {
                if (Objects.equals(token, "end"))
                    type = TokenType.END;
                else
                if (Objects.equals(token, "private"))
                    type = TokenType.PRIVATE;
                else
                if (Objects.equals(token, "public"))
                    type = TokenType.PUBLIC;
                else
                if (Objects.equals(token, "return"))
                    type = TokenType.RETURN_VALUE;

                if (type != TokenType.WORD)
                    token = null;
            } else
            if (type == TokenType.NONE) {
                // double math
                if (Objects.equals(token, "++"))
                    type = TokenType.INCREMENT;
                else
                if (Objects.equals(token, "+="))
                    type = TokenType.PLUS_EQUALS;
                else
                if (Objects.equals(token, "--"))
                    type = TokenType.DECREMENT;
                else
                if (Objects.equals(token, "-="))
                    type = TokenType.MINUS_EQUALS;
                else
                if (Objects.equals(token, "*="))
                    type = TokenType.MULTIPLY_EQUALS;
                else
                if (Objects.equals(token, "/="))
                    type = TokenType.DIVIDE_EQUALS;
                else
                // double logical
                if (Objects.equals(token, "!="))
                    type = TokenType.NOT_EQUAL;
                else
                if (Objects.equals(token, "=="))
                    type = TokenType.DOUBLE_EQUAL;
                else
                if (Objects.equals(token, "&&"))
                    type = TokenType.AND;
                else
                if (Objects.equals(token, "||"))
                    type = TokenType.OR;

                if (type != TokenType.NONE)
                    token = null;
            }

            //
            tokens.add(new Token(token, type));
            return true;
        }
        return false;
    }
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
    private String getQuotes(char quote) { // get quotes `/"/'
        if (input.charAt(counter) == quote) {
            if (counter+1 >= inputLength) new Log(LogType.error,"[Tokenizer]: Quote was not closed at the end");

            StringBuilder result = new StringBuilder();
            boolean openSingleComment = false;

            while (counter < inputLength) {
                char currentChar = input.charAt(counter);
                result.append(currentChar);
                if (currentChar == quote) {
                    if (openSingleComment) {
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