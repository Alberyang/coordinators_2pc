package twopc.coordinator.handler;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class ShoppingHandler extends AbstractHandler {
    private static Logger log = Logger.getLogger(ShoppingHandler.class.getName());
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(4);

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        baseRequest.setHandled(true);


        if (target.equalsIgnoreCase("/transfer")) {
            if (pre_commit() && do_commit()){
                response.setStatus(HttpServletResponse.SC_OK);
                log.info("Transaction Process Completed.");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                log.info("Transaction Process Failed, Rollback to Previous");
            }

        }
    }

    /**
     * Pre-Commit Phase
     */
    private boolean pre_commit() {
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
     * Pre-Commit Phase
     */
    private boolean do_commit() {
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

}