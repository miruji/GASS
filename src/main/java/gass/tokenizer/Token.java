package gass.tokenizer;

import java.util.ArrayList;
import java.util.Objects;

public class Token {
    public String data;                // word, block num ...
    public TokenType type;             // type
    public ArrayList<Token> childrens; // children tokens
    public Token(String data, TokenType type) {
        this.data = data;
        this.type = type;
    }
    public Token(String data, TokenType type, ArrayList<Token> childrens) {
        this.data = data;
        this.type = type;
        this.childrens = childrens;
    }
    /** type to string */
    public static String typeToString(TokenType type) {
        // single math
        if (type == TokenType.PLUS) return "+";
        if (type == TokenType.MINUS) return "-";
        if (type == TokenType.MULTIPLY) return "*";
        if (type == TokenType.DIVIDE) return "/";
        if (type == TokenType.EQUAL) return "=";
        if (type == TokenType.MODULO) return "%";
        // double math
        if (type == TokenType.INCREMENT) return "++";
        if (type == TokenType.PLUS_EQUALS) return "+=";
        if (type == TokenType.DECREMENT) return "--";
        if (type == TokenType.MINUS_EQUALS) return "-=";
        if (type == TokenType.MULTIPLY_EQUALS) return "*=";
        if (type == TokenType.DIVIDE_EQUALS) return "/=";
        // single logical
        if (type == TokenType.QUESTION) return "?";
        if (type == TokenType.NOT) return "!";
        // double logical
        if (type == TokenType.NOT_EQUAL) return "!=";
        if (type == TokenType.DOUBLE_EQUAL) return "==";
        if (type == TokenType.AND) return "&&";
        if (type == TokenType.OR) return "||";
        //
        return "";
    }
    /** string to type. using pretype */
    public static TokenType stringToType(String data, TokenizerTokenType type) {
        // words
        if (type == TokenizerTokenType.NUMBER) return TokenType.NUMBER; else
        if (type == TokenizerTokenType.FLOAT)  return TokenType.FLOAT; else
        if (type == TokenizerTokenType.WORD) {
            if (Objects.equals(data, "end")) return TokenType.END;
            if (Objects.equals(data, "return")) return TokenType.RETURN_VALUE;
            if (Objects.equals(data, "func")) return TokenType.FUNCTION;
            if (Objects.equals(data, "proc")) return TokenType.PROCEDURE;
            if (Objects.equals(data, "private")) return TokenType.PRIVATE;
            if (Objects.equals(data, "public")) return TokenType.PUBLIC;
            if (Objects.equals(data, "enum")) return TokenType.ENUM;
            else return TokenType.WORD;
        } else
        // single math
        if (type == TokenizerTokenType.SINGLE_MATH) {
            if (Objects.equals(data, "+")) return TokenType.PLUS;
            if (Objects.equals(data, "-")) return TokenType.MINUS;
            if (Objects.equals(data, "*")) return TokenType.MULTIPLY;
            if (Objects.equals(data, "/")) return TokenType.DIVIDE;
            if (Objects.equals(data, "=")) return TokenType.EQUAL;
            if (Objects.equals(data, "%")) return TokenType.MODULO;
        } else
        // double math
        if (type == TokenizerTokenType.DOUBLE_MATH) {
            if (Objects.equals(data, "++")) return TokenType.INCREMENT;
            if (Objects.equals(data, "+=")) return TokenType.PLUS_EQUALS;
            if (Objects.equals(data, "--")) return TokenType.DECREMENT;
            if (Objects.equals(data, "-=")) return TokenType.MINUS_EQUALS;
            if (Objects.equals(data, "*=")) return TokenType.MULTIPLY_EQUALS;
            if (Objects.equals(data, "/=")) return TokenType.DIVIDE_EQUALS;
        } else
        // single logical
        if (type == TokenizerTokenType.SINGLE_LOGICAL) {
            if (Objects.equals(data, "?")) return TokenType.QUESTION;
            if (Objects.equals(data, "!")) return TokenType.NOT;
        } else
        // double logical
        if (type == TokenizerTokenType.DOUBLE_LOGICAL) {
            if (Objects.equals(data, "!=")) return TokenType.NOT_EQUAL;
            if (Objects.equals(data, "==")) return TokenType.DOUBLE_EQUAL;
            if (Objects.equals(data, "&&")) return TokenType.AND;
            if (Objects.equals(data, "||")) return TokenType.OR;
        } else
        // quote
        if (type == TokenizerTokenType.BACK_QUOTE)   return TokenType.BACK_QUOTE;   else
        if (type == TokenizerTokenType.SINGLE_QUOTE) return TokenType.SINGLE_QUOTE; else
        if (type == TokenizerTokenType.DOUBLE_QUOTE) return TokenType.DOUBLE_QUOTE; else
        // block
        if (type == TokenizerTokenType.BLOCK_BEGIN) return TokenType.BLOCK_BEGIN; else
        if (type == TokenizerTokenType.CIRCLE_BLOCK) {
            if (Objects.equals(data, "(")) return TokenType.CIRCLE_BLOCK_BEGIN;
            else                              return TokenType.CIRCLE_BLOCK_END;
        }
        else
        if (type == TokenizerTokenType.FIGURE_BLOCK) {
            if (Objects.equals(data, "(")) return TokenType.FIGURE_BLOCK_BEGIN;
            else                              return TokenType.FIGURE_BLOCK_END;
        }
        else
        if (type == TokenizerTokenType.SQUARE_BLOCK) {
            if (Objects.equals(data, "(")) return TokenType.SQUARE_BLOCK_BEGIN;
            else                              return TokenType.SQUARE_BLOCK_END;
        }
        else
        // endline
        if (type == TokenizerTokenType.ENDLINE) return TokenType.ENDLINE; else
        // ~
        if (type == TokenizerTokenType.COMMA) return TokenType.COMMA; else
        if (type == TokenizerTokenType.DOT) return TokenType.DOT;
        //
        return TokenType.NONE;
    }
    /** add child token to this token */
    public void addChildren(Token child) {
        if (childrens == null) childrens = new ArrayList<>();
        childrens.add(child);
    }
    /** add child tokens to this token */
    public void addChildrens(ArrayList<Token> childrens) {
        for (Token children : childrens)
            addChildren(children);
    }
    /** tokens tree output */
    public static String outputChildrens(Token token, int depth) {
        StringBuilder output = new StringBuilder();
        output.append("\t".repeat(Math.max(0, depth)));

        if (token.data != null)
            output.append(token.type).append(" [").append(token.data).append("]\n");
        else
            output.append(token.type).append('\n');

        if (token.childrens != null) {
            for (Token child : token.childrens)
                output.append(outputChildrens(child, depth+1));
        }
        return output.toString();
    }
}
