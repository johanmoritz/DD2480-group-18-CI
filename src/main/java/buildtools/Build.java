package buildtools;

public class Build {
    private String jobID;
    private String status;
    private String commitSha;
    private String url;
    private String log;

    public Build(String jobID, String status, String commitSha, String url, String log) {
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
        return status;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public String getUrl() {
        return url;
    }

    public String getLog() {
        return log;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLog(String log) {
        this.log = log;
    }

}
