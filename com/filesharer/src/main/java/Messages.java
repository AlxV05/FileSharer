package main.java;

public final class Messages {
    public static final class CLIOutput {
        public static final String prompt = "> ";
        public static final class Successes {
            public static final String connectionSuccess = "Connected";
            public static final String clientStartSuccess = "Started Client";
            public static final String sudoClientStartSuccess = "Started SudoClient";
            public static final String connectionKillSuccess = "Connection killed";
            public static final String pushedFileSuccessfully = "Added file with tag \"%s\" to Server database";
            public static final String removedFileSuccessfully = "Removed file with tag \"%s\" from Server database";
            public static final String pulledFileSuccessfully = "Successfully pulled file \"%s\" from server to path \"%s\"";
        }

        public static final class Failures {
            public static final String serverSpecifiedPortError = "Error parsing User-supplied port number: Defaulting to port \"1234\"";
            public static final String connectionFailure = "Failed to connect";
            public static final String noConnection = "No connection";
            public static final String lineReadFailure = "Failed to read input line";
            public static final String unknownCommand = "Unknown command: \"%s\"";
            public static final String argumentRequirementFailure = "Argument requirements not met";

            private static final String unspecifiedFile = "No file specified ";
            public static final String unspecifiedFileToRead = unspecifiedFile + "to read";
            public static final String unspecifiedFileToRemove = unspecifiedFile + "to remove";

            public static final String writeToFileFailed = "Failed to write file";
            public static final String noFileWithTag = "File with tag \"%s\" not found";
            public static final String noFileAtPath = "No file with path \"%s\" found";
            public static final String fileAlreadyExistsAtPath = "File already exists at path \"%s\"";
            public static final String failedToCreateNewFileAtPath = "Failed to create new file at path \"%s\"";
            public static final String byteReadLengthFailure = "The number of bytes read differed from the length of the file";
        }

        public static final class Statuses {
            public static final String startingServer = "Starting Server";
            public static final String closingServer = "Closing Server";
            public static final String closingSudoClient = "Closing SudoClient";
            public static final String closingClient = "Closing Client";
            public static final String alreadyConnected = "Already connected";
        }

        public static final class Helps {
            public static final String fullHelp = """
                    ===Help Page===
                    Client Commands:
                        help: - Displays this page.
                        connect: - Connects to the server (TBD add specified ips & ports).
                        kill: - Kills the connection with the Server.
                        exit: - Exits the client.
                    Server Commands:
                        list: - Prints the tags of the files stored in the Server Database.
                        read {File Tag}:
                            - Prints the contents of the specified file from the Server Database.
                        push {File Tag} {Source Path}:
                            - Uploads the contents of file located at the specified path to the Server Database.
                        pull {File Tag} {Destination Path}:
                            - Generates the specified file from the Server Database at the specified path.
                        remove {File Tag}:
                            - Removes the specified file from the Server Database.""";
        }
    }

    public static final class CLIInput {
        public static final class Commands {
            public static final String startConnection = "connect";
            public static final String killConnection = "kill";
            public static final String listFiles = "list";
            public static final String readFile = "read";
            public static final String pushFile = "push";
            public static final String pullFile = "pull";
            public static final String removeFile = "remove";
            public static final String exitClient = "exit";
            public static final String help = "help";
        }

        public static final class ArgumentPlaceholders {
            public static final String doubleArgs = "%s %s";
            public static final String tripleArgs = "%s %s %s";
        }
    }


}
