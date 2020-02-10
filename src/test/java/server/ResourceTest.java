package server;

import org.junit.Test;
import org.junit.Assert;

import buildtools.Build;
import buildtools.Storage;
import server.resources.*;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.junit.Assert.*;

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

    /**
     * Tests if the /ci/push handler can handle a web hook creation POST request from Github.
     */
    @Test
    public void test2() {
        String payload = "{\n" +
                "  \"zen\": \"Speak like a human.\",\n" +
                "  \"hook_id\": 182401794,\n" +
                "  \"hook\": {\n" +
                "    \"type\": \"Repository\",\n" +
                "    \"id\": 182401794,\n" +
                "    \"name\": \"web\",\n" +
                "    \"active\": true,\n" +
                "    \"events\": [\n" +
                "      \"push\"\n" +
                "    ],\n" +
                "    \"config\": {\n" +
                "      \"content_type\": \"form\",\n" +
                "      \"insecure_ssl\": \"0\",\n" +
                "      \"url\": \"http://83b9ac97.ngrok.io/ci/push\"\n" +
                "    },\n" +
                "    \"updated_at\": \"2020-02-10T16:58:20Z\",\n" +
                "    \"created_at\": \"2020-02-10T16:58:20Z\",\n" +
                "    \"url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/hooks/182401794\",\n" +
                "    \"test_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/hooks/182401794/test\",\n" +
                "    \"ping_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/hooks/182401794/pings\",\n" +
                "    \"last_response\": {\n" +
                "      \"code\": null,\n" +
                "      \"status\": \"unused\",\n" +
                "      \"message\": null\n" +
                "    }\n" +
                "  },\n" +
                "  \"repository\": {\n" +
                "    \"id\": 237951979,\n" +
                "    \"node_id\": \"MDEwOlJlcG9zaXRvcnkyMzc5NTE5Nzk=\",\n" +
                "    \"name\": \"DD2480-group-18-CI\",\n" +
                "    \"full_name\": \"adbjo/DD2480-group-18-CI\",\n" +
                "    \"private\": false,\n" +
                "    \"owner\": {\n" +
                "      \"login\": \"adbjo\",\n" +
                "      \"id\": 38786385,\n" +
                "      \"node_id\": \"MDQ6VXNlcjM4Nzg2Mzg1\",\n" +
                "      \"avatar_url\": \"https://avatars3.githubusercontent.com/u/38786385?v=4\",\n" +
                "      \"gravatar_id\": \"\",\n" +
                "      \"url\": \"https://api.github.com/users/adbjo\",\n" +
                "      \"html_url\": \"https://github.com/adbjo\",\n" +
                "      \"followers_url\": \"https://api.github.com/users/adbjo/followers\",\n" +
                "      \"following_url\": \"https://api.github.com/users/adbjo/following{/other_user}\",\n" +
                "      \"gists_url\": \"https://api.github.com/users/adbjo/gists{/gist_id}\",\n" +
                "      \"starred_url\": \"https://api.github.com/users/adbjo/starred{/owner}{/repo}\",\n" +
                "      \"subscriptions_url\": \"https://api.github.com/users/adbjo/subscriptions\",\n" +
                "      \"organizations_url\": \"https://api.github.com/users/adbjo/orgs\",\n" +
                "      \"repos_url\": \"https://api.github.com/users/adbjo/repos\",\n" +
                "      \"events_url\": \"https://api.github.com/users/adbjo/events{/privacy}\",\n" +
                "      \"received_events_url\": \"https://api.github.com/users/adbjo/received_events\",\n" +
                "      \"type\": \"User\",\n" +
                "      \"site_admin\": false\n" +
                "    },\n" +
                "    \"html_url\": \"https://github.com/adbjo/DD2480-group-18-CI\",\n" +
                "    \"description\": \"A simple continuos integration solution, created for assignment 2 at KTH course DD2480. Spring of 2020.\",\n" +
                "    \"fork\": false,\n" +
                "    \"url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI\",\n" +
                "    \"forks_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/forks\",\n" +
                "    \"keys_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/keys{/key_id}\",\n" +
                "    \"collaborators_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/collaborators{/collaborator}\",\n" +
                "    \"teams_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/teams\",\n" +
                "    \"hooks_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/hooks\",\n" +
                "    \"issue_events_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/issues/events{/number}\",\n" +
                "    \"events_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/events\",\n" +
                "    \"assignees_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/assignees{/user}\",\n" +
                "    \"branches_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/branches{/branch}\",\n" +
                "    \"tags_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/tags\",\n" +
                "    \"blobs_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/git/blobs{/sha}\",\n" +
                "    \"git_tags_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/git/tags{/sha}\",\n" +
                "    \"git_refs_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/git/refs{/sha}\",\n" +
                "    \"trees_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/git/trees{/sha}\",\n" +
                "    \"statuses_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/statuses/{sha}\",\n" +
                "    \"languages_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/languages\",\n" +
                "    \"stargazers_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/stargazers\",\n" +
                "    \"contributors_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/contributors\",\n" +
                "    \"subscribers_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/subscribers\",\n" +
                "    \"subscription_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/subscription\",\n" +
                "    \"commits_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/commits{/sha}\",\n" +
                "    \"git_commits_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/git/commits{/sha}\",\n" +
                "    \"comments_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/comments{/number}\",\n" +
                "    \"issue_comment_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/issues/comments{/number}\",\n" +
                "    \"contents_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/contents/{+path}\",\n" +
                "    \"compare_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/compare/{base}...{head}\",\n" +
                "    \"merges_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/merges\",\n" +
                "    \"archive_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/{archive_format}{/ref}\",\n" +
                "    \"downloads_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/downloads\",\n" +
                "    \"issues_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/issues{/number}\",\n" +
                "    \"pulls_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/pulls{/number}\",\n" +
                "    \"milestones_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/milestones{/number}\",\n" +
                "    \"notifications_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/notifications{?since,all,participating}\",\n" +
                "    \"labels_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/labels{/name}\",\n" +
                "    \"releases_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/releases{/id}\",\n" +
                "    \"deployments_url\": \"https://api.github.com/repos/adbjo/DD2480-group-18-CI/deployments\",\n" +
                "    \"created_at\": \"2020-02-03T11:42:31Z\",\n" +
                "    \"updated_at\": \"2020-02-10T14:11:19Z\",\n" +
                "    \"pushed_at\": \"2020-02-10T14:11:17Z\",\n" +
                "    \"git_url\": \"git://github.com/adbjo/DD2480-group-18-CI.git\",\n" +
                "    \"ssh_url\": \"git@github.com:adbjo/DD2480-group-18-CI.git\",\n" +
                "    \"clone_url\": \"https://github.com/adbjo/DD2480-group-18-CI.git\",\n" +
                "    \"svn_url\": \"https://github.com/adbjo/DD2480-group-18-CI\",\n" +
                "    \"homepage\": null,\n" +
                "    \"size\": 13344,\n" +
                "    \"stargazers_count\": 0,\n" +
                "    \"watchers_count\": 0,\n" +
                "    \"language\": \"Java\",\n" +
                "    \"has_issues\": false,\n" +
                "    \"has_projects\": true,\n" +
                "    \"has_downloads\": true,\n" +
                "    \"has_wiki\": true,\n" +
                "    \"has_pages\": false,\n" +
                "    \"forks_count\": 0,\n" +
                "    \"mirror_url\": null,\n" +
                "    \"archived\": false,\n" +
                "    \"disabled\": false,\n" +
                "    \"open_issues_count\": 0,\n" +
                "    \"license\": null,\n" +
                "    \"forks\": 0,\n" +
                "    \"open_issues\": 0,\n" +
                "    \"watchers\": 0,\n" +
                "    \"default_branch\": \"master\"\n" +
                "  },\n" +
                "  \"sender\": {\n" +
                "    \"login\": \"adbjo\",\n" +
                "    \"id\": 38786385,\n" +
                "    \"node_id\": \"MDQ6VXNlcjM4Nzg2Mzg1\",\n" +
                "    \"avatar_url\": \"https://avatars3.githubusercontent.com/u/38786385?v=4\",\n" +
                "    \"gravatar_id\": \"\",\n" +
                "    \"url\": \"https://api.github.com/users/adbjo\",\n" +
                "    \"html_url\": \"https://github.com/adbjo\",\n" +
                "    \"followers_url\": \"https://api.github.com/users/adbjo/followers\",\n" +
                "    \"following_url\": \"https://api.github.com/users/adbjo/following{/other_user}\",\n" +
                "    \"gists_url\": \"https://api.github.com/users/adbjo/gists{/gist_id}\",\n" +
                "    \"starred_url\": \"https://api.github.com/users/adbjo/starred{/owner}{/repo}\",\n" +
                "    \"subscriptions_url\": \"https://api.github.com/users/adbjo/subscriptions\",\n" +
                "    \"organizations_url\": \"https://api.github.com/users/adbjo/orgs\",\n" +
                "    \"repos_url\": \"https://api.github.com/users/adbjo/repos\",\n" +
                "    \"events_url\": \"https://api.github.com/users/adbjo/events{/privacy}\",\n" +
                "    \"received_events_url\": \"https://api.github.com/users/adbjo/received_events\",\n" +
                "    \"type\": \"User\",\n" +
                "    \"site_admin\": false\n" +
                "  }\n" +
                "}";

        Resource resource = new Resource();
        Response response = resource.push(payload);
        assertEquals(200, response.getStatus());
    }

    /**
     * Tests if the /ci/push handler can handle a push POST request from Github.
     */
    @Test
    public void test1() {
        String payload = "{\n" +
                "  \"after\": \"00327ebb5ef2d37ca1dd72070af5b20feff8e676\",\n" +
                "  \"base_ref\": null,\n" +
                "  \"before\": \"672feb5ba785d1a6af223f0821252f53c25cfbd0\",\n" +
                "  \"commits\": [\n" +
                "    {\n" +
                "      \"added\": [],\n" +
                "      \"author\": {\n" +
                "        \"email\": \"38786385+adbjo@users.noreply.github.com\",\n" +
                "        \"name\": \"Adam Bj÷rnberg\",\n" +
                "        \"username\": \"adbjo\"\n" +
                "      },\n" +
                "      \"committer\": {\n" +
                "        \"email\": \"noreply@github.com\",\n" +
                "        \"name\": \"GitHub\",\n" +
                "        \"username\": \"web-flow\"\n" +
                "      },\n" +
                "      \"distinct\": true,\n" +
                "      \"id\": \"00327ebb5ef2d37ca1dd72070af5b20feff8e676\",\n" +
                "      \"message\": \"Update README.md\",\n" +
                "      \"modified\": [\n" +
                "        \"README.md\"\n" +
                "      ],\n" +
                "      \"removed\": [],\n" +
                "      \"timestamp\": \"2020-02-10T18:31:20+01:00\",\n" +
                "      \"tree_id\": \"636adf4b41c39f7fa5a44ae6286304e5e56a8ff3\",\n" +
                "      \"url\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build/commit/00327ebb5ef2d37ca1dd72070af5b20feff8e676\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"compare\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build/compare/672feb5ba785...00327ebb5ef2\",\n" +
                "  \"created\": false,\n" +
                "  \"deleted\": false,\n" +
                "  \"forced\": false,\n" +
                "  \"head_commit\": {\n" +
                "    \"added\": [],\n" +
                "    \"author\": {\n" +
                "      \"email\": \"38786385+adbjo@users.noreply.github.com\",\n" +
                "      \"name\": \"Adam Bj÷rnberg\",\n" +
                "      \"username\": \"adbjo\"\n" +
                "    },\n" +
                "    \"committer\": {\n" +
                "      \"email\": \"noreply@github.com\",\n" +
                "      \"name\": \"GitHub\",\n" +
                "      \"username\": \"web-flow\"\n" +
                "    },\n" +
                "    \"distinct\": true,\n" +
                "    \"id\": \"00327ebb5ef2d37ca1dd72070af5b20feff8e676\",\n" +
                "    \"message\": \"Update README.md\",\n" +
                "    \"modified\": [\n" +
                "      \"README.md\"\n" +
                "    ],\n" +
                "    \"removed\": [],\n" +
                "    \"timestamp\": \"2020-02-10T18:31:20+01:00\",\n" +
                "    \"tree_id\": \"636adf4b41c39f7fa5a44ae6286304e5e56a8ff3\",\n" +
                "    \"url\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build/commit/00327ebb5ef2d37ca1dd72070af5b20feff8e676\"\n" +
                "  },\n" +
                "  \"pusher\": {\n" +
                "    \"email\": \"38786385+adbjo@users.noreply.github.com\",\n" +
                "    \"name\": \"adbjo\"\n" +
                "  },\n" +
                "  \"ref\": \"refs/heads/master\",\n" +
                "  \"repository\": {\n" +
                "    \"archive_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/{archive_format}{/ref}\",\n" +
                "    \"archived\": false,\n" +
                "    \"assignees_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/assignees{/user}\",\n" +
                "    \"blobs_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/git/blobs{/sha}\",\n" +
                "    \"branches_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/branches{/branch}\",\n" +
                "    \"clone_url\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build.git\",\n" +
                "    \"collaborators_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/collaborators{/collaborator}\",\n" +
                "    \"comments_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/comments{/number}\",\n" +
                "    \"commits_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/commits{/sha}\",\n" +
                "    \"compare_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/compare/{base}...{head}\",\n" +
                "    \"contents_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/contents/{+path}\",\n" +
                "    \"contributors_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/contributors\",\n" +
                "    \"created_at\": 1580730151,\n" +
                "    \"default_branch\": \"master\",\n" +
                "    \"deployments_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/deployments\",\n" +
                "    \"description\": \"A simple continuos integration solution, created for assignment 2 at KTH course DD2480. Spring of 2020.\",\n" +
                "    \"disabled\": false,\n" +
                "    \"downloads_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/downloads\",\n" +
                "    \"events_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/events\",\n" +
                "    \"fork\": false,\n" +
                "    \"forks\": 0,\n" +
                "    \"forks_count\": 0,\n" +
                "    \"forks_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/forks\",\n" +
                "    \"full_name\": \"adbjo/testing_mobergliuslefors_do_not_build\",\n" +
                "    \"git_commits_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/git/commits{/sha}\",\n" +
                "    \"git_refs_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/git/refs{/sha}\",\n" +
                "    \"git_tags_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/git/tags{/sha}\",\n" +
                "    \"git_url\": \"git://github.com/adbjo/testing_mobergliuslefors_do_not_build.git\",\n" +
                "    \"has_downloads\": true,\n" +
                "    \"has_issues\": false,\n" +
                "    \"has_pages\": false,\n" +
                "    \"has_projects\": true,\n" +
                "    \"has_wiki\": true,\n" +
                "    \"homepage\": null,\n" +
                "    \"hooks_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/hooks\",\n" +
                "    \"html_url\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build\",\n" +
                "    \"id\": 237951979,\n" +
                "    \"issue_comment_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/issues/comments{/number}\",\n" +
                "    \"issue_events_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/issues/events{/number}\",\n" +
                "    \"issues_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/issues{/number}\",\n" +
                "    \"keys_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/keys{/key_id}\",\n" +
                "    \"labels_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/labels{/name}\",\n" +
                "    \"language\": \"Java\",\n" +
                "    \"languages_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/languages\",\n" +
                "    \"license\": null,\n" +
                "    \"master_branch\": \"master\",\n" +
                "    \"merges_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/merges\",\n" +
                "    \"milestones_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/milestones{/number}\",\n" +
                "    \"mirror_url\": null,\n" +
                "    \"name\": \"testing_mobergliuslefors_do_not_build\",\n" +
                "    \"node_id\": \"MDEwOlJlcG9zaXRvcnkyMzc5NTE5Nzk=\",\n" +
                "    \"notifications_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/notifications{?since,all,participating}\",\n" +
                "    \"open_issues\": 0,\n" +
                "    \"open_issues_count\": 0,\n" +
                "    \"owner\": {\n" +
                "      \"avatar_url\": \"https://avatars3.githubusercontent.com/u/38786385?v=4\",\n" +
                "      \"email\": \"38786385+adbjo@users.noreply.github.com\",\n" +
                "      \"events_url\": \"https://api.github.com/users/adbjo/events{/privacy}\",\n" +
                "      \"followers_url\": \"https://api.github.com/users/adbjo/followers\",\n" +
                "      \"following_url\": \"https://api.github.com/users/adbjo/following{/other_user}\",\n" +
                "      \"gists_url\": \"https://api.github.com/users/adbjo/gists{/gist_id}\",\n" +
                "      \"gravatar_id\": \"\",\n" +
                "      \"html_url\": \"https://github.com/adbjo\",\n" +
                "      \"id\": 38786385,\n" +
                "      \"login\": \"adbjo\",\n" +
                "      \"name\": \"adbjo\",\n" +
                "      \"node_id\": \"MDQ6VXNlcjM4Nzg2Mzg1\",\n" +
                "      \"organizations_url\": \"https://api.github.com/users/adbjo/orgs\",\n" +
                "      \"received_events_url\": \"https://api.github.com/users/adbjo/received_events\",\n" +
                "      \"repos_url\": \"https://api.github.com/users/adbjo/repos\",\n" +
                "      \"site_admin\": false,\n" +
                "      \"starred_url\": \"https://api.github.com/users/adbjo/starred{/owner}{/repo}\",\n" +
                "      \"subscriptions_url\": \"https://api.github.com/users/adbjo/subscriptions\",\n" +
                "      \"type\": \"User\",\n" +
                "      \"url\": \"https://api.github.com/users/adbjo\"\n" +
                "    },\n" +
                "    \"private\": false,\n" +
                "    \"pulls_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/pulls{/number}\",\n" +
                "    \"pushed_at\": 1581355880,\n" +
                "    \"releases_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/releases{/id}\",\n" +
                "    \"size\": 13344,\n" +
                "    \"ssh_url\": \"git@github.com:adbjo/testing_mobergliuslefors_do_not_build.git\",\n" +
                "    \"stargazers\": 0,\n" +
                "    \"stargazers_count\": 0,\n" +
                "    \"stargazers_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/stargazers\",\n" +
                "    \"statuses_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/statuses/{sha}\",\n" +
                "    \"subscribers_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/subscribers\",\n" +
                "    \"subscription_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/subscription\",\n" +
                "    \"svn_url\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build\",\n" +
                "    \"tags_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/tags\",\n" +
                "    \"teams_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/teams\",\n" +
                "    \"trees_url\": \"https://api.github.com/repos/adbjo/testing_mobergliuslefors_do_not_build/git/trees{/sha}\",\n" +
                "    \"updated_at\": \"2020-02-10T17:29:43Z\",\n" +
                "    \"url\": \"https://github.com/adbjo/testing_mobergliuslefors_do_not_build\",\n" +
                "    \"watchers\": 0,\n" +
                "    \"watchers_count\": 0\n" +
                "  },\n" +
                "  \"sender\": {\n" +
                "    \"avatar_url\": \"https://avatars3.githubusercontent.com/u/38786385?v=4\",\n" +
                "    \"events_url\": \"https://api.github.com/users/adbjo/events{/privacy}\",\n" +
                "    \"followers_url\": \"https://api.github.com/users/adbjo/followers\",\n" +
                "    \"following_url\": \"https://api.github.com/users/adbjo/following{/other_user}\",\n" +
                "    \"gists_url\": \"https://api.github.com/users/adbjo/gists{/gist_id}\",\n" +
                "    \"gravatar_id\": \"\",\n" +
                "    \"html_url\": \"https://github.com/adbjo\",\n" +
                "    \"id\": 38786385,\n" +
                "    \"login\": \"adbjo\",\n" +
                "    \"node_id\": \"MDQ6VXNlcjM4Nzg2Mzg1\",\n" +
                "    \"organizations_url\": \"https://api.github.com/users/adbjo/orgs\",\n" +
                "    \"received_events_url\": \"https://api.github.com/users/adbjo/received_events\",\n" +
                "    \"repos_url\": \"https://api.github.com/users/adbjo/repos\",\n" +
                "    \"site_admin\": false,\n" +
                "    \"starred_url\": \"https://api.github.com/users/adbjo/starred{/owner}{/repo}\",\n" +
                "    \"subscriptions_url\": \"https://api.github.com/users/adbjo/subscriptions\",\n" +
                "    \"type\": \"User\",\n" +
                "    \"url\": \"https://api.github.com/users/adbjo\"\n" +
                "  }\n" +
                "}";
        Resource resource = new Resource();
        Response response = resource.push(payload);
        assertEquals(200, response.getStatus());
    }

}