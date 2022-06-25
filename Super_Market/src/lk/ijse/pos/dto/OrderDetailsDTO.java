package lk.ijse.pos.dto;

import java.math.BigDecimal;

public class OrderDetailsDTO {
    private String orderID;
    private String itemCode;
    private int OrderQty;
    private BigDecimal discount;

    public OrderDetailsDTO() {
    }

    public OrderDetailsDTO(String orderID, String itemCode, int orderQty, BigDecimal discount) {
        this.orderID = orderID;
        this.itemCode = itemCode;
        this.OrderQty = orderQty;
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

    public int getOrderQty() {
        return OrderQty;
    }

    public void setOrderQty(int orderQty) {
        OrderQty = orderQty;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderID='" + orderID + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", OrderQty=" + OrderQty +
                ", discount=" + discount +
                '}';
    }
}