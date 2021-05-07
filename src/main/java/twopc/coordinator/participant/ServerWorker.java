package twopc.coordinator.participant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import twopc.coordinator.common.TransferMessage;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

@Data
@NoArgsConstructor
public class ServerWorker {
    private TransferMessage transferMessage;
    private Socket coConnection;
    private Connection sqlConnection;
    public ServerWorker(Socket coConection, Connection sqlConnection){
        this.sqlConnection = sqlConnection;
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
//        try {

//        }
        //关闭输出流
    }
    public void work(){
        this.transferMessage = this.readTransferMessage(this.coConnection);
        //first phase: vote-request
        if(transferMessage.getStage().getCode()==1){
            //数据库本地执行 不提交
            System.out.println(" Current stage is "+ transferMessage.getStage());

        } else if(transferMessage.getStage().getCode() == 4){
            // globle commit
            // 数据库提交
            // 更新数据库日志表
            try {
                this.sqlConnection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            // 返回执行结果并返回给协调者
            transferMessage.setMsg("order database commit successully");
            this.responseTransferMessage(coConnection,transferMessage);

        }else if(transferMessage.getStage().getCode() == 5){
            // globle rollback阶段
            // 本地数据库rollback
            try {
                //数据库回滚
//                this.sqlConnection.rollback();
                //记录日志
                this.sqlConnection.close();
                // 返回执行结果并返回给协调者
                transferMessage.setMsg("order database rollback successfully");
                this.responseTransferMessage(coConnection,transferMessage);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }


}
