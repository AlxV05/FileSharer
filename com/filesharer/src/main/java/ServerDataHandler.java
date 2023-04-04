package main.java;

import java.util.HashMap;
import java.util.Map;

public class ServerDataHandler {
    protected final Map<String, FileDataObject> files;

    public ServerDataHandler() {
        this.files = new HashMap<>();
    }

    public String listFiles() {
        return String.join("%n", files.keySet());
    }

    public synchronized String readFile(String fileTag) {
        if (files.containsKey(fileTag)) {
            return files.get(fileTag).fileData();
        } else {
            return String.format("File with tag \"%s\" not found", fileTag);
        }
    }

    public synchronized void addFile(FileDataObject fileDataObject) {
        files.put(fileDataObject.fileTag(), fileDataObject);
    }

    public synchronized void removeFile(String fileName) {
        files.remove(fileName);
    }
}
