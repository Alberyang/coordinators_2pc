package twopc.coordinator;

import org.eclipse.jetty.server.Server;
import twopc.common.Stage;
import twopc.common.TransferMessage;
import twopc.coordinator.handler.ShoppingHandler;
import utils.SocketUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

public class CoordinatorServer {
    public static HashMap<Integer, Socket> participants = new HashMap<Integer, Socket>();
    private static final Logger log = Logger.getLogger(CoordinatorServer.class.getName());
    private static final int port = 9000;

    /**
     * Socket client
     */
    public void run() throws Exception{
        // HTTP Server
        Server server = new Server(8001);
        server.setHandler(new ShoppingHandler());
        server.start();

        // Localhost socket server
        IOThread ioThread;
        try {
            ioThread = new IOThread(port, this);
        } catch (IOException e1) {
            log.severe("could not start the io thread");
            return;
        }

        try {
            // just wait for this thread to terminate
            ioThread.join();
        } catch (InterruptedException e) {
            // just make sure the ioThread is going to terminate
            ioThread.shutDown();
        }

        log.info("io thread has joined");
    }

    public void acceptClient(Socket socket){
        int clientPort = socket.getPort();
        participants.put(clientPort, socket);
        log.info("Server:" + clientPort + " is online.");
    }

    public static void commitRequest(TransferMessage message){
        // Send commit request
        participants.forEach((key, value) -> {
            BufferedWriter out = SocketUtil.createOutputStream(value);
            message.setPort(key);
            SocketUtil.sendTransferMsg(out, message);
        });
    }

    public static void rollback(Stage stage, TransferMessage message){
        // Rollback request
        participants.forEach((key, value) -> {
            BufferedWriter out = SocketUtil.createOutputStream(value);
            message.setPort(key);
            message.setStage(stage);
            message.setMsg("Commit failed, global rollback");
            SocketUtil.sendTransferMsg(out, message);
        });
    }
}