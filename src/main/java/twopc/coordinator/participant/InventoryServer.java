package twopc.coordinator.participant;

import com.alibaba.fastjson.JSONObject;
import twopc.coordinator.common.TransferMessage;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;

public class InventoryServer extends Server {


    public InventoryServer(Integer port) {
        this.setPort(port);
    }

    public static void main(String[] args) {

    }
}
