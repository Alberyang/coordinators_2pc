package twopc.participant;

public class InventoryServer extends Server {

    public InventoryServer(Integer serverPort,Integer clientPort) {
        super(serverPort,clientPort,"inventory");
    }

    public static void main(String[] args) {
        InventoryServer inventoryServer = new InventoryServer(9000,9002);
        inventoryServer.serve(inventoryServer.connect());
    }
}
