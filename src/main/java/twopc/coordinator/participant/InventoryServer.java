package twopc.coordinator.participant;

public class InventoryServer extends Server {

    public InventoryServer(Integer port) {
        super(port,"2pc_inventory");
    }

    public static void main(String[] args) {
        InventoryServer inventoryServer = new InventoryServer(8001);
        inventoryServer.serve(inventoryServer.connect());
    }
}
