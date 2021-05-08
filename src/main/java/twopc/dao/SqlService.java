package twopc.dao;

import java.sql.SQLException;

public interface SqlService {
    void saveLog(Integer port) throws SQLException;
    void updateLog(Integer port)throws SQLException;
    void placeOrder()throws SQLException;
    void deleteInventory()throws SQLException;
}
