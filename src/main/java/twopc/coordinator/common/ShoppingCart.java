package twopc.coordinator.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    //库存数据库结构 商品名 库存数量
    //订单数据库结构 订单id(时间戳)  iphone  ipad  imac --》值为购买的数量  限制商品数简化
    //日志表记录 时间戳 操作
    private HashMap<String,Integer> cart;
    void buyIphone(Integer num){
        this.cart.put("iphone",num);
    }
    void buyIpad(Integer num){
        this.cart.put("ipad",num);
    }
    void buyImac(Integer num){
        this.cart.put("imac",num);
    }
}
