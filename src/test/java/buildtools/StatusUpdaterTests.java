package buildtools;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class StatusUpdaterTests {
    /**
     * Tests if the GitHub API is reachable.
     */
    @Test
    public void test0() {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.github.com/orgs/octokit/repos");
        try {
            CloseableHttpResponse response = client.execute(request);
            assertEquals(200, response.getStatusLine().getStatusCode());
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if the createHttpPost function creates the HTTP POST request correctly.
     */
    @Test
    public void test1() {
        String owner = "adbjo";
        String repo = "DD2480-group-18-CI";
        String commitSha = "afda99d6c1462f549e27dd86c0a6918194c4a7f6";
        Build.Result status = Build.Result.success;
        String token = "9d876fs987f9s87fd6s987fsd98f7s698f7ds69f";

        HttpPost httpPost = StatusUpdater.createHttpPost(owner, repo, commitSha, status, token);

        assertEquals("POST", httpPost.getMethod());
        assertEquals("api.github.com", httpPost.getURI().getAuthority());
        assertEquals(
                "/repos/adbjo/DD2480-group-18-CI/statuses/afda99d6c1462f549e27dd86c0a6918194c4a7f6",
                httpPost.getURI().getPath()
        );
        boolean hasAuthHeader = false;
        for (Header h : httpPost.getAllHeaders()) {
            if (h.getName().equals("Authorization")) {
                hasAuthHeader = true;
                assertEquals("token 9d876fs987f9s87fd6s987fsd98f7s698f7ds69f", h.getValue());
            }
        }
        assertTrue(hasAuthHeader);
        try {
            assertEquals(
                    new JSONObject(EntityUtils.toString(httpPost.getEntity(), "UTF-8")).toString(),
                    new JSONObject("{" +
                            "state:'success'," +
                            "target_url:'https://www.google.se'," +
                            "description:'Success'," +
                            "context:'mobergliuslefors'}").toString()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
