package readfilejenkins.files;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

public class Files {

    public static void main(String[] args) throws Exception {
        if (args.length != 6) {
            System.err.println("Usage: java JenkinsFileReader <jenkins-url> <job-name> <build-number> <file-parameter-name> <username> <api-token>");
            System.exit(1);
        }

        // Read parameters from command line arguments
        String jenkinsUrl = args[0];
        String jobName = args[1];
        String buildNumber = args[2];
        String fileParameterName = args[3];
        String username = args[4];
        String apiToken = args[5];

        // Create the HTTP client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Construct the URL to get the build information
            String buildUrl = String.format("%s/job/%s/%s/api/json", jenkinsUrl, jobName, buildNumber);

            // Create the HTTP GET request
            HttpGet request = new HttpGet(buildUrl);
            request.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));

            // Execute the request
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            // Parse the response JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = EntityUtils.toString(entity);
            JsonNode jsonNode = mapper.readTree(jsonResponse);

            // Extract the file parameter URL
            String fileParameterUrl = jsonNode.path("actions").path(0).path("parameters").findValue(fileParameterName).asText();

            if (fileParameterUrl != null && !fileParameterUrl.isEmpty()) {
                // Construct the URL to get the file content
                String fileUrl = jenkinsUrl + fileParameterUrl;

                // Create the HTTP GET request for the file
                HttpGet fileRequest = new HttpGet(fileUrl);
                fileRequest.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes()));

                // Execute the request
                HttpResponse fileResponse = httpClient.execute(fileRequest);
                HttpEntity fileEntity = fileResponse.getEntity();
 
                // Read and print the file content
                try (InputStream inputStream = fileEntity.getContent();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            } else {
                System.out.println("File parameter not found.");
            }
        }
    }
}

