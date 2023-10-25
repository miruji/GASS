package gass;

import gass.Tokenizer.Token;
import gass.Tokenizer.Tokenizer;
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

        StringBuilder tokensStr = new StringBuilder("Tokenizer output: [\n");
        for (Token token : tokenizer.tokens) {
            if (token.word != null)
                tokensStr.append("    [").append(token.type).append("]: [").append(token.word).append("]\n");
            else
                tokensStr.append("    [").append(token.type).append("]\n");
        }
        new Log(LogType.info, tokensStr.append("] \n").toString());
    }
}