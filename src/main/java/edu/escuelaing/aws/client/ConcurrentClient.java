package edu.escuelaing.aws.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 *
 * @author alejo
 */
public class ConcurrentClient extends Thread{
  
    public static int CLIENT_THREADS = 10;
    private URL url;
    private int responseCode;
    private String response;

    /**
     * Concurrent client constructor
     * @param url Server url
     */
    public ConcurrentClient(URL url) {
        this.url = url;
        responseCode = 0;
    }

    /**
     * Makes the GET request
     */
    @Override
    public void run() {
        try {
            HttpURLConnection yc = (HttpURLConnection) url.openConnection();
            responseCode = yc.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            response = sb.toString();
            in.close();
            yc.disconnect();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Gets the response code
     * @return response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Gets the server response
     * @return response
     */
    public String getResponse(){
        return response;
    }

    /**
     * Main method for executing the client requests
     * @param args Contains the url
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        URL url = new URL(args[0]);
        CLIENT_THREADS = Integer.parseInt(args[1]);
        ConcurrentClient[] concurrentClients = new ConcurrentClient[CLIENT_THREADS];
        for (int i = 0; i < CLIENT_THREADS; i++) {
            concurrentClients[i] = new ConcurrentClient(url);
        }
        long startTime = System.nanoTime();
        for (int i = 0; i < CLIENT_THREADS; i++) {
            concurrentClients[i].start();
        }
        int success = 0;
        int fails = 0;
        for (int i = 0; i < CLIENT_THREADS; i++) {
            concurrentClients[i].join();
            System.out.println("Response for client " + i + " -> " + concurrentClients[i].getResponse());
            if (concurrentClients[i].getResponseCode() >= 200) {
                success++;
            } else {
                fails++;
            }
        }
        long elapsedTime = System.nanoTime() - startTime;
        System.out.printf("Success: %d\nFails: %d\nExecuting %d requests in %f seconds\n", success, fails, CLIENT_THREADS, (double) elapsedTime/1000000000);

    }
}
