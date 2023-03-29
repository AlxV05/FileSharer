package main.java;

import java.util.HashMap;
import java.util.Map;

public class ServerDataHandler {
    protected final Map<String, FileDataObject> files;

    public ServerDataHandler() {
        this.files = new HashMap<>();
    }

    public String listFiles() {
//        return String.join("\n", this.files.keySet());
        return "Listed";
    }

    public synchronized String readFile(String fileName) {
        return String.join("\n", this.files.get(fileName).fileData());
    }

    public synchronized void addFile(FileDataObject fileDataObject) {
        this.files.put(fileDataObject.fileName(), fileDataObject);
    }

    public synchronized void removeFile(String fileName) {
        this.files.remove(fileName);
    }
}
