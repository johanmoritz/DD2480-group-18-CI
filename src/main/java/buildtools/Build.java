package buildtools;

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
