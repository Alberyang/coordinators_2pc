package twopc.coordinator.participant;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import twopc.coordinator.common.TransferMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        if(socket==null){
            System.out.println("It can't connect to the coordinator after reconnecting for "+reconnect_num+" times");
            System.exit(-1);
        }
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        while(true) {
            try {
                inputStream = socket.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                String temp = null;
                while ((temp = bufferedReader.readLine()) != null) {
                    TransferMessage transferMessage = JSONObject.parseObject(temp, TransferMessage.class);
                    System.out.println("This server received the object 'TransferMessage'");
                    ServerWorker orderServerWorker = new ServerWorker(socket, sqlConnection);
                    orderServerWorker.work();
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    if (!socket.isClosed()) {
                        socket.close();
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                System.out.println("Fetal Error! The server has stopped!");
            }
        }
    }

    public static void main(String[] args) {

    }
}
