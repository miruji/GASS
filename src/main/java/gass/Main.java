package gass;

import gass.parser.Block;
import gass.parser.Class;
import gass.parser.Enum;
import gass.parser.Parser;
import gass.tokenizer.Token;
import gass.tokenizer.Tokenizer;
import gass.io.fs.File;
import gass.io.log.Log;
import gass.io.log.LogType;

public class Main {
    public static void main(String[] args) {
        // args / argv check : TO:DO:
        // debug mode
        boolean debugMode = false; // args : TO:DO:
        if (debugMode)
            Log.debugStackTraceFlag = true;

        // start
        new Log(LogType.info, "start\n");

        // open .ll file
        String openFilePath = "release/Main.gs";

        String openFile = File.getFileString(openFilePath);
        new Log(LogType.info, "Open file data: ["+openFile+"]\n");

        // tokenizer
        Tokenizer tokenizer = new Tokenizer(openFile);

        StringBuilder outputBuffer;/* = new StringBuilder("Tokenizer output: [\n");
        for (Token token : tokenizer.tokens) {
            if (token.word != null)
                outputBuffer.append("    [").append(token.type).append("]: [").append(token.word).append("]\n");
            else
                outputBuffer.append("    [").append(token.type).append("]\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());
        */

        // parser
        Parser parser = new Parser(tokenizer.tokens);
        tokenizer = null;

        outputBuffer = new StringBuilder("Parser output: [\n");
        for (Token t : parser.tokens) {
            outputBuffer.append(Token.printChildrens(t, 1));
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());

        // enums
        outputBuffer = new StringBuilder("Enums: [\n");
        for (Enum e : parser.enums) {
            outputBuffer.append('\t').append(e.name).append(": [\n");
            for (Token t : e.tokens) {
                outputBuffer.append(Token.printChildrens(t, 2));
            }
            outputBuffer.append("\t}\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());

        // classes
        outputBuffer = new StringBuilder("Classes: [\n");
        for (Class c : parser.classes) {
            outputBuffer.append('\t').append(c.type.toString()).append(' ').append(c.name).append(": [\n");
            for (Token t : c.tokens) {
                outputBuffer.append(Token.printChildrens(t, 2));
            }
            outputBuffer.append("\t}\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());

        // global functions
        outputBuffer = new StringBuilder("Global blocks: [\n");
        for (Block b : parser.blocks) {
            outputBuffer.append('\t').append(b.type.toString()).append(' ').append(b.name).append(": [\n");
            for (Token t : b.tokens) {
                outputBuffer.append(Token.printChildrens(t, 2));
            }
            outputBuffer.append("\t}\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());
    }
}