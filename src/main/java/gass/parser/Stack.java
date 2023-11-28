package gass.parser;

import gass.tokenizer.TokenType;

import java.util.ArrayList;

public class Stack {
    public final StackType type;
    public final ArrayList<ExpressionObject> values;
    public StringBuilder data;
    public Stack(final StackType type, final ArrayList<ExpressionObject> values) {
        this.type = type;
        this.values = values;
    }
    public static String typeToString(final StackType type) {
        return switch (type) {
            // single math
            case CALL -> "call";
            // default
            default -> "";
        };
    }
    public String toString(final String num, final String indent) {
        if (values == null || values.isEmpty()) return null;
        final StringBuilder result = new StringBuilder();
        this.data = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            // call
            if (type == StackType.CALL) {
                final String callName = values.get(0).value.toString();
                final String callName2 = callName+'_'+num;
                if (i == 0) {
                    result.append(indent).append("# ").append(callName2).append("\n");
                    continue;
                }

                // print
                if (callName.equals("print") || callName.equals("println")) {
                    final String dataName = callName2+'_'+(i-1);
                    if (i == 1)
                        data.append("# ").append(callName2).append("\n");
                    data.append(dataName).append(":\n");
                    data.append(indent).append(".string \"").append(values.get(i).value).append("\"\n");
                    if (i+1 == values.size())
                        data.append('\n');

                    result.append(indent).append("movl $").append(dataName).append(", %ecx\n");
                    result.append(indent).append(typeToString(type)).append(' ').append(callName);
                }

                result.append("\n\n");
            }
            //
        }
        return result.toString();
    }
}
