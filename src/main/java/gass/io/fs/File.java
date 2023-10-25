package gass.io.fs;

import gass.io.log.Log;
import gass.io.log.LogFlag;
import gass.io.log.LogType;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class File {
    public static String getFileString(String openFilePath) {
        try {
            new Log(LogType.info, "Open file by path: ["+openFilePath+']');
            BufferedReader reader = new BufferedReader(new FileReader(openFilePath));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line).append('\u001F');
            reader.close();
            return result.toString();
        } catch (IOException e) {
            new Log(LogType.error,"Unable to read file ["+openFilePath+']', LogFlag.stackTraceCallInfo);
            return "";
        }
    }
    public static void stringToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            new Log(LogType.info,"Content has been written to the file", LogFlag.none);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}