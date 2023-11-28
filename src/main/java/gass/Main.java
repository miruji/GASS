package gass;

import gass.parser.*;
import gass.parser.Class;
import gass.parser.Enum;
import gass.tokenizer.Token;
import gass.tokenizer.Tokenizer;
import gass.io.fs.File;
import gass.io.log.Log;
import gass.io.log.LogType;

public class Main {
    public static void main(final String[] args) {
        // args / argv check : TO:DO:
        // debug mode
        final boolean debugMode = false; // args : TO:DO:
        if (debugMode)
            Log.debugStackTraceFlag = true;

        // start
        new Log(LogType.info, "start\n");

        // open .gs file
        final String openFilePath = "release/Main.gs";

        final String openFile = File.getFileString(openFilePath);
        new Log(LogType.info, "Open file data: ["+openFile+"]\n");

        /* next tokenizer */
        final Tokenizer tokenizer = new Tokenizer(openFile);

        StringBuilder outputBuffer; /* = new StringBuilder("Tokenizer output: [\n");
        for (Token token : tokenizer.tokens) {
            if (token.data != null)
                outputBuffer.append("    [").append(token.type).append("]: [").append(token.data).append("]\n");
            else
                outputBuffer.append("    [").append(token.type).append("]\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());
        */

        /* next parser */
        final Parser parser = new Parser(tokenizer.tokens);

        /*
        // enums
        outputBuffer = new StringBuilder("Enums: [\n");
        for (final Enum e : parser.enums) {
            outputBuffer.append("\t[").append(e.name).append("]: [\n");
            for (final Token t : e.tokens)
                outputBuffer.append(Token.outputChildrens(t, 2));
            outputBuffer.append("\t}\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());

        // classes
        outputBuffer = new StringBuilder("Classes: [\n");
        for (final Class c : parser.classes) {
            outputBuffer.append('\t').append(c.type.toString()).append(" [").append(c.name).append("]: [\n");
            for (final Token t : c.tokens) {
                outputBuffer.append(Token.outputChildrens(t, 2));
            }
            outputBuffer.append("\t}\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());
         */

        // global functions
        outputBuffer = new StringBuilder("Global blocks:\n");
        for (final Block b : parser.blocks) {
            outputBuffer.append(Block.outputLocalBlocks(b, 1));
        }
        new Log(LogType.info, outputBuffer.toString());

        /* next output stack */
        final StringBuilder outputData = new StringBuilder(
            ".section .data\n"
        );
        outputBuffer = new StringBuilder(
            ".section .text\n"+
            ".globl _start\n"+
            "_start:\n"
        );
        for (int i = 0; i < parser.mainBlock.stack.size(); i++) {
            final Stack stack = parser.mainBlock.stack.get(i);
            outputBuffer.append(stack.toString(String.valueOf(i), "  "));
            outputData.append(stack.data);
        }
        outputBuffer.append(
            "  #\n"+
            "  movl $1, %eax\n"+
            "  xorl %ebx, %ebx\n"+
            "  int $0x80\n");
        File.stringToFile("release/Main.s", outputData.append(outputBuffer).toString());
    }
}