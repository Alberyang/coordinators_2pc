package twopc.coordinator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class IOThread extends Thread{
    private static final Logger log = Logger.getLogger(IOThread.class.getName());
    private ServerSocket serverSocket = null;
    private CoordinatorServer coordinatorServer;

    public IOThread(int port, CoordinatorServer coordinatorServer) throws IOException {
        serverSocket = new ServerSocket(port);
        this.coordinatorServer = coordinatorServer;
        setName("IOThread");
        start();
    }

    @Override
    public void run() {
        // Client Socket Accept
        while (!isInterrupted() && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                log.info("Received connection from " + clientSocket.getInetAddress());
                coordinatorServer.acceptClient(clientSocket);
            } catch(Exception e){
                log.warning("Exception closing server socket: " + e.getMessage());
            }
        }

        // Server socket Terminate
        log.info("IOThread terminating");
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.warning("Exception closing server socket: "+e.getMessage());
        }
    }

    /**
     * Close the server socket and make sure the thread terminates.
     */
    public void shutDown() {
        if(serverSocket!=null)
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.warning("exception closing server socket: "+e.getMessage());
            }
        interrupt();
    }
}
