package twopc.coordinator.participant;


import lombok.Data;

@Data
public class OrderServer extends Server{

    public OrderServer(Integer port) {
        super(port,"2pc_order");
    }

    public static void main(String[] args) {
        OrderServer orderServer = new OrderServer(9000);
        orderServer.serve(orderServer.connect());
    }
}
