package twopc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastStatus {
    private Stage lastStage;
    private String lastOrderId;
    private HashMap<String,Integer> lastSQLOperation;
}
