package twopc.coordinator.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import twopc.coordinator.common.TransferMessage;

import java.io.IOException;
import java.net.ServerSocket;
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
    private void serverLaunch(){
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            System.out.println("Server order has been launched");
            while(true){
                Socket coConnection = serverSocket.accept();
                //生成新连接？
                OrderServerWorker orderServerWorker = new OrderServerWorker(coConnection,sqlConnection);
                orderServerWorker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }
}
