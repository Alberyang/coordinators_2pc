package twopc.coordinator.common;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class SocketUtil {
    private TransferMessage transferMessage;
    private Socket socket;
    public SocketUtil(Socket socket){
        this.socket = socket;
    }

    public static TransferMessage parseTransferMessage(String jsonMsg){
        return JSONObject.parseObject(jsonMsg, TransferMessage.class);
    }
    public static BufferedWriter createOutputStream(Socket socket){
        try {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            BufferedWriter out = new BufferedWriter(outputStreamWriter);
            return out;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error happened when create outputsteam object");
        }
        return null;
    }
    public static void responseTransferMsg(BufferedWriter out,TransferMessage transferMessage){
        try{
            System.out.println("ready to send to coordinator");
            out.write(JSONObject.toJSONString(transferMessage));
            out.flush();
            System.out.println("send to coordinator done");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error happened when response transfer message to server");
        }
    }

    public TransferMessage readTransferMessage(Socket coConection){
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try{
            inputStream = coConection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String temp = "";
//            while ((temp = bufferedReader.readLine()) != null) {
//                TransferMessage transferMessage = JSONObject.parseObject(temp.toString(), TransferMessage.class);
//            }
            return JSONObject.parseObject(temp, TransferMessage.class);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            // close
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(inputStreamReader!=null){
                    inputStreamReader.close();
                }
                if(bufferedReader!=null){
                    bufferedReader.close();
                }
            }catch (Exception e){
                System.out.println("关闭流失败");
                e.printStackTrace();
            }

        }
        return null;
    }
    public TransferMessage getTransferMessage() {
        return transferMessage;
    }

    public void setTransferMessage(TransferMessage transferMessage) {
        this.transferMessage = transferMessage;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
