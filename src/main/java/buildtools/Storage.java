package buildtools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import org.json.JSONObject;
import org.json.JSONException;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Stores and fetches data from a json file regarding build info
 */
public class Storage {
    String fileName = "db.json";
    /**
     * Creates a new json data object if none exists already
     */
    public Storage() {
        // Check if database already exists
        File db = new File("db.json");
        if (!db.exists()) {
            JSONObject data = new JSONObject();

            // Write to JSON file
            try (FileWriter file = new FileWriter(fileName)) {
                file.write(data.toString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Taken from https://stackoverflow.com/questions/7463414/what-s-the-best-way-to-load-a-jsonobject-from-a-json-text-file
     * @param file name of file name
     * @return json object of the file given
     * @throws JSONException - file content is not valid JSON.
     * @throws IOException - cannot read from file with name @param file
     */
    private JSONObject parseJSON(String file) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(file)));
        return new JSONObject(content);
    }

    /**
     * Writes a build to the database file in JSON format.
     * @param build - the Build object to be stored.
     * @throws IOException - if db file is not found.
     */
    public void post(Build build) throws IOException {
        File db = new File(fileName);
        if (!db.exists()) {
            throw new IOException("Database file not found");
        } else {
            JSONObject dbJSON = parseJSON(fileName);
            JSONObject data = new JSONObject();
            data.put("status", build.getStatus());
            data.put("commitSha", build.getCommitSha());
            data.put("url", build.getUrl());
            data.put("log", build.getLog());
            dbJSON.put(build.getJobID(), data);

            // Write to JSON file
            try (FileWriter file = new FileWriter(fileName)) {
                file.write(dbJSON.toString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads a build from the database.
     * @param jobID - the build's jobID.
     * @return build as Build object
     * @throws IOException - if db file is not found
     */
    public JSONObject get(String jobID) throws IOException {
        File db = new File(fileName);
        if (!db.exists()) {
            throw new IOException("Database file not found");
        } else {
            JSONObject dbJSON = parseJSON(fileName);

            // Try to get object, if it doesn't exist then return empty
            JSONObject rv;
            try {
                rv = dbJSON.getJSONObject(jobID);
            } catch (JSONException e) {
                rv = new JSONObject();
            }

            return rv;
        }
    }

    /**
     * Gets all build from the database.
     * @return all builds as a JSONObject.
     * @throws IOException if database file was not found
     */
    public JSONObject getAll() throws IOException {
        File db = new File(fileName);
        if (!db.exists()) {
            throw new IOException("Database file not found");
        } else {
            return parseJSON(fileName);
        }
    }
}