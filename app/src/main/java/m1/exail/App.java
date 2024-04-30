package m1.exail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;

public class App {

    public static void main(String[] args) {
        String directoryPath = "C:\\Users\\lgair\\Downloads\\iperf3.16_64\\iperf3.16_64";

        // Command to be executed
        String command = "iperf3 -s -p 8888";
        // String command = "iperf3 -c X.X.X.X -p 8888";

        // Create a ProcessBuilder
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);

        // Set the working directory
        builder.directory(new File(directoryPath));

        try {
            // Start the process
            Process process = builder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the command to complete and get the exit code
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);

            // Close the reader
            reader.close();

        } catch (IOException | InterruptedException ignored) { }
    }
}