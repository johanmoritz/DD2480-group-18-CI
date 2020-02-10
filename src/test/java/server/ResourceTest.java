import org.junit.Test;
import org.junit.Assert;

import buildtools.Build;
import buildtools.Storage;
import server.resources.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class ResourceTest {
    /**
     * Creates the test data object
     * @param status status of build
     * @param sha commit sha
     * @param url url of build
     * @param log build log
     * @param date date and time of build
     * @return JSONObject with given fields
     */
    public JSONObject createTestObject(Build.Result status, String sha, String url, List<ArrayList<String>> log, String date) {
        JSONObject data = new JSONObject();
        data.put("status", status);
        data.put("commitSha", sha);
        data.put("url", url);
        data.put("log", log);
        data.put("date", date);

        return data;
    }
    
    // Test get functionality
    @Test
    public void test0() {
        // Create two fake data objects to see that both are fetched
        Storage s = new Storage();
        try {
            s.clear();
        } catch (IOException e) {
            Assert.fail("Error, couldn't clear database");
        }

        List<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
        ArrayList<String> a1 = new ArrayList<String>();
        ArrayList<String> a2 = new ArrayList<String>();
        a1.add("test0build");
        a2.add("test0test");
        l.add(a1);
        l.add(a2);

        String key = "test0job";
        Build.Result buildResult = Build.Result.success;
        String sha = "test0sha";
        String url = "test0url";
        String date = "2020-02-02 19:19:00";

        Build b1 = new Build(key, buildResult, sha, url, l, date);

        String key2 = "test0job2";
        String sha2 = "test0sha2";

        Build b2 = new Build(key2, buildResult, sha2, url, l, date);
        
        // Store
        try {
            s.post(b1);
            s.post(b2);
        } catch(IOException e) {
            Assert.fail("Error, couldn't build job");
        }

        // Fetch
        Resource resource = new Resource();
        ArrayList<Build> builds = new ArrayList<Build>();
        try {
            builds = resource.getBuilds();
        } catch (IOException e) {
            Assert.fail("Error, couldn't get build");
        }

        // Create the object that it SHOULD be when fetched
        JSONObject data = createTestObject(buildResult, sha, url, l, date);
        JSONObject data2 = createTestObject(buildResult, sha2, url, l, date);

        // Check so everything is fetched properly, therefor also stored properly
        assertEquals(builds.get(0).getStatus(), "success");
        assertEquals(builds.get(0).getCommitSha(), data.get("commitSha"));
        assertEquals(builds.get(0).getUrl(), data.get("url"));
        assertEquals(builds.get(0).getDate(), data.get("date"));

        String testRow = builds.get(0).getLog().get(0).get(0);
        assertEquals(testRow, a1.get(0));
        String buildRow = builds.get(0).getLog().get(1).get(0);
        assertEquals(buildRow, a2.get(0));

        // Check second object as well
        assertEquals(builds.size(), 2);
        assertEquals(builds.get(1).getStatus(), "success");
        assertEquals(builds.get(1).getCommitSha(), data2.get("commitSha"));
        assertEquals(builds.get(1).getUrl(), data2.get("url"));
        assertEquals(builds.get(1).getDate(), data2.get("date"));

        testRow = builds.get(1).getLog().get(0).get(0);
        assertEquals(testRow, a1.get(0));
        buildRow = builds.get(1).getLog().get(1).get(0);
        assertEquals(buildRow, a2.get(0));
    }
}