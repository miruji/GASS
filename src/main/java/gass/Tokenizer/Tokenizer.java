package gass.Tokenizer;

import gass.io.log.Log;
import gass.io.log.LogType;

import java.util.ArrayList;

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
            if ( !addToken(getQuotes('`'), TokenType.quote) )
            if ( !addToken(getQuotes('"'), TokenType.quote) )
            if ( !addToken(getQuotes('\''), TokenType.quote) )
            if ( !addToken(getFloatNumber(), TokenType.floatNumber) )
            if ( !addToken(getNumber(), TokenType.number) )
            if ( !addToken(getWord(), TokenType.word) )
            if ( !addToken(getLogicalOperator(), TokenType.doubleLogicalOperator) )
            if ( !addToken(getMathOperator(), TokenType.doubleMathOperator) )
                {
                char c = input.charAt(counter);
                //
                if (c == '\u001F' && tokens.isEmpty())
                    counter++;
                else {
                    if (c == '\u001F' && tokens.get(tokens.size()-1).type != TokenType.endline)
                        addToken(String.valueOf(c), TokenType.endline);
                    else
                    if (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '%')
                        addToken(String.valueOf(c), TokenType.singleMathOperator);
                    else
                    if (c == '?' || c == '!')
                        addToken(String.valueOf(c), TokenType.singleLogicalOperator);
                    else
                    if (c == '(')
                        addToken(String.valueOf(c), TokenType.parameterBegin);
                    else
                    if (c == ')')
                        addToken(String.valueOf(c), TokenType.parameterEnd);
                    else
                    if (c == '{')
                        addToken(String.valueOf(c), TokenType.classBegin);
                    else
                    if (c == '}')
                        addToken(String.valueOf(c), TokenType.classEnd);
                    else
                    if (c == '[')
                        addToken(String.valueOf(c), TokenType.arrayBegin);
                    else
                    if (c == ']')
                        addToken(String.valueOf(c), TokenType.arrayEnd);
                    else
                    if (c == ':')
                        addToken(String.valueOf(c), TokenType.blockBegin);
                    else
                    if (c == ',')
                        addToken(String.valueOf(c), TokenType.comma);
                    counter++;
                }
            }
            //
        }
        new Log(LogType.info,"Tokens size: "+tokens.size());
    }
    private boolean addToken(String token, TokenType type) {
        if (!token.isEmpty()) {
            // preparser rename types
            if (type == TokenType.word && token.equals("end"))
                type = TokenType.blockEnd;
            if (type == TokenType.word && token.equals("return"))
                type = TokenType.returnValue;
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
            case '*' -> result = nextChar == '=' ? "*=" : (nextChar == '*' ? "**" : "");
            case '/' -> result = nextChar == '=' ? "/=" : (nextChar == '/' ? "//" : "");
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