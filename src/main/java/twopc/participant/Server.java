package twopc.participant;
import twopc.common.TransferMessage;
import utils.SocketUtil;
import utils.DbUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;

public abstract class Server {
    private Connection sqlConnection;
    private final Integer serverPort;
    private final Integer clientPort;
    private final String database;
    public Server(Integer serverPort,Integer clientPort, String database) {
        this.database = database;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.sqlConnection = DbUtils.getConnection(this.database);
    }
    // Connect to the coordinator
    public Socket connect(){
        try {
            Socket socket = new Socket();
            socket.bind(new InetSocketAddress(this.clientPort));
            socket.connect(new InetSocketAddress("localhost",this.serverPort));
            socket.setKeepAlive(true);
            System.out.println("Server order has been connected with coordinator");
            return socket;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    //
    public void serve(Socket socket){
        int reconnect_num = 5;
        while(socket==null && reconnect_num>0){
            socket = this.connect();
            reconnect_num--;
        }
        if(socket==null){
            System.out.println("It can't connect to the coordinator after reconnecting for 5 times");
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
                while ((temp = bufferedReader.readLine())!=null) {
                    TransferMessage transferMessage = null;
                    try {
                         transferMessage = SocketUtil.parseTransferMessage(temp);
                    }catch (Exception e){
                        System.out.println("Msg from coordinator can not be parsed as the object TransferMessage");
                        continue;
                    }

                    if(sqlConnection==null){
                        this.sqlConnection = DbUtils.getConnection(this.database);
                    }
                    if(transferMessage!=null){
                        System.out.println("This server received the object 'TransferMessage'");
                        ServerWorker orderServerWorker = new ServerWorker(socket, sqlConnection,transferMessage);
                        orderServerWorker.work();
                    }else {
                        System.out.println("The message server received can not be identified");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error, closing the connection with coordinator");
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

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public void setSqlConnection(Connection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public Integer getClientPort() {
        return clientPort;
    }
}