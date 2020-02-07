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
    public static void updateStatus(String owner, String repo, String sha, Build.Result status) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/statuses/" + sha;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        String token;
        try {
            token = new BufferedReader(new FileReader("token")).readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Authorization token not found.");
            return;
        }

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

        JSONObject json = new JSONObject();
        json.put("state", status);
        json.put("target_url", "https://www.google.se");
        json.put("description", description);
        json.put("context", "mobergliuslefors");

        try {
            httpPost.setEntity(new StringEntity(json.toString()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpPost.setHeader("Authorization", "token " + token);

        try {
            client.execute(httpPost);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
