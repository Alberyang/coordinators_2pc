package twopc.coordinator.dao;

import twopc.coordinator.common.TransferMessage;

import java.sql.Connection;

public interface SqlService {
    void prepare_order(Connection sqlConnection, TransferMessage transferMessage);
    void prepare_inventory(Connection sqlConnection, TransferMessage transferMessage);
}
