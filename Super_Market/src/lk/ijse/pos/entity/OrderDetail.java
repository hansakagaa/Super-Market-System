package lk.ijse.pos.entity;

import java.math.BigDecimal;

public class OrderDetail {
    private String orderID;
    private String itemCode;
    private int OrderQTY;
    private BigDecimal discount;

    public OrderDetail() {
    }

    public OrderDetail(String orderID, String itemCode, int orderQTY, BigDecimal discount) {
        this.orderID = orderID;
        this.itemCode = itemCode;
        OrderQTY = orderQTY;
        this.discount = discount;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getOrderQTY() {
        return OrderQTY;
    }

    public void setOrderQTY(int orderQTY) {
        OrderQTY = orderQTY;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}
