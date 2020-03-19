package edu.escuelaing.aws.server.requests;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author alejo
 */
public interface ResourceHandler {
    /**
     * Handles resources
     * @param resource A file path to the resource
     * @param ext The extension of the resource
     * @param socket The client socket
     * @throws IOException
     */
    void handleResource(File resource, String ext,Socket socket) throws IOException;
}
