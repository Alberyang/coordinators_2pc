package twopc.coordinator;

import org.eclipse.jetty.server.Server;
import twopc.common.Stage;
import twopc.common.TransferMessage;
import twopc.coordinator.handler.ShoppingHandler;
import utils.SocketUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Coordinator {
    public static HashMap<Integer, Socket> participants = new HashMap<Integer, Socket>();
    public static ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final Logger log = Logger.getLogger(Coordinator.class.getName());
    private static final int port = 9000;

    /**
     * Socket client
     */
    public static void main(String[] args) throws Exception{
        // HTTP Server
        Server server = new Server(8001);
        server.setHandler(new ShoppingHandler());
        server.start();
        server.join();

        // Localhost socket server
        IOThread ioThread;
        try {
            ioThread = new IOThread(port, new Coordinator());
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

    public static void rollback(Stage stage){
        // Rollback request
        TransferMessage message = new TransferMessage();
        participants.forEach((key, value) -> {
            BufferedWriter out = SocketUtil.createOutputStream(value);
            message.setPort(key);
            message.setStage(stage);
            SocketUtil.sendTransferMsg(out, message);
        });
    }
}