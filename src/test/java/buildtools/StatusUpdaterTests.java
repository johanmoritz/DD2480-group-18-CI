package buildtools;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.junit.Test;
import static org.junit.Assert.*;

public class StatusUpdaterTests {

    // TODO
    // Test if server can reach the GitHub api.

    // TODO
    // Tests if http POST is properly constructed. WIP.
    @Test
    public void test1() {
        String owner = "adbjo";
        String repo = "DD2480-group-18-CI";
        String commitSha = "afda99d6c1462f549e27dd86c0a6918194c4a7f6";
        String token = "9d876fs987f9s87fd6s987fsd98f7s698f7ds69f";

        HttpPost httpPost = StatusUpdater.createHttpPost(owner, repo, commitSha, Build.Result.success, token);

        Header[] headers = httpPost.getAllHeaders();
        for (Header h: headers) {
            if (h.getName().equals("Authorization")) {

            }
        }

        assertTrue(false);
    }
}
