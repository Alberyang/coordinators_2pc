package twopc.coordinator.participant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;

@Data
@AllArgsConstructor
public class OrderServer {
    private Connection sqlConnection;
    private Integer port;
    public OrderServer(Connection connection) {
        this.sqlConnection = connection;
    }
    // Connect to the coordinator
    public Socket connect(){
        try {
            Socket socket = new Socket("localhost",this.port);
            socket.setKeepAlive(true);
            System.out.println("Server order has been connected with coordinator");
            return socket;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //
    public void work(Socket socket){
        int reconnect_num = 5;
        while(socket==null && reconnect_num>0){
            socket = this.connect();
            reconnect_num--;
        }
        if(socket!=null){
            ServerWorker orderServerWorker = new ServerWorker(socket,sqlConnection);
            orderServerWorker.work();
        }else {
            System.out.println("It can't connect to the coordinator after reconnecting for "+reconnect_num+" times");
            System.exit(-1);
        }

    }

    public static void main(String[] args) {

    }
}
