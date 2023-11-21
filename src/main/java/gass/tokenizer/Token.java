package gass.tokenizer;

import java.util.*;

public class Token {
    public String data;                // word, block num ...
    public TokenType type;             // type
    public ArrayList<Token> childrens; // children tokens
    public Token(final String data, final TokenType type) {
        this.data = data;
        this.type = type;
    }
    public Token(final String data, final TokenType type, final ArrayList<Token> childrens) {
        this.data = data;
        this.type = type;
        this.childrens = childrens;
    }
    /** operators */
    private static final Set<TokenType> OPERATORS;
    static {
        OPERATORS = new HashSet<>();
        OPERATORS.add(TokenType.PLUS);
        OPERATORS.add(TokenType.MINUS);
        OPERATORS.add(TokenType.MULTIPLY);
        OPERATORS.add(TokenType.DIVIDE);
        OPERATORS.add(TokenType.EQUAL);
        OPERATORS.add(TokenType.MODULO);
        OPERATORS.add(TokenType.INCREMENT);
        OPERATORS.add(TokenType.PLUS_EQUALS);
        OPERATORS.add(TokenType.DECREMENT);
        OPERATORS.add(TokenType.MINUS_EQUALS);
        OPERATORS.add(TokenType.MULTIPLY_EQUALS);
        OPERATORS.add(TokenType.DIVIDE_EQUALS);
        OPERATORS.add(TokenType.QUESTION);
        OPERATORS.add(TokenType.NOT);
        OPERATORS.add(TokenType.NOT_EQUAL);
        OPERATORS.add(TokenType.DOUBLE_EQUAL);
        OPERATORS.add(TokenType.AND);
        OPERATORS.add(TokenType.OR);
    }
    /** check operator */
    public static boolean checkOperator(final TokenType type) {
        return OPERATORS.contains(type);
    }
    /** type to string */
    public static String typeToString(final TokenType type) {
        return switch (type) {
            // single math
            case PLUS -> "+";
            case MINUS -> "-";
            case MULTIPLY -> "*";
            case DIVIDE -> "/";
            case EQUAL -> "=";
            case MODULO -> "%";
            // double math
            case INCREMENT -> "++";
            case PLUS_EQUALS -> "+=";
            case DECREMENT -> "--";
            case MINUS_EQUALS -> "-=";
            case MULTIPLY_EQUALS -> "*=";
            case DIVIDE_EQUALS -> "/=";
            // single logical
            case QUESTION -> "?";
            case NOT -> "!";
            // double logical
            case NOT_EQUAL -> "!=";
            case DOUBLE_EQUAL -> "==";
            case AND -> "&&";
            case OR -> "||";
            // block
            case BLOCK_BEGIN -> ":";
            case CIRCLE_BLOCK_BEGIN -> "(";
            case CIRCLE_BLOCK_END -> ")";
            case FIGURE_BLOCK_BEGIN -> "{";
            case FIGURE_BLOCK_END -> "}";
            case SQUARE_BLOCK_BEGIN -> "[";
            case SQUARE_BLOCK_END -> "]";
            // default
            default -> "";
        };
    }
    /** string to type. using pretype */
    public static TokenType stringToType(final String data, final TokenizerTokenType type) {
        return switch (type) {
            case NUMBER -> TokenType.NUMBER;
            case FLOAT -> TokenType.FLOAT;
            case WORD -> switch (data) {
                case "end" -> TokenType.END;
                case "return" -> TokenType.RETURN_VALUE;
                case "func" -> TokenType.FUNCTION;
                case "proc" -> TokenType.PROCEDURE;
                case "private" -> TokenType.PRIVATE;
                case "public" -> TokenType.PUBLIC;
                case "enum" -> TokenType.ENUM;
                default -> TokenType.WORD;
            };
            case SINGLE_MATH -> switch (data) {
                case "+" -> TokenType.PLUS;
                case "-" -> TokenType.MINUS;
                case "*" -> TokenType.MULTIPLY;
                case "/" -> TokenType.DIVIDE;
                case "=" -> TokenType.EQUAL;
                case "%" -> TokenType.MODULO;
                default -> TokenType.NONE;
            };
            case DOUBLE_MATH -> switch (data) {
                case "++" -> TokenType.INCREMENT;
                case "+=" -> TokenType.PLUS_EQUALS;
                case "--" -> TokenType.DECREMENT;
                case "-=" -> TokenType.MINUS_EQUALS;
                case "*=" -> TokenType.MULTIPLY_EQUALS;
                case "/=" -> TokenType.DIVIDE_EQUALS;
                default -> TokenType.NONE;
            };
            case SINGLE_LOGICAL -> switch (data) {
                case "?" -> TokenType.QUESTION;
                case "!" -> TokenType.NOT;
                default -> TokenType.NONE;
            };
            case DOUBLE_LOGICAL -> switch (data) {
                case "!=" -> TokenType.NOT_EQUAL;
                case "==" -> TokenType.DOUBLE_EQUAL;
                case "&&" -> TokenType.AND;
                case "||" -> TokenType.OR;
                default -> TokenType.NONE;
            };
            case BACK_QUOTE -> TokenType.BACK_QUOTE;
            case SINGLE_QUOTE -> TokenType.SINGLE_QUOTE;
            case DOUBLE_QUOTE -> TokenType.DOUBLE_QUOTE;
            case BLOCK_BEGIN -> TokenType.BLOCK_BEGIN;
            case CIRCLE_BLOCK -> {
                if (Objects.equals(data, "("))
                    yield TokenType.CIRCLE_BLOCK_BEGIN;
                yield TokenType.CIRCLE_BLOCK_END;
            }
            case FIGURE_BLOCK -> {
                if (Objects.equals(data, "{"))
                    yield TokenType.FIGURE_BLOCK_BEGIN;
                yield TokenType.FIGURE_BLOCK_END;
            }
            case SQUARE_BLOCK -> {
                if (Objects.equals(data, "["))
                    yield TokenType.SQUARE_BLOCK_BEGIN;
                yield TokenType.SQUARE_BLOCK_END;
            }
            case ENDLINE -> TokenType.ENDLINE;
            case COMMA -> TokenType.COMMA;
            case DOT -> TokenType.DOT;
            default -> TokenType.NONE;
        };
    }
    /** add child token to this token */
    public void addChildren(final Token child) {
        if (childrens == null) childrens = new ArrayList<>();
        childrens.add(child);
    }
    /** add child tokens to this token */
    public void addChildrens(final ArrayList<Token> childrens) {
        for (final Token children : childrens)
            addChildren(children);
    }
    /** tokens tree output */
    public static String outputChildrens(final Token token, final int depth) {
        final StringBuilder output = new StringBuilder();
        output.append("\t".repeat(Math.max(0, depth)));

        if (token.data != null && !token.data.isEmpty())
            output.append(token.type).append(" [").append(token.data).append("]\n");
        else
            output.append(token.type).append('\n');

        if (token.childrens != null && !token.childrens.isEmpty())
            for (final Token child : token.childrens)
                output.append(outputChildrens(child, depth+1));
        return output.toString();
    }
    /** tokens to string */
    public static String tokensToString(final ArrayList<Token> tokens, final boolean readChildrens) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            final Token token = tokens.get(i);
            final TokenType type = token.type;

            //
            if (List.of(TokenType.BLOCK_ASSIGN, TokenType.FUNCTION_ASSIGN, TokenType.PROCEDURE_ASSIGN).contains(type) &&
               ( (i+1 < tokens.size() && !List.of(TokenType.CIRCLE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_BEGIN).contains( tokens.get(i+1).type )) ||
                  i+1 == tokens.size()) ) {
                result.append("LB["); // local block assign
                result.append(token.data != null ? token.data : typeToString(type));
                result.append(']');
                continue;
            }
            // else -> basic
            result.append(token.data != null ? token.data : typeToString(type));

            // if no ( { [ or ) } ]
            if (!List.of(TokenType.CIRCLE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_BEGIN).contains(type) && i+1 < tokens.size() &&
                !List.of(TokenType.CIRCLE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_BEGIN,
                         TokenType.CIRCLE_BLOCK_END, TokenType.FIGURE_BLOCK_END, TokenType.SQUARE_BLOCK_END,
                         TokenType.BLOCK_BEGIN).contains( tokens.get(i+1).type )) {
                    result.append(' ');
                }

            // childrens
            if (readChildrens) {
                if (token.childrens != null && !token.childrens.isEmpty())
                    result.append( tokensToString(token.childrens, true) );
                // add close
                if (token.type == TokenType.CIRCLE_BLOCK_BEGIN) result.append(')');
                else if (token.type == TokenType.FIGURE_BLOCK_BEGIN) result.append('}');
                else if (token.type == TokenType.SQUARE_BLOCK_BEGIN) result.append(']');

                if (List.of(TokenType.CIRCLE_BLOCK_BEGIN, TokenType.FIGURE_BLOCK_BEGIN, TokenType.SQUARE_BLOCK_BEGIN, TokenType.BLOCK_BEGIN).contains( type ) &&
                    i+1 < tokens.size() && Token.checkOperator(tokens.get(i+1).type))
                    result.append(' ');
            }
        }
        return result.toString();
    }
}
