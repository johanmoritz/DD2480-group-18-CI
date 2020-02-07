package buildtools;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileReader;

import java.io.File;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

public class RunBash {

    /**
     * Extract commands from dd.yml and runs the commands found there in bash
     * @param buildDirectoryPath - the relative path to the directory which commands are run in
     * @param buildConfigPath - the relative path to the build configuration file
     * @return output of all commands
     */

    public static ArrayList<ArrayList<String>> run(String buildDirectoryPath, String buildConfigPath) {
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        try {
            commands = readThis(buildDirectoryPath, buildConfigPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commands;
    }

    /**
     * reads through a file and searches for key words, then executes command line
     * below match and returns output of them all
     * @throws Exception
     */
    private static ArrayList<ArrayList<String>> readThis(String buildDirectoryPath, String buildConfigPath) throws Exception {
        BufferedReader reader;
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        try {
            reader = new BufferedReader(new FileReader(buildConfigPath));
            String line = reader.readLine();
            while (line != null) {
                if (exactMatch(line, "Build") || exactMatch(line, "Test") ) {
                    line = reader.readLine();
                    if (line != null) {
                        commands.add(runCommand(line, buildDirectoryPath));
                    }
                    // read next line
                }
                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commands;
    }

    /**
     * Find the Instructions in a file
     * @param line - A string of a line
     * @param match - Keyword
     */
    private static boolean exactMatch(String line, String match) {
        String pattern = "\\b" + match + "\\b";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(line);
        return m.find();
    }

    /**
     * reads output and errors of the command execution
     */
    private static ArrayList<String> output(InputStream in) throws Exception {
        String ln;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        ArrayList<String> output = new ArrayList<String>();
        while ((ln = reader.readLine()) != null) {
            output.add(ln);
        }
        return output;
    }

    /**
     * This functions runs the String as a bash command
     * @param line - String with the command to be run in bash
     * @return output, errors, exit value
     * @throws Exception
     */
    private static ArrayList<String> runCommand(String line, String buildDirectoryPath) throws Exception {

        String[] arr = line.split(" ");

        ProcessBuilder pb = new ProcessBuilder(arr);
        pb.redirectErrorStream(true);
        pb.directory(new File(buildDirectoryPath));
        Process p = pb.start();

        ArrayList<String> cmdOutput = output(p.getInputStream());
        cmdOutput.addAll(output(p.getErrorStream()));
        p.waitFor();
        int eValue = p.exitValue();
        cmdOutput.add(Integer.toString(eValue));

        return cmdOutput;

    }
}
