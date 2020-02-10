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
        String owner = "Kappenn";
        String repo = "HelloWorld";
        String commitSha = "40bbbdf251c8a1003e78959cc95a6f8d72795a8c";
        Build.Result status = Build.Result.success;
        String token = "c88cc863ad888b0d91ea5ba749f1a234d9054e6b";
        String jobID = "testjobID12312313";
        HttpPost request = StatusUpdater.createHttpPost(owner, repo, commitSha, status, token, jobID);
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = client.execute(request);
            assertEquals(201, response.getStatusLine().getStatusCode());
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
        String jobID = "testjobID12312313";

        HttpPost httpPost = StatusUpdater.createHttpPost(owner, repo, commitSha, status, token, jobID);

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
            assertTrue(
                    new JSONObject(EntityUtils.toString(httpPost.getEntity(), "UTF-8")).similar(
                            new JSONObject("{" +
                                    "state:'success'," +
                                    "target_url:'http://localhost:3000/build/testjobID12312313'," +
                                    "description:'Success'," +
                                    "context:'mobergliuslefors'}")
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
