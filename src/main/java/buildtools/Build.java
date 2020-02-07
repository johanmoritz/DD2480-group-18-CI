package buildtools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Build {
    public enum Result {
        pending,
        success,
        failure,
        error
    }

    private String jobID;
    private Result status;
    private String commitSha;
    private String url;
    private List<ArrayList<String>> log;

    public Build(String jobID, Result status, String commitSha, String url, List<ArrayList<String>> log) {
        this.jobID = jobID;
        this.status = status;
        this.commitSha = commitSha;
        this.url = url;
        this.log = log;
    }

    public Build(String jobID, JSONObject json) throws IOException {
        this.jobID = jobID;
        this.commitSha = json.getString("commitSha");
        this.url = json.getString("url");
        this.log = new ArrayList<>();

        JSONArray allLogsJson = json.getJSONArray("log");
        for (int i = 0; i < allLogsJson.length(); i++) {
            JSONArray logCommandJSON = allLogsJson.getJSONArray(i);
            ArrayList<String> logCommand = new ArrayList<>();

            for (int j = 0; j < logCommandJSON.length(); j++) {
                logCommand.add(logCommandJSON.getString(j));
            }
            log.add(logCommand);
        }

        // fix status
        String tempStatus = json.getString("status");
        switch (tempStatus) {
            case "success":
                status = Build.Result.success;
                break;
            case "pending":
                status = Build.Result.pending;
                break;
            case "failure":
                status = Build.Result.failure;
                break;
            case "error":
                status = Build.Result.error;
                break;
            default:
                throw new IOException("Invalid 'status' in database");
        }
    }

    public String getJobID() {
        return jobID;
    }

    public String getStatus() {
        return status.toString();
    }

    public String getCommitSha() {
        return commitSha;
    }

    public String getUrl() {
        return url;
    }

    public List<ArrayList<String>> getLog() {
        return log;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public void setStatus(Result status) {
        this.status = status;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLog(List<ArrayList<String>> log) {
        this.log = log;
    }

}
