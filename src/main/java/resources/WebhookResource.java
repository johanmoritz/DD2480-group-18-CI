package resources;

import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("ci")
public class WebhookResource {
    // Create "build" class for storing and sending
    public class Build {
        private String message;

        public Build(String name) {
            this.message = getGreeting(name);
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String name) {
            this.message = name;
        }

        private String getGreeting(String name) {
            return "Hello " + name;
        }

    }

    // EXAMPLE GET REQUEST HANDLER
    // A list of of builds is sent by returning List<Build> instead.
    // TODO
    // load a previous build from storage
    // load multiple previous builds from storage
    @GET
    @Produces("application/json")
    public Build hello() {
        return new Build("genius!");
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

        JSONObject json = new JSONObject(payload);
        String cloneURL = json.getJSONObject("repository").getString("clone_url");
        System.out.println(cloneURL);

        return Response.status(200).build();
    }

}
