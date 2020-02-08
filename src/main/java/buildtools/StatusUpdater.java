package buildtools;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class StatusUpdater {

    /**
     * Sets commit status. OAuth authorization using /token file in root folder.
     * @param owner - name of repo owners account
     * @param repo - name of repo
     * @param sha - sha value of commit
     * @param status - pending, success, failure, error
     */
    public static void updateStatus(String owner, String repo, String sha, Build.Result status, String jobID) {
        String token = getToken();
        if (token !=  null) {
            sendHttpPost(createHttpPost(owner, repo, sha, status, token, jobID));
        }
    }

    /**
     * Creates a HTTP POST request to GitHubs commit status API.
     * @param owner - name of repository owner
     * @param repo - name of repository
     * @param sha - commit sha value
     * @param status - commit status to be set
     * @param token - repository owner's OAuth authorization token
     * @return HttpPost object configured for the GitHub commit status API
     */
    public static HttpPost createHttpPost(String owner,
                                          String repo,
                                          String sha,
                                          Build.Result status,
                                          String token,
                                          String jobID) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/statuses/" + sha;
        HttpPost httpPost = new HttpPost(url);

        String description = getStatusDescription(status);

        JSONObject json = new JSONObject();
        json.put("state", status);
        json.put("target_url", "http://localhost:3000/build/" + jobID);
        json.put("description", description);
        json.put("context", "mobergliuslefors");

        try {
            httpPost.setEntity(new StringEntity(json.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpPost.setHeader("Authorization", "token " + token);
        return httpPost;
    }

    /**
     * Sends a HTTP Post request
     * @param httpPost - a http POST request to be sent
     */
    public static void sendHttpPost(HttpPost httpPost) {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            client.execute(httpPost);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads token from token file
     * @return token string
     */
    public static String getToken() {
        String token = null;
        try {
            token = new BufferedReader(new FileReader("token")).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Authorization token not found.");
        }
        return token;
    }

    /**
     * Gets the commit status description depending on build status.
     * @param status - the build status
     * @return commit status description string
     */
    public static String getStatusDescription(Build.Result status) {
        String description;
        switch (status) {
            case pending:
                description = "Pending";
                break;
            case success:
                description = "Success";
                break;
            case failure:
                description = "Failure";
                break;
            default:
                description = "Error";

        }
        return description;
    }

}
