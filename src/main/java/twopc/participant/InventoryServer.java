package twopc.participant;

public class InventoryServer extends Server {

    public InventoryServer(Integer port) {
        super(port,"inventory");
    }

    public static void main(String[] args) {
        InventoryServer inventoryServer = new InventoryServer(8001);
        inventoryServer.serve(inventoryServer.connect());
    }
}
