package edu.escuelaing.aws.server.requests;

import edu.escuelaing.aws.server.requests.impl.ImageHandler;
import edu.escuelaing.aws.server.requests.impl.TextHandler;
import java.io.*;
import java.net.Socket;
import java.util.regex.*;
import org.apache.commons.io.FilenameUtils;


/**
 *
 * @author alejo
 */
public class RequestHandler {

    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private ResourceHandler resourceHandler;

    /**
     * Constructor for the RequestHandler object
     * @param socket client socket
     * @throws IOException
     */
    public RequestHandler(Socket socket) throws IOException {
        clientSocket = socket;
        in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(
                clientSocket.getOutputStream(), true);
        resourceHandler = new TextHandler(); //default handler
    }

    /**
     * Reads the client requests
     * @throws IOException
     */
    public void readClientRequest() throws IOException{
        String inputLine;
        StringBuilder stringBuilder = new StringBuilder();
        Pattern pattern = Pattern.compile("GET (/[^\\s]*)");
        Matcher matcher = null;
        while ((inputLine = in.readLine()) != null) {
            //System.out.println("Recibí: " + inputLine);
            stringBuilder.append(inputLine);
            if (!in.ready()) {
                matcher = pattern.matcher(stringBuilder.toString());
                if (matcher.find()) {
                    String fileRequested = matcher.group().substring(5);
                    //System.out.println("VALUE: " + fileRequested);
                    handleRequest(fileRequested);
                }
                break;
            }
        }

        in.close();
        clientSocket.close();
    }

    /**
     * Handles how to send back a requested resource
     *
     * @param fileRequested The requested resource
     * @throws IOException
     */
    private void handleRequest(String fileRequested) throws IOException{
        String filePath = "src/main/resources/";
        String ext = FilenameUtils.getExtension(fileRequested);
        switch (ext) {
            case "png":
                filePath += "images/" + fileRequested;
                resourceHandler = new ImageHandler(); //change handler for images handler
                break;
            case "js":
                filePath += "js/" + fileRequested;
                break;
            case "html":
                filePath += "web-pages/" + fileRequested;
                break;
        }
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            resourceHandler.handleResource(file, ext, clientSocket);
        } else {
            out.println("HTTP/1.1 404\r\nAccess-Control-Allow-Origin: *\r\n\r\n<html><body><h1>404 NOT FOUND ("+fileRequested+")</h1></body></html>");
        }
    }
}
