package readfilejenkins.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Files {

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.err.println("Usage: java Files <jenkins-url> <job-name> <build-number> <file-parameter-name> <username> <api-token>");
            System.exit(1);
        }

        // Read parameters from command line arguments
        String jenkinsUrl = args[0];
        String jobName = args[1];
        String buildNumber = args[2];
        String fileParameterName = args[3]; // This should be the name of the file parameter
        String username = args[4];
        String apiToken = args[5];

        // Get the workspace directory from the environment variable
        String workspaceDirectory = System.getenv("WORKSPACE");
        if (workspaceDirectory == null || workspaceDirectory.isEmpty()) {
            System.err.println("The WORKSPACE environment variable is not set.");
            System.exit(1);
        }

        // Construct the file path
        String filePath = new File(workspaceDirectory, fileParameterName).getAbsolutePath();
        File file = new File(filePath);

        // Check if the file exists and read it
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("File does not exist at the specified path: " + filePath);
        }
    }
}
