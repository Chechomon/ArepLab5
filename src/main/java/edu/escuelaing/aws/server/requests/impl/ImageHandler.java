package edu.escuelaing.aws.server.requests.impl;

import edu.escuelaing.aws.server.requests.ResourceHandler;
import java.io.*;
import java.net.Socket;
/**
 *
 * @author alejo
 */
public class ImageHandler implements ResourceHandler{
     /**
     * Handles image resources
     * @param resource A file path to the resource
     * @param ext The extension of the resource
     * @param socket The client socket
     * @throws IOException
     */
    @Override
    public void handleResource(File resource, String ext, Socket socket) throws IOException {
        String header = "HTTP/1.1 200 \r\nAccess-Control-Allow-Origin: *\r\nContent-Type: image/" + ext + "\r\nConnection: close\r\nContent-Length:" + resource.length() + "\r\n\r\n";
        FileInputStream fileIn = new FileInputStream(resource);
        OutputStream os = socket.getOutputStream();
        for (char c : header.toCharArray()) {
            os.write(c);
        }
        int a;
        while ((a = fileIn.read()) > -1) {
            os.write(a);
        }
        os.flush();
        fileIn.close();
        os.close();
    }
}
