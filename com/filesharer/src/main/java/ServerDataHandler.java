package main.java;

import java.util.HashMap;
import java.util.Map;

public class ServerDataHandler {
    protected final Map<String, FileDataObject> files;

    public ServerDataHandler() {
        this.files = new HashMap<>();
    }

    public String listFiles() {
        return String.join("%n", this.files.keySet());
    }

    public synchronized String readFile(String fileName) {
        if (this.files.containsKey(fileName)) {
            return this.files.get(fileName).fileData();
        } else {
            return String.format("File \"%s\" not found", fileName);
        }
    }

    public synchronized void addFile(FileDataObject fileDataObject) {
        this.files.put(fileDataObject.fileName(), fileDataObject);
    }

    public synchronized void removeFile(String fileName) {
        this.files.remove(fileName);
    }
}
