package main.java;

import java.util.HashMap;
import java.util.Map;

import static main.java.Messages.CLIOutput.*;

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
            return String.format(Failures.noFileWithTag, fileTag);
        }
    }

    public synchronized void addFile(String fileTag, String fileBytes) {
        files.put(fileTag, new FileDataObject(fileTag, fileBytes));
    }

    public synchronized void removeFile(String fileTag) {
        files.remove(fileTag);
    }
}
