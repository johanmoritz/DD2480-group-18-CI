package buildtools;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import server.ContinuousIntegrationServer;

public class BuildJob {
    public static String BUILD_CONFIG_FILE_NAME = ".dd.yml";
    private static Storage storage = ContinuousIntegrationServer.storage;

    public static void run(String jobID, String cloneURL, String branchRef, String owner, String repo, String commitSha) {
        List<ArrayList<String>> log = new ArrayList<>();
        ArrayList<String> logEntry = new ArrayList<>();
        logEntry.add("Running build job with id " + jobID);
        log.add(new ArrayList<>(logEntry));


        System.out.println("Running build job with id " + jobID);
        Build pendingBuild = new Build(jobID, Build.Result.pending, commitSha, "", log, "");
        StatusUpdater.updateStatus(owner, repo, commitSha, Build.Result.pending, jobID);

        try {
            BuildJob.storage.post(pendingBuild);

        } catch (IOException e) {
            e.printStackTrace();
            logEntry.clear();
            logEntry.add("Internal issue. Contact support.");
            log.add(logEntry);
            BuildJob.error(jobID, log, owner, repo, commitSha);
            return;
        }

        logEntry.clear();
        logEntry.add("Cloning repository.");
        log.add(new ArrayList<>(logEntry));


        Git git;
        try {
            git = Git.cloneRepository()
                    .setURI(cloneURL)
                    .setDirectory(new File("./" + jobID))
                    .setBranch(branchRef)
                    .call();

        } catch (GitAPIException e) {
            e.printStackTrace();
            logEntry.clear();
            logEntry.add("Failed to clone repository " + cloneURL);
            log.add(new ArrayList<>(logEntry));
            BuildJob.error(jobID, log, owner, repo, commitSha);
            return;
        }

        Repository repository = git.getRepository();
        File root = repository.getWorkTree();
        File[] rootFiles = root.listFiles();

        String buildDirectory = "./" + jobID;
        String buildConfig = buildDirectory + "/" + BUILD_CONFIG_FILE_NAME;

        boolean hasBuildConfig = false;
        assert rootFiles != null;
        for (File f : rootFiles) {
            hasBuildConfig |= f.getPath().equals(buildConfig);
        }

        if (hasBuildConfig) {
            ArrayList<ArrayList<String>> commands = RunBash.run(buildDirectory, buildConfig);
            ArrayList<Integer> exitValues = new ArrayList<>();

            boolean failedBuild = false;

            for (ArrayList<String> command : commands) {
                int ev = Integer.parseInt(command.get(command.size() - 1));
                exitValues.add(ev);
                command.remove(command.size() - 1);
                for (String ln : command) {
                    System.out.println(ln);
                }
            }
            for (int ev : exitValues) {
                if (ev != 0) {
                    failedBuild = true;
                    break;
                }
            }

            logEntry.clear();
            if (failedBuild) {
                //TODO: differentiate between test and build, currently we only have build implemented
                logEntry.add("Build or test failed, an exit-value was non-zero");
                log.add(new ArrayList<>(logEntry));
                log.addAll(commands);

                BuildJob.fail(jobID, log, owner, repo, commitSha);
            } else {
                logEntry.add("Found build file.");
                log.add(new ArrayList<>(logEntry));
                log.addAll(commands);

                BuildJob.success(jobID, log, owner, repo, commitSha);
            }
        } else {

            logEntry.clear();
            logEntry.add("Failed to find a build file.");
            log.add(new ArrayList<>(logEntry));

            BuildJob.error(jobID, log, owner, repo, commitSha);
        }

        System.out.println("Finished build job with id " + jobID);
    }

    /**
     * Gets a formatted string output of the current date and time
     * @return YYYY-MM-DD H:M:S
     */
    private static String getTimeString() {
        LocalDateTime time = LocalDateTime.now();
        String year = "" + time.getYear();
        String month = String.format("%02d", time.getMonthValue());
        String day = String.format("%02d", time.getDayOfMonth());
        String hour = String.format("%02d", time.getHour());
        String minutes = String.format("%02d", time.getMinute());
        String seconds = String.format("%02d", time.getSecond());
        return year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + seconds;
    }

    /**
     * This function is called if the build cannot be compiled (or error while compiling?).
     * Updates commit status to "error" and stores the build in the database with its log.
     *
     * @param jobID
     * @param log
     * @param owner
     * @param repo
     * @param commitSha
     */
    public static void error(String jobID, List<ArrayList<String>> log, String owner, String repo, String commitSha) {
        Build failedBuild = new Build(jobID, Build.Result.error, commitSha, owner + "/" + repo, log, getTimeString());
        StatusUpdater.updateStatus(owner, repo, commitSha, Build.Result.error, jobID);

        try {
            BuildJob.storage.post(failedBuild);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Failed job " + jobID + " with log ");
        System.out.println(log);

    }

    /**
     * This function is called if the build successfully compiles and passes all tests.
     * Updates commit status to "success" and stores the build in the database with its log.
     *
     * @param jobID
     * @param log
     * @param owner
     * @param repo
     * @param commitSha
     */
    public static void success(String jobID, List<ArrayList<String>> log, String owner, String repo, String commitSha) {

        Build succeededBuild = new Build(jobID, Build.Result.success, commitSha, owner + "/" + repo, log, getTimeString());
        StatusUpdater.updateStatus(owner, repo, commitSha, Build.Result.success, jobID);

        try {
            BuildJob.storage.post(succeededBuild);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Succeeded job " + jobID + " with log ");
        System.out.println(log);
    }

    /**
     * This function is called if the build compiles but fails one or more tests.
     * Updates commit status to "failure" and stores the build in the database with its log.
     *
     * @param jobID
     * @param log
     * @param owner
     * @param repo
     * @param commitSha
     */
    public static void fail(String jobID, List<ArrayList<String>> log, String owner, String repo, String commitSha) {
        Build failedBuild = new Build(jobID, Build.Result.failure, commitSha, owner + "/" + repo, log, getTimeString());
        StatusUpdater.updateStatus(owner, repo, commitSha, Build.Result.failure, jobID);

        try {
            BuildJob.storage.post(failedBuild);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Failed job " + jobID + " with log ");
        System.out.println(log);
    }

}