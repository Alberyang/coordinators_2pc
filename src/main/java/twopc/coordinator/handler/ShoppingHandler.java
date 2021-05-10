package twopc.coordinator.handler;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import twopc.common.ShoppingCart;
import twopc.common.Stage;
import twopc.common.TransferMessage;
import twopc.coordinator.CoordinatorServer;
import utils.SocketUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ShoppingHandler extends AbstractHandler {
    private static final Lock lock = new ReentrantLock();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException {
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

            System.out.println("\n------------------------Transaction Start------------------------");
            message = setTransferMessage(requestJson, Stage.VOTE_REQUEST, "Request Pre-Commit Voting");
            if (!preCommit(message)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                System.out.println("Pre-commit Process Failed, Rollback to Previous");
                CoordinatorServer.rollback(Stage.GLOBAL_ABORT, message);
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
            System.out.println("Transaction Process Completed.");
            System.out.println("-------------------------------------------------------------------");
        } else if (target.equalsIgnoreCase("/no_2pc")) {
            // Processing HTTP Request
            String line;
            TransferMessage message;
            StringBuilder jsonString = new StringBuilder();
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONObject requestJson = JSONObject.parseObject(jsonString.toString());

            System.out.println("\n------------------------Transaction Start------------------------");
            message = setTransferMessage(requestJson, Stage.INIT, "Simple commit");
            simpleCommit(message);
            System.out.println("Transaction Process Completed.");
            System.out.println("-------------------------------------------------------------------");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            System.out.println("Unknown type of operation");
        }
    }

    /**
     * Pre-Commit Phase
     * @param message - transaction needed to be process
     */
    private boolean preCommit(TransferMessage message) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        List<TransferMessage> responses = new ArrayList<>();
        try{
            // Send Request
            CoordinatorServer.commitRequest(message);
            // Wait for response
            for (Map.Entry<Integer, Socket> entry : CoordinatorServer.participants.entrySet()) {
               new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            // Concurrent control
                            lock.lock();
                            // Receive responses
                            BufferedReader in = SocketUtil.createInputStream(entry.getValue());
                            if (entry.getValue() != null && in != null) {
                                TransferMessage msg = SocketUtil.getResponse(in);
                                if (msg != null) {
                                    responses.add(msg);
                                    System.out.println("This node received the message " + msg);
                                } else {
                                    System.out.println("The message this node received can not be identified");
                                }
                            }
                            // Concurrent control
                            lock.unlock();
                            cyclicBarrier.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
            System.out.println("System Pre-Commit Failed, Roll Back Operations");
            return false;
        }

        if (cyclicBarrier.getNumberWaiting() == 0) {
            for (TransferMessage response : responses) {
                if (response.getStage() != Stage.VOTE_COMMIT) {
                    System.out.println("Participant:" + response.getPort() + " Pre-commit failed");
                    System.out.println(response.getMsg());
                    return false;
                }
            }
            // Pre-Commit Successful
            if (!responses.isEmpty()) {
                System.out.println("System Pre-Commit Done");
                return true;
            }
        } else {
            // Pre-Commit Unsuccessful
            System.out.println("System Pre-Commit Failed: response timeout");
        }
        System.out.println("System Pre-Commit Failed");
        return false;
    }

    /**
     * Do-Commit Phase
     * @param message - transaction needed to be process
     */
    private boolean doCommit(TransferMessage message) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
        List<TransferMessage> responses = new ArrayList<>();
        try{
            // Send Request
            CoordinatorServer.commitRequest(message);
            // Wait for response
            for (Map.Entry<Integer, Socket> entry : CoordinatorServer.participants.entrySet()) {
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            // Concurrent control
                            lock.lock();
                            // Receive response
                            BufferedReader in = SocketUtil.createInputStream(entry.getValue());
                            if (entry.getValue() != null && in != null) {
                                TransferMessage msg = SocketUtil.getResponse(in);
                                if (msg != null) {
                                    responses.add(msg);
                                    System.out.println("This node received the message " + msg);
                                } else {
                                    System.out.println("The message this node received can not be identified");
                                }
                            }
                            // Concurrent control
                            lock.unlock();
                            cyclicBarrier.await();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
            System.out.println("System Do-Commit Failed, Roll Back Operations");
            return false;
        }
        if (cyclicBarrier.getNumberWaiting() == 0) {
            for (TransferMessage response : responses) {
                if (response.getStage() != Stage.COMMIT_SUCCESS) {
                    System.out.println("Participant:" + response.getPort() + " Do-commit failed");
                    System.out.println(response.getMsg());
                    return false;
                }
            }
            // Do-Commit Successful
            if (!responses.isEmpty()) {
                System.out.println("System Do-Commit Done");
                return true;
            }
        } else {
            System.out.println("System Do-Commit Failed: response timeout");
        }
        // Do-Commit Unsuccessful
        System.out.println("System Do-Commit Failed");
        return false;
    }

    /**
     * A simple commit function of transaction
     * @param message - transaction needed to be process
     */
    private void simpleCommit(TransferMessage message){
        // Send Request
        message.setStage(Stage.VOTE_REQUEST);
        CoordinatorServer.commitRequest(message);

        // Wait a bit
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Transaction Commit Failed");
        }

        // Send Request
        message.setStage(Stage.GLOBAL_COMMIT);
        CoordinatorServer.commitRequest(message);
    }

    /**
     * Generate transfer message
     * @param jsonData - json data from http request about the transaction
     * @param msg - text message needed to be sent to the participants
     * @param stage - current commit stage
     */
    private TransferMessage setTransferMessage(JSONObject jsonData, Stage stage, String msg){
        TransferMessage message = new TransferMessage();

        // Process Shopping Cart
        ShoppingCart cart = new ShoppingCart();
        cart.setCart(new HashMap<>());
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