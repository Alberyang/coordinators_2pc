package twopc.participant;


public class OrderServer extends Server{

    public OrderServer(Integer serverPort,Integer clientPort) {
        super(serverPort,clientPort,"order");
    }

    public static void main(String[] args) {
        OrderServer orderServer = new OrderServer(9000,9001);
        orderServer.serve(orderServer.connect());
    }
}
