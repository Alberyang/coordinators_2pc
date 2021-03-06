package twopc.dao;

import twopc.common.LastStatus;
import twopc.common.TransferMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SqlServiceImpl implements SqlService {
    private final String sql_order_log_insert= "insert into orderLog(id,stage,_from,_to,content,msg,port) values(?,?,?,?,?,?,?)";
    private final String sql_inventory_log_insert= "insert into inventoryLog(id,stage,_from,_to,content,msg,port) values(?,?,?,?,?,?,?)";
    private final String sql_order_log_update = "update orderLog set stage=? where id=?";
    private final String sql_inventory_log_update = "update inventoryLog set stage=? where id=?";
    private final String sql_order_insert = "insert into `order`(id,iPhone,iPad,iMac) values(?,?,?,?)";
    private final String sql_inventory_update = "update inventory set inventoryNum=inventoryNum-? where item=? and inventoryNum>=?";
    private final String sql_order_delete = "delete from `order` where id = ?";
    private final String sql_inventory_restore = "update inventory set inventoryNum = inventoryNum + ? where item = ?";
    private Connection sqlConnection;
    private TransferMessage transferMessage;
    private HashMap<String, Integer> cart = null;
    private final String uuid;

    public SqlServiceImpl(Connection sqlConnection, TransferMessage transferMessage) {
        this.sqlConnection = sqlConnection;
        this.transferMessage = transferMessage;
        this.cart = transferMessage.getCart().getCart();
        this.uuid = UUID.randomUUID().toString();
    }
    /**
     * Insert the operation log to the database
     * @param port - identify the server by port
     */
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
    /**
     * Update the operation log to the database
     * @param port - identify the server by port
     */
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
    /**
     * Delete order by assigning the id
     * @param id - order id need to be assigned
     */
    @Override
    public void deleteOrder(String id) throws SQLException {
        PreparedStatement preparedStatement = sqlConnection.prepareStatement(this.sql_order_delete);
        preparedStatement.setString(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Restore the inventory data to make sure the data consistency
     * @param cart - The cart of last status
     */
    @Override
    public void restoreInventory(HashMap<String, Integer> cart) throws SQLException {
        PreparedStatement ps = sqlConnection.prepareStatement(this.sql_inventory_restore);
        ps.setInt(1,cart.get("iPhone"));
        ps.setString(2,"iPhone");
        ps.addBatch();
        ps.setInt(1, cart.get("iPad"));
        ps.setString(2, "iPad");
        ps.addBatch();
        ps.setInt(1, cart.get("iMac"));
        ps.setString(2, "iMac");
        ps.addBatch();
        ps.executeBatch();
    }

    /**
     * Insert the order item to the database
     * @param lastStatus - update the laststatus
     */
    @Override
    public void placeOrder(LastStatus lastStatus) throws SQLException {
        PreparedStatement preparedStatement = sqlConnection.prepareStatement(this.sql_order_insert);
        preparedStatement.setString(1, uuid);
        preparedStatement.setInt(2, cart.get("iPhone"));
        preparedStatement.setInt(3, cart.get("iPad"));
        preparedStatement.setInt(4, cart.get("iMac"));
        preparedStatement.executeUpdate();
        lastStatus.setLastOrderId(uuid);
    }

    /**
     * Decrease the inventory number corresponding to the item
     */
    @Override
    public int[] deleteInventory() throws SQLException {
        PreparedStatement ps = sqlConnection.prepareStatement(this.sql_inventory_update);
        ps.setInt(1, cart.get("iPhone"));
        ps.setString(2, "iPhone");
        ps.setInt(3, cart.get("iPhone"));
        ps.addBatch();
        ps.setInt(1, cart.get("iPad"));
        ps.setString(2, "iPad");
        ps.setInt(3, cart.get("iPad"));
        ps.addBatch();
        ps.setInt(1, cart.get("iMac"));
        ps.setString(2, "iMac");
        ps.setInt(3, cart.get("iMac"));
        ps.addBatch();
        return ps.executeBatch();


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
