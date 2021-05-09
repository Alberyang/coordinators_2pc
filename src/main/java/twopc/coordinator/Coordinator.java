package twopc.coordinator;

public class Coordinator {
    /**
     * Coordinator Starter
     */
    public static void main(String[] args) throws Exception{
        CoordinatorServer coordinatorServer = new CoordinatorServer();
        coordinatorServer.run();
    }
}