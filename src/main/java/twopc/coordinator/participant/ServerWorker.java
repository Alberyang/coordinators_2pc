package twopc.coordinator.participant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import twopc.coordinator.common.SocketUtil;
import twopc.coordinator.common.Stage;
import twopc.coordinator.common.TransferMessage;
import twopc.coordinator.dao.SqlService;
import twopc.coordinator.dao.SqlServiceImpl;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

@Data
@NoArgsConstructor
public class ServerWorker {
    private TransferMessage transferMessage;
    private Socket coConnection;
    private Connection sqlConnection;

    public ServerWorker(Socket coConection, Connection sqlConnection,TransferMessage transferMessage){
        this.sqlConnection = sqlConnection;
        this.coConnection = coConection;
        this.transferMessage = transferMessage;

    }
    public void responseTransferMessage(Socket coConection,TransferMessage transferMessage){

        try {
            System.out.println("ready to send to coordinator");
            OutputStream outputStream = coConection.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            BufferedWriter out = new BufferedWriter(outputStreamWriter);
            out.write(JSONObject.toJSONString(transferMessage));
            out.flush();
            System.out.println("send to coordinator done");

        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error happened when server transmitted message to the coordinator");
        }

    }
    public void work(){
        Integer port = transferMessage.getPort();
        BufferedWriter out = SocketUtil.createOutputStream(this.coConnection);
        SqlServiceImpl sqlService = new SqlServiceImpl(sqlConnection,transferMessage);
        //first phase: vote-request / pre-commit
        if(transferMessage.getStage().getCode()==1){
            System.out.println("Current stage is "+ transferMessage.getStage());
            System.out.println("Receiving message from coordinator "+ transferMessage.getMsg());
            try{
//                sqlService.saveLog(transferMessage.getPort());
                if(port==9001){
                    sqlService.placeOrder();
                }
                if(port==9002){
                    sqlService.delteInventory();
                }
                transferMessage.setStage(Stage.VOTE_COMMIT);
                transferMessage.setMsg("This server votes commit to coordinator");
            }catch (Exception e){
                transferMessage.setMsg("Local Transaction execution fails");
                transferMessage.setStage(Stage.VOTE_ABORT);
            }finally {
                SocketUtil.responseTransferMsg(out,transferMessage);
            }

        } else if(transferMessage.getStage().getCode() == 4){
            // globle commit
            // 数据库提交
            // 更新数据库日志表
            System.out.println("Current stage is "+ transferMessage.getStage());
            System.out.println("Receiving message from coordinator "+ transferMessage.getMsg());
            try {
                if(sqlConnection!=null){
                    this.sqlConnection.commit();
                    this.sqlConnection.close();
                    transferMessage.setMsg("This database commit successully");
                    transferMessage.setStage(Stage.COMMIT_SUCCESS);
                }
            }catch (Exception e){
                e.printStackTrace();
                transferMessage.setMsg("Database commit fails");
                transferMessage.setStage(Stage.ABORT);
            }
            finally {
                SocketUtil.responseTransferMsg(out,transferMessage);
            }


        }else if(transferMessage.getStage().getCode() == 5){
            // globle rollback阶段
            // 本地数据库rollback
            System.out.println("Current stage is "+ transferMessage.getStage());
            System.out.println("Receiving message from coordinator "+ transferMessage.getMsg());
            try {
                //数据库回滚
                this.sqlConnection.rollback();
                //记录日志
                this.sqlConnection.close();
                // 返回执行结果并返回给协调者
                transferMessage.setMsg("This database rollback successfully");
                // 回复进入初始化阶段 等待重新整个服务
                transferMessage.setStage(Stage.INIT);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                transferMessage.setMsg("This database rollback fails");
                transferMessage.setStage(Stage.ABORT);
            }finally {
                SocketUtil.responseTransferMsg(out,transferMessage);
            }

        }
    }


}