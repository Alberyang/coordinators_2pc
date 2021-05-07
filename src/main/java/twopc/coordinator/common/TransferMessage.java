package twopc.coordinator.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferMessage {
    private Stage stage;
    private ShoppingCart cart; // 购物车里的商品
    private String from; //表明传递双方的角色
    private String to;
    private String msg; // 传递消息


}
