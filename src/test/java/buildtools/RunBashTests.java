package buildtools;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.rules.ExpectedException;


import static org.junit.Assert.*;

import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import static org.junit.Assert.*;

public class RunBashTests {

    /*
    * Tests that the runCommand function executes a command and records the output correctly
    */
    @Test
    public void test0() {
        File file = new File("runbashtest.txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("This \n");
            writer.write("is \n");
            writer.write("the \n");
            writer.write("test sentence");
            writer.close();
            try {
                ArrayList<String> cmd = RunBash.runCommand("cat runbashtest.txt", "runbashtest.txt");
                ArrayList<String> comp = new ArrayList<String>();
                comp.add("This");
                comp.add("is");
                comp.add("the");
                comp.add("test sentence");
                assertEquals(comp, cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}