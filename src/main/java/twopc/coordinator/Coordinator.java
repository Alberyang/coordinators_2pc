package twopc.coordinator;

import org.eclipse.jetty.server.Server;
import twopc.coordinator.handler.ShoppingHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coordinator {
    public static Set<Integer> portSet= new HashSet<Integer>();
    public static ExecutorService executor = Executors.newFixedThreadPool(5);

    /**
     * Socket client
     */
    public static void main(String[] args) throws Exception{
        Server server = new Server(8001);
        server.setHandler(new ShoppingHandler());
        server.start();
        server.join();
    }
}