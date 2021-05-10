package twopc.coordinator.handler;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import twopc.common.ShoppingCart;
import twopc.common.Stage;
import twopc.common.TransferMessage;
import twopc.coordinator.Coordinator;
import twopc.coordinator.CoordinatorServer;
import utils.SocketUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class ShoppingHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ShoppingHandler.class.getName());
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(4);
//    private Coordinator coordinator;

//    public ShoppingHandler(Coordinator coordinator){this.coordinator = coordinator}

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        // Http Servlet Settings
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        baseRequest.setHandled(true);

        if (target.equalsIgnoreCase("/shopping")) {
            // Processing HTTP Request
            String line;
            TransferMessage message;
            StringBuilder jsonString = new StringBuilder();
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONObject requestJson = JSONObject.parseObject(jsonString.toString());

            message = setTransferMessage(requestJson, Stage.VOTE_REQUEST, "Request Pre-Commit Voting");
            if (!preCommit(message)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("Pre-commit Process Failed, Rollback to Previous");
                CoordinatorServer.rollback(Stage.VOTE_ABORT, message);
                return;
            }

            message = setTransferMessage(requestJson, Stage.GLOBAL_COMMIT, "Request Global Commit");
            if (!doCommit(message)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("Do-commit Process Failed, Rollback to Previous");
                CoordinatorServer.rollback(Stage.GLOBAL_ABORT, message);
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            log.info("Transaction Process Completed.");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.info("Unknown type of operation");
        }
    }

    /**
     * Pre-Commit Phase
     */
    private boolean preCommit(TransferMessage message) {
        List<TransferMessage> responses = new ArrayList<>();
        try{
            // Send Request
            CoordinatorServer.commitRequest(message);

            CoordinatorServer.executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Wait for response
                        CoordinatorServer.participants.forEach((key, value) ->{
                            try {
                                BufferedReader in = SocketUtil.createInputStream(value);
                                if (value != null && in != null) {
                                    responses.add(SocketUtil.getResponse(in));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            cyclicBarrier.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            log.severe(e.toString());
            log.severe("System Pre-Commit Failed, Roll Back Operations");
            // Rollback Operation
            return false;
        }

        if (cyclicBarrier.getNumberWaiting() == 0) {
            for (TransferMessage response : responses) {
                if (response.getStage() != Stage.COMMIT_SUCCESS) {
                    log.warning("Participant:" + response.getPort() + " Pre-commit failed");
                    System.out.println(response.getMsg());
                    return false;
                }
            }
            // Pre-Commit Successful
            log.info("System Pre-Commit Done");
            return true;
        } else {
            // Pre-Commit Unsuccessful
            log.warning("System Pre-Commit Failed");
            return false;
        }
    }

    /**
     * Do-Commit Phase
     */
    private boolean doCommit(TransferMessage message) {
        List<TransferMessage> responses = new ArrayList<>();
        try{
            // Send Request
            CoordinatorServer.commitRequest(message);

            CoordinatorServer.executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Wait for response
                        CoordinatorServer.participants.forEach((key, value) ->{
                            try {
                                BufferedReader in = SocketUtil.createInputStream(value);
                                if (value != null && in != null) {
                                    responses.add(SocketUtil.getResponse(in));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            cyclicBarrier.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            log.severe("System Do-Commit Failed, Roll Back Operations");
            // Rollback Operation
            return false;
        }
        if (cyclicBarrier.getNumberWaiting() == 0) {
            for (TransferMessage response : responses) {
                if (response.getStage() != Stage.COMMIT_SUCCESS) {
                    log.warning("Participant:" + response.getPort() + " Do-commit failed");
                    System.out.println(response.getMsg());
                    return false;
                }
            }
            // Do-Commit Successful
            log.info("System Do-Commit Done");
            return true;
        } else {
            // Do-Commit Unsuccessful
            log.warning("System Do-Commit Failed");
            return false;
        }
    }

    /**
     * Generate transfer message
     */
    private TransferMessage setTransferMessage(JSONObject jsonData, Stage stage, String msg){
        TransferMessage message = new TransferMessage();

        // Process Shopping Cart
        ShoppingCart cart = new ShoppingCart();
        cart.setCart(new HashMap<String, Integer>());
        cart.buyiPhone(jsonData.getIntValue("iPhone"));
        cart.buyiMac(jsonData.getIntValue("iMac"));
        cart.buyiPad(jsonData.getIntValue("iPad"));

        // Set Transaction Message
        message.setStage(stage);
        message.setCart(cart);
        message.setFrom("Coordinator");
        message.setTo("Server");
        message.setMsg(msg);

        return message;
    }
}