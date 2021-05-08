package twopc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    //库存数据库结构 商品名 库存数量
    //订单数据库结构 订单id(时间戳)  iPhone  iPad  iMac --》值为购买的数量  限制商品数简化
    //日志表记录 时间戳 操作
    private HashMap<String,Integer> cart;
    public void buyiPhone(Integer num){
        this.cart.put("iPhone",num);
    }
    public void buyiPad(Integer num){
        this.cart.put("iPad",num);
    }
    public void buyiMac(Integer num){
        this.cart.put("iMac",num);
    }

    public HashMap<String, Integer> getCart() {
        return cart;
    }

    public void setCart(HashMap<String, Integer> cart) {
        this.cart = cart;
    }
}
