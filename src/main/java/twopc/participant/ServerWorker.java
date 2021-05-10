package twopc.participant;

import lombok.Data;
import lombok.NoArgsConstructor;
import utils.SocketUtil;
import twopc.common.Stage;
import twopc.common.TransferMessage;
import twopc.dao.SqlServiceImpl;
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

    public ServerWorker(Socket coConection, Connection sqlConnection,TransferMessage transferMessage){
        this.sqlConnection = sqlConnection;
        this.coConnection = coConection;
        this.transferMessage = transferMessage;
    }
    public void work(){
        Integer port = transferMessage.getPort();
        BufferedWriter out = SocketUtil.createOutputStream(this.coConnection);
        SqlServiceImpl sqlService = new SqlServiceImpl(sqlConnection,transferMessage);
        transferMessage.setFrom("Server");
        transferMessage.setTo("Coordinator");
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
                    int[] results = sqlService.deleteInventory();
                    for(int i:results){
                        if(i==0){
                            throw new SQLException("Inventory is not enough");
                        }
                    }
                }
                transferMessage.setStage(Stage.VOTE_COMMIT);
                transferMessage.setMsg("This server votes commit to coordinator");
            }catch (Exception e){
                transferMessage.setMsg("Local Transaction execution fails, the reason is that "+ e.getMessage());
                transferMessage.setStage(Stage.VOTE_ABORT);

            }finally {
                SocketUtil.responseTransferMsg(out,transferMessage);
            }

        } else if(transferMessage.getStage().getCode() == 4){
            // globle commit
            System.out.println("Current stage is "+ transferMessage.getStage());
            System.out.println("Receiving message from coordinator "+ transferMessage.getMsg());
            try {
                if(port==9002){
                    throw new Exception("停掉");
                }
                if(sqlConnection!=null){
                    this.sqlConnection.commit();
//                    this.sqlConnection.close();
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
                System.out.println("The execution of this transaction of shopping is completed");
            }

        }else if(transferMessage.getStage().getCode() == 5){
            // globle rollback阶段
            System.out.println("Current stage is "+ transferMessage.getStage());
            System.out.println("Receiving message from coordinator "+ transferMessage.getMsg());
            try {

                this.sqlConnection.rollback();
//                this.sqlConnection.close();
                // 返回执行结果并返回给协调者
                transferMessage.setMsg("This database rollback successfully");
                // 回复进入初始化阶段 等待重新整个服务
                transferMessage.setStage(Stage.INIT);

            } catch (SQLException throwables) {
                transferMessage.setMsg("This database rollback fails");
                transferMessage.setStage(Stage.ABORT);
                throwables.printStackTrace();
            }finally {
                SocketUtil.responseTransferMsg(out,transferMessage);
            }

        }
    }


}
