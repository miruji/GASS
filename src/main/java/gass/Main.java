package gass;

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

        StringBuilder outputBuffer = new StringBuilder("Tokenizer output: [\n");
        for (Token token : tokenizer.tokens) {
            if (token.word != null)
                outputBuffer.append("    [").append(token.type).append("]: [").append(token.word).append("]\n");
            else
                outputBuffer.append("    [").append(token.type).append("]\n");
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());

        // parser
        Parser parser = new Parser(tokenizer.tokens);
        tokenizer = null;

        outputBuffer = new StringBuilder("Parser output: [\n");
        for (Token token : parser.tokens) {
            outputBuffer.append(printTokensTree(token, 1));
        }
        new Log(LogType.info, outputBuffer.append("] \n").toString());
    }
    // Рекурсивная функция для вывода токенов и их детей
    public static String printTokensTree(Token token, int depth) {
        StringBuilder output = new StringBuilder();
        output.append("\t".repeat(Math.max(0, depth)));

        if (token.word != null)
            output.append("[").append(token.type).append("]: [").append(token.word).append("]\n");
        else
            output.append("[").append(token.type).append("]\n");

        if (token.childrens != null) {

            for (Token child : token.childrens) {
                output.append(printTokensTree(child, depth+1));
            }
        }
        return output.toString();
    }

}