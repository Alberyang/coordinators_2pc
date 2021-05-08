package twopc.coordinator;

import org.eclipse.jetty.server.Server;
import twopc.coordinator.handler.ShoppingHandler;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Coordinator {
    public static HashMap<Integer, Socket> participants = new HashMap<Integer, Socket>();
    public static ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final Logger log = Logger.getLogger(Coordinator.class.getName());

    /**
     * Socket client
     */
    public static void main(String[] args) throws Exception{
        Server server = new Server(8001);
        server.setHandler(new ShoppingHandler());
        server.start();
        server.join();
    }


//    @SuppressWarnings("InfiniteLoopStatement")
//    public static void initCoordinator(){
//
//        try {
//            // Coordinator Socket Setup
//            ServerSocket serverSocket = new ServerSocket(9000);
//            // Client Socket
//            Socket socket = new Socket();
//
//            while(true){
//                socket = serverSocket.accept();
//
////                ServerThread thread = new ServerThread(socket);
////                thread.start();
//
//                int clientPort = socket.getPort();
//                participants.put(clientPort, socket);
//                log.info("Server:" + clientPort + " is online.");
//            }
//        } catch (Exception e) {
//            log.warning("Exception closing server socket: "+e.getMessage());
//        }
//    }
}