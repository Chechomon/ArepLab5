package edu.escuelaing.aws.server;

import edu.escuelaing.aws.server.requests.RequestHandler;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 *
 * @author alejo
 */
public class WebServer {
  
    private static final int MAX_THREADS = 10;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean running;
    private ExecutorService threadPool;

    /**
     * Creates an instance of the Http Server object
     */
    public WebServer() {
        int port = getPort();
        running = true;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }
        threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        clientSocket = null;
    }

    /**
     * Starts the server, begins to listen to connections
     *
     * @throws IOException
     */
    public void start() throws IOException {
        while (running) {
            //System.out.println("Ready to receive");
            clientSocket = serverSocket.accept();
            RequestHandler requestHandler = new RequestHandler(clientSocket);
            Runnable runnable = () -> {
                try {
                    requestHandler.readClientRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            threadPool.execute(runnable);

        }
        threadPool.shutdown();
        serverSocket.close();
    }

    /**
     * This method reads the default port as specified by the PORT variable in
     * the environment.
     *
     * @return The port variable if set, else 4567 as default
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    /**
     * Main method that starts the HTTP Server
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new WebServer().start();
    }

}
