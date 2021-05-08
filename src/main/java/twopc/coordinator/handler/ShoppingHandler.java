package twopc.coordinator.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import twopc.coordinator.common.ShoppingCart;
import twopc.coordinator.common.Stage;
import twopc.coordinator.common.TransferMessage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class ShoppingHandler extends AbstractHandler {
    private static final Logger log = Logger.getLogger(ShoppingHandler.class.getName());
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(4);

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
            StringBuilder jsonString = new StringBuilder();
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONObject requestJson = JSONObject.parseObject(jsonString.toString());

            if (preCommit(requestJson) && doCommit(requestJson)){
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Transaction Process Completed.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                log.info("Transaction Process Failed, Rollback to Previous");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            log.info("Unknown type of operation");
        }
    }

    /**
     * Pre-Commit Phase
     */
    private boolean preCommit(JSONObject jsonData) {
        final String msg = "Request Pre-Commit Voting";
        TransferMessage message = setTransferMessage(jsonData, Stage.VOTE_REQUEST, msg);
        try{
            cyclicBarrier.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            log.severe("System Pre-Commit Failed, Roll Back Operations");
            // Rollback Operation
            return false;
        }

        if (cyclicBarrier.getNumberWaiting() == 0) {
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
    private boolean doCommit(JSONObject jsonData) {
        final String msg = "Request Global Commit";
        TransferMessage message = setTransferMessage(jsonData, Stage.GLOBAL_COMMIT, msg);
        try{
            cyclicBarrier.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | BrokenBarrierException | TimeoutException e) {
            log.severe("System Do-Commit Failed, Roll Back Operations");
            // Rollback Operation
            return false;
        }
        if (cyclicBarrier.getNumberWaiting() == 0) {
            // Pre-Commit Successful
            log.info("System Do-Commit Done");
            return true;
        } else {
            // Pre-Commit Unsuccessful
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
        cart.buyIphone(jsonData.getIntValue("Iphone"));
        cart.buyImac(jsonData.getIntValue("Imac"));
        cart.buyIpad(jsonData.getIntValue("Ipad"));

        // Set Transaction Message
        message.setStage(stage);
        message.setCart(cart);
        message.setFrom("Coordinator");
        message.setTo("Server");
        message.setMsg(msg);


        // Sent message to servers
//        message.setPort();
//
//        message.setPort(port);
        return message;
    }
}