package twopc.participant;
import twopc.common.LastStatus;
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
    private LastStatus lastStatus = null;
    public Server(Integer serverPort,Integer clientPort, String database) {
        this.database = database;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.sqlConnection = DbUtils.getConnection(this.database);
        this.lastStatus = new LastStatus();
    }
    // Connect to the coordinator
    public Socket connect(){
        int reconnect_num = 0;
        Socket socket = null;
        while(socket==null){
            try {
                socket = new Socket();
                socket.bind(new InetSocketAddress(this.clientPort));
                socket.connect(new InetSocketAddress("localhost",this.serverPort));
                socket.setKeepAlive(true);
                System.out.println("The Server has been connected with coordinator");
                return socket;
            }catch (IOException e){
//                e.printStackTrace();
                ++reconnect_num;
                socket=null;
                System.out.println("Connecting to the coordinator for " + reconnect_num +" times");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return socket;

    }
    //
    public void serve(Socket socket){
        while(true) {
            try {
                    if(socket.isClosed()){
                        socket = this.connect();
                    }
                    BufferedReader in = SocketUtil.createInputStream(socket);
                    String temp = null;
                    while ((temp = in.readLine())!=null) {
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
                            System.out.println("--------------------------------------------------------------------");
                            System.out.println("This server received "+transferMessage);
                            ServerWorker orderServerWorker = new ServerWorker(socket, sqlConnection,transferMessage,lastStatus);
                            orderServerWorker.work();
                        }else {
                            System.out.println("The message server received can not be identified");
                        }
                    }
            } catch (IOException e1) {
//                e1.printStackTrace();
                System.out.println("can't connect with coordinator, trying to reconnect");
                try {
                    if(!socket.isClosed()){
                        socket.close();
                    }

                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }catch (Exception e){
                System.out.println("This server has some exceptions, which is that "+e.getMessage());
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
