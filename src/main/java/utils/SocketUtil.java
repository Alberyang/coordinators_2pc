package utils;

import com.alibaba.fastjson.JSONObject;
import twopc.common.TransferMessage;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketUtil {

    /**
     * Convert transfer message from json to class
     * @param jsonMsg - transfer message in json format
     * @return Transfer message class of transfer message
     */
    public static TransferMessage parseTransferMessage(String jsonMsg){
        return JSONObject.parseObject(jsonMsg, TransferMessage.class);
    }

    /**
     * Create output stream for socket
     * @param socket - target socket
     * @return output stream buffered writer
     */
    public static BufferedWriter createOutputStream(Socket socket){
        try {
            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            return new BufferedWriter(outputStreamWriter);
        }catch (Exception e){
            System.out.println("Error happened when create outputsteam object");
        }
        return null;
    }

    /**
     * Response transfer message to coordinator
     * @param out - output stream buffered writer
     * @param transferMessage - transfer message needed to be sent
     */
    public static void responseTransferMsg(BufferedWriter out,TransferMessage transferMessage){
        try{
            System.out.println("ready to send to coordinator");
            out.write(JSONObject.toJSONString(transferMessage));
            out.write("\n");
            out.flush();
            System.out.println("Message" + JSONObject.toJSONString(transferMessage) + "has been successfully sent to coordinator");
        }catch (Exception e){
            System.out.println("Error happened when response transfer message to server");
        }
    }

    /**
     * Send transfer message to participant
     * @param out - output stream buffered writer
     * @param transferMessage - transfer message needed to be sent
     */
    public static void sendTransferMsg(BufferedWriter out,TransferMessage transferMessage){
        try{
            System.out.println("ready to send message to servers");
            out.write(JSONObject.toJSONString(transferMessage));
            out.write("\n");
            out.flush();
            System.out.println("Message" + JSONObject.toJSONString(transferMessage) + "has been successfully sent to servers");
        }catch (Exception e){
            System.out.println("Server has disconnected");
            System.out.println("Error happened when send transfer message to servers");
        }
    }

    /**
     * Create input stream for socket
     * @param socket - target socket
     * @return input stream buffered reader
     */
    public static BufferedReader createInputStream(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream,StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    /**
     * Get response from the other socket
     * @param in - input stream buffered reader
     * @return Transfer message received
     */
    public static TransferMessage getResponse(BufferedReader in) throws IOException {
        String temp;
        TransferMessage transferMessage = null;
        if ((temp = in.readLine())!=null) {
            transferMessage = SocketUtil.parseTransferMessage(temp);
        }
        return transferMessage;
    }
}
