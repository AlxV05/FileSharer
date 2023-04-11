package main.java;

import java.util.concurrent.ConcurrentHashMap;

import static main.java.Messages.CLIOutput.Failures;
import static main.java.Messages.CLIOutput.Statuses;

public class ServerDataHandler {
    protected final ConcurrentHashMap<String, String> files;

    public ServerDataHandler() {
        this.files = new ConcurrentHashMap<>();
    }

    public String listFiles() {
        if (files.isEmpty()) {
            return Statuses.noFilesInDatabase;
        } else {
            return String.join("%n", files.keySet());
        }
    }

    public String readFile(String fileTag) {
        if (files.containsKey(fileTag)) {
            return files.get(fileTag);
        } else {
            return String.format(Failures.noFileWithTag, fileTag);
        }
    }

    public void addFile(String fileTag, String fileBytes) {
        files.put(fileTag, fileBytes);
    }

    public void removeFile(String fileTag) {
        files.remove(fileTag);
    }
}
