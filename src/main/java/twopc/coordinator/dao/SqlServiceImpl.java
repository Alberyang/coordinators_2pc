package twopc.coordinator.dao;

import twopc.coordinator.common.TransferMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SqlServiceImpl implements SqlService {
    private final String sql_order_log_insert= "insert into orderLog(id,stage,from,to,content,msg,port) values(?,?,?,?,?,?,?)";
    private final String sql_inventory_log_insert= "insert into inventoryLog(id,stage,from,to,content,msg,port) values(?,?,?,?,?,?,?)";
    private final String sql_order_log_update = "update orderLog set stage=? where id=?)";
    private final String sql_inventory_log_update = "update inventoryLog set stage=? where id=?)";
    private final String sql_order_insert = "insert into order(id,iphone,ipad,imac) values(?,?,?,?)";
    private final String sql_inventory_update = "update inventory set inventoryNum=inventoryNum-? where item=?";
    private Connection sqlConnection;
    private TransferMessage transferMessage;
    private HashMap<String, Integer> cart;
    private final String uuid;

    public SqlServiceImpl(Connection sqlConnection, TransferMessage transferMessage) {
        this.sqlConnection = sqlConnection;
        this.transferMessage = transferMessage;
        this.cart = transferMessage.getCart().getCart();
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public void saveLog(Integer port) throws SQLException {
        String statement = "";
        if(port==9001) statement = sql_order_log_insert;
        if(port==9002) statement = sql_inventory_log_insert;
        PreparedStatement preparedStatement = sqlConnection.prepareStatement(statement);
        preparedStatement.setString(1, transferMessage.getId());
        preparedStatement.setString(2, transferMessage.getStage().name());
        preparedStatement.setString(3, transferMessage.getFrom());
        preparedStatement.setString(4, transferMessage.getTo());
        preparedStatement.setString(5, transferMessage.getCart().toString());
        preparedStatement.setString(6, transferMessage.getMsg());
        preparedStatement.setInt(7, transferMessage.getPort());
        preparedStatement.executeUpdate();
    }

    @Override
    public void updateLog(Integer port) throws SQLException {
        String statement = "";
        if(port==9001) statement = sql_order_log_update;
        if(port==9002) statement = sql_inventory_log_update;
        PreparedStatement preparedStatement = sqlConnection.prepareStatement(statement);
        preparedStatement.setString(1, transferMessage.getStage().name());
        preparedStatement.setString(2, transferMessage.getId());
        preparedStatement.executeUpdate();
    }

    @Override
    public void placeOrder() throws SQLException {
        PreparedStatement preparedStatement = sqlConnection.prepareStatement(this.sql_order_insert);
        preparedStatement.setString(1, uuid);
        preparedStatement.setInt(2, cart.get("iphone"));
        preparedStatement.setInt(3, cart.get("ipad"));
        preparedStatement.setInt(4, cart.get("imac"));
        preparedStatement.executeUpdate();
    }

    @Override
    public void delteInventory() throws SQLException {
        Integer iphoneNum = cart.get("iphone");
        PreparedStatement ps = sqlConnection.prepareStatement(this.sql_inventory_update);
        ps.setInt(1, cart.get("iphone"));
        ps.setString(2, "iphone");
        ps.addBatch();

        ps.setInt(1, cart.get("ipad"));
        ps.setString(2, "ipad");
        ps.addBatch();

        ps.setInt(1, cart.get("imac"));
        ps.setString(2, "imac");
        ps.addBatch();
        ps.executeUpdate();
    }

    public String getSql_order_insert() {
        return sql_order_insert;
    }

    public String getSql_inventory_update() {
        return sql_inventory_update;
    }

    public HashMap<String, Integer> getCart() {
        return cart;
    }

    public String getSql_order_log_insert() {
        return sql_order_log_insert;
    }

    public String getSql_inventory_log_insert() {
        return sql_inventory_log_insert;
    }

    public String getSql_order_log_update() {
        return sql_order_log_update;
    }

    public String getSql_inventory_log_update() {
        return sql_inventory_log_update;
    }

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public void setSqlConnection(Connection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    public TransferMessage getTransferMessage() {
        return transferMessage;
    }

    public void setTransferMessage(TransferMessage transferMessage) {
        this.transferMessage = transferMessage;
    }

}
