import org.junit.Test;
import org.junit.Assert;

import buildtools.Build;
import buildtools.Storage;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import org.json.JSONObject;
import org.json.JSONArray;

import java.beans.Transient;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class StorageTest {
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

    /**
     * Check that correct data is fetched and stored
     */
  @Test
  public void test0() {
      // Create fake data
    Storage s = new Storage();
    List<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
    ArrayList<String> a1 = new ArrayList<String>();
    ArrayList<String> a2 = new ArrayList<String>();
    a1.add("test0build");
    a2.add("test0test");
    l.add(a1);
    l.add(a2);

    String key = "testjob";
    Build.Result buildResult = Build.Result.success;
    String sha = "test0sha";
    String url = "test0url";
    String date = "2020-02-02 19:19:00";

    Build b = new Build(key, buildResult, sha, url, l, date);
    
    // Store
    try {
        s.post(b);
    } catch(IOException e) {
        Assert.fail("Error, couldn't build job");
    }

    // Fetch
    JSONObject result = new JSONObject();
    try {
        result = s.get("testjob");
    } catch (IOException e) {
        Assert.fail("Error, couldn't get build");
    }

    // Create the object that it SHOULD be when fetched
    JSONObject data = createTestObject(buildResult, sha, url, l, date);

    // Check so everything is fetched properly, therefor also stored properly
    assertEquals(result.get("status"), "success");
    assertEquals(result.get("commitSha"), data.get("commitSha"));
    assertEquals(result.get("url"), data.get("url"));
    assertEquals(result.get("date"), data.get("date"));

    String testRow = result.getJSONArray("log").getJSONArray(0).getString(0);
    assertEquals(testRow, a1.get(0));
    String buildRow = result.getJSONArray("log").getJSONArray(1).getString(0);
    assertEquals(buildRow, a2.get(0));
  }

  /**
   * Check so we can update fields in existing key
   */
  @Test
  public void test1() {
      // Create fake data but for same key in database as in test0 "testjob"
    Storage s = new Storage();
    List<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
    ArrayList<String> a1 = new ArrayList<String>();
    ArrayList<String> a2 = new ArrayList<String>();
    a1.add("test1build");
    a2.add("test1test");
    l.add(a1);
    l.add(a2);

    String key = "testjob";
    Build.Result buildResult = Build.Result.pending;
    String sha = "test1sha";
    String url = "test1url";
    String date = "2020-02-04 23:00:00";

    Build b = new Build(key, buildResult, sha, url, l, date);
    
    // Store
    try {
        s.post(b);
    } catch(IOException e) {
        Assert.fail("Error, couldn't build job");
    }

    // Fetch
    JSONObject result = new JSONObject();
    try {
        result = s.get("testjob");
    } catch (IOException e) {
        Assert.fail("Error, couldn't get build");
    }

    // Create object we expect
    JSONObject data = createTestObject(buildResult, sha, url, l, date);

    // Check that the same key as used in test0 has been updated to new values
    assertEquals(result.get("status"), "pending");
    assertEquals(result.get("commitSha"), data.get("commitSha"));
    assertEquals(result.get("url"), data.get("url"));
    assertEquals(result.get("date"), data.get("date"));

    String testRow = result.getJSONArray("log").getJSONArray(0).getString(0);
    assertEquals(testRow, a1.get(0));
    String buildRow = result.getJSONArray("log").getJSONArray(1).getString(0);
    assertEquals(buildRow, a2.get(0));
  }

  /**
   * Check that we can add more than one key
   */
  @Test
  public void test2() {
      // Create fake data but for same key in database as in test0 "testjob"
    Storage s = new Storage();
    List<ArrayList<String>> l = new ArrayList<ArrayList<String>>();
    ArrayList<String> a1 = new ArrayList<String>();
    ArrayList<String> a2 = new ArrayList<String>();
    a1.add("test2build");
    a2.add("test2test");
    l.add(a1);
    l.add(a2);

    String key = "test2job";
    Build.Result buildResult = Build.Result.failure;
    String sha = "test2sha";
    String url = "test2url";
    String date = "2020-02-03 20:00:00";

    Build b = new Build(key, buildResult, sha, url, l, date);
    
    // Save old object used in test0 and test1 to compare later
    JSONObject oldData = new JSONObject();
    try {
        oldData = s.get("testjob");
    } catch (IOException e) {
        Assert.fail("Error, couldn't get build");
    }

    // Store new
    try {
        s.post(b);
    } catch(IOException e) {
        Assert.fail("Error, couldn't build job");
    }

    // Fetch it
    JSONObject result = new JSONObject();
    try {
        result = s.get("test2job");
    } catch (IOException e) {
        Assert.fail("Error, couldn't get build");
    }

    // Create object we expect
    JSONObject data = createTestObject(buildResult, sha, url, l, date);

    // Check that fetched data is same as stored for this new key
    assertEquals(result.get("status"), "failure");
    assertEquals(result.get("commitSha"), data.get("commitSha"));
    assertEquals(result.get("url"), data.get("url"));
    assertEquals(result.get("date"), data.get("date"));

    String testRow = result.getJSONArray("log").getJSONArray(0).getString(0);
    assertEquals(testRow, data.getJSONArray("log").getJSONArray(0).getString(0));
    String buildRow = result.getJSONArray("log").getJSONArray(1).getString(0);
    assertEquals(buildRow, data.getJSONArray("log").getJSONArray(1).getString(0));

    // Check that the old object has not been changed while we stored the old one
    try {
        result = s.get("testjob");
    } catch (IOException e) {
        Assert.fail("Error, couldn't get build");
    }

    assertEquals(result.get("status"), "pending");
    assertEquals(result.get("commitSha"), oldData.get("commitSha"));
    assertEquals(result.get("url"), oldData.get("url"));
    assertEquals(result.get("date"), oldData.get("date"));

    testRow = result.getJSONArray("log").getJSONArray(0).getString(0);
    assertEquals(testRow, oldData.getJSONArray("log").getJSONArray(0).getString(0));
    buildRow = result.getJSONArray("log").getJSONArray(1).getString(0);
    assertEquals(buildRow, oldData.getJSONArray("log").getJSONArray(1).getString(0));
  }
}