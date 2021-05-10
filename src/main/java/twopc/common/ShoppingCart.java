package twopc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
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
