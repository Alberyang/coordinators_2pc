package twopc.coordinator.participant;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import twopc.coordinator.common.TransferMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;

@Data
public class OrderServer extends Server{


    public OrderServer(Connection connection) {
        super(connection);
        this.setPort(9001);
    }

    public static void main(String[] args) {

    }
}
