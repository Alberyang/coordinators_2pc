package twopc.participant;


import lombok.Data;

@Data
public class OrderServer extends Server{

    public OrderServer(Integer serverPort,Integer clientPort) {
        super(serverPort,clientPort,"order");
    }

    public static void main(String[] args) {
        OrderServer orderServer = new OrderServer(8001,9001);
        orderServer.serve(orderServer.connect());
    }
}
