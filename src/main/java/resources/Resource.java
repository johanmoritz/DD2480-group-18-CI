package resources;

import buildtools.Build;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("ci")
public class Resource {
    // EXAMPLE GET REQUEST HANDLER
    // A list of of builds is sent by returning List<Build> instead.
    // TODO
    // load a previous build from storage
    // load multiple previous builds from storage
    @GET
    @Produces("application/json")
    public Build hello() {
        return new Build("jobID", "status", "commitSha", "url", "log");
    }

    // EXAMPLE POST REQUEST HANDLER
    @POST
    @Path("push")
    @Consumes("application/x-www-form-urlencoded")
    public Response push(@FormParam("payload") String payload) {

        // TODO
        // 1. set commit status to pending
        // 2. clone repository
        /*
        try {
            Git git = Git.cloneRepository().setURI("https://github.com/eclipse/jgit.git").call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        */
        // 3. build
        // 4. run tests
        // 5. store build data
        // 6. set commit status to success/fail/error

        System.out.println();
        JSONObject json = new JSONObject(payload);
        //String cloneURL = json.getJSONObject("repository").getString("clone_url");
        System.out.println(payload);

        return Response.status(200).build();
    }

}
