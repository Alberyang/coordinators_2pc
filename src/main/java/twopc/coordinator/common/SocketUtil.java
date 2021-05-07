package twopc.coordinator.common;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;

public class SocketUtil {
    private TransferMessage transferMessage;
    private Socket coConnection;
    public SocketUtil(Socket coConection){
        this.coConnection = coConection;
    }

    public TransferMessage readTransferMessage(Socket coConection){
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try{
            inputStream = coConection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder msg = new StringBuilder();

//            while ((temp = bufferedReader.readLine()) != null) {
//                info.append(temp);
//                System.out.println("server accept connection");
//                System.out.println("client info:" + info);
//            }
            return JSONObject.parseObject(msg.toString(),TransferMessage.class);
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
    public void responseTransferMessage(Socket coConection,TransferMessage transferMessage){
        //相应协调者
        try {
            System.out.println("ready to send to coordinator");
            OutputStream outputStream = coConection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.print(JSONObject.toJSONString(transferMessage));
            printWriter.flush();
            coConection.shutdownOutput();
            System.out.println("send to coordinator done");
            printWriter.close();
            outputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
