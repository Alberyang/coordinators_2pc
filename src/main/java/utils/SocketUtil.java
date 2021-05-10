package utils;

import com.alibaba.fastjson.JSONObject;
import twopc.common.TransferMessage;
import twopc.participant.ServerWorker;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
    public static void sendTransferMsg(BufferedWriter out,TransferMessage transferMessage){
        try{
            System.out.println("ready to send to participant");
            out.write(JSONObject.toJSONString(transferMessage));
            out.flush();
            System.out.println("send to participant done");
            System.out.println("已发送"+JSONObject.toJSONString(transferMessage));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error happened when send transfer message to server");
        }
    }
    public static BufferedReader createInputStream(Socket coConection){
        try{
            InputStream inputStream = coConection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(inputStreamReader);
            return in;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error happened when create the inputstream");
        }
        return null;
    }

    public static TransferMessage getResponse(BufferedReader in) throws IOException {
        String temp = null;
        TransferMessage transferMessage = null;
        if ((temp = in.readLine())!=null) {
            try {
                transferMessage = SocketUtil.parseTransferMessage(temp);
            } catch (Exception e) {
                System.out.println("Msg from coordinator can not be parsed as the object TransferMessage");
            }
            if(transferMessage!=null){
                System.out.println("This server received the object 'TransferMessage'");
            }else {
                System.out.println("The message server received can not be identified");
            }
        }
        return transferMessage;
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
