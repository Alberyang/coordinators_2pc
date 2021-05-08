package twopc.coordinator.dao;

import twopc.coordinator.common.TransferMessage;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlService {
    void saveLog(Integer port) throws SQLException;
    void updateLog(Integer port)throws SQLException;
    void placeOrder()throws SQLException;
    void deleteInventory()throws SQLException;
}
