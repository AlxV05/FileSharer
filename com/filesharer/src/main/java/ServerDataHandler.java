package main.java;

import java.util.HashMap;

import static main.java.Messages.CLIOutput.*;

public class ServerDataHandler {
    protected final HashMap<String, String> files;

    public ServerDataHandler() {
        this.files = new HashMap<>();
    }

    public String listFiles() {
        if (files.isEmpty()) {
            return Statuses.noFilesInDatabase;
        } else {
            return String.join("%n", files.keySet());
        }
    }

    public synchronized String readFile(String fileTag) {
        if (files.containsKey(fileTag)) {
            return files.get(fileTag);
        } else {
            return String.format(Failures.noFileWithTag, fileTag);
        }
    }

    public synchronized void addFile(String fileTag, String fileBytes) {
        files.put(fileTag, fileBytes);
    }

    public synchronized void removeFile(String fileTag) {
        files.remove(fileTag);
    }
}
