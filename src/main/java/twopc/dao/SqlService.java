package twopc.dao;

import twopc.common.LastStatus;

import java.sql.SQLException;
import java.util.HashMap;

public interface SqlService {
    void saveLog(Integer port) throws SQLException;
    void updateLog(Integer port)throws SQLException;
    void deleteOrder(String id) throws SQLException;
    void restoreInventory(HashMap<String,Integer> cart) throws SQLException;
    void placeOrder(LastStatus lastStatus)throws SQLException;
    int[] deleteInventory()throws SQLException;
}
