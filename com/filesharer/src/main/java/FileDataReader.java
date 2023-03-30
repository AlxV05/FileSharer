package main.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileDataReader {

    public String getFileData(File file) {
        try {
            return String.join("%n", Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to read file";
        }
    }

}
