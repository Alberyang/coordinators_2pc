package twopc.participant;


import lombok.Data;

@Data
public class OrderServer extends Server{

    public OrderServer(Integer port) {
        super(port,"order");
    }

    public static void main(String[] args) {
        OrderServer orderServer = new OrderServer(8001);
        orderServer.serve(orderServer.connect());
    }
}
