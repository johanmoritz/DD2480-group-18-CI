package server.resources;

import buildtools.Build;
import buildtools.BuildJob;

import org.json.JSONException;
import org.json.JSONObject;
import server.ContinuousIntegrationServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("ci")
public class Resource {

    ExecutorService jobsQueue = Executors.newFixedThreadPool(10);

    /**
     * Fetches all builds from local database
     * path: /ci/get
     * @return - ArrayList with all build as Build objects.
     * @throws IOException - Storage.getAll() throws IOException
     */
    @GET
    @Path("get")
    @Produces("application/json")
    public ArrayList<Build> getBuilds() throws IOException {
        JSONObject dbJSON = ContinuousIntegrationServer.storage.getAll();
        ArrayList<Build> l = new ArrayList<>();

        // go through every job
        for (String key : dbJSON.keySet()) {
            Build b = new Build(key, dbJSON.getJSONObject(key));
            l.add(b);
        }

        return l;
    }

    // EXAMPLE POST REQUEST HANDLER
    @POST
    @Path("push")
    @Consumes("application/x-www-form-urlencoded")
    public Response push(@FormParam("payload") String payload) {

        // TODO
        // 4. run tests

        String jobID = UUID.randomUUID().toString();


        JSONObject json = new JSONObject(payload);
        JSONObject repository = json.getJSONObject("repository");

        try {
            json.getJSONObject("hook");
            System.out.println("New webhook: " + repository.getString("full_name"));
            return Response.status(200).build();
        } catch (JSONException ignored) {}

        String owner = repository.getJSONObject("owner").getString("name");
        String repo = repository.getString("name");
        String commitSha = json.getJSONArray("commits").getJSONObject(0).getString("id");

        String branchRef = json.getString("ref");
        String cloneUrl = repository.getString("clone_url");


        // Run build jobs asynchronously
        Runnable job = () -> BuildJob.run(jobID, cloneUrl, branchRef, owner, repo, commitSha);
        jobsQueue.execute(job);

        return Response.status(200).build();
    }

}
