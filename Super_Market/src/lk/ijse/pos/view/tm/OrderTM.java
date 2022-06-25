package lk.ijse.pos.view.tm;

public class OrderTM {
    private String orderID;
    private String cstID;
    private String orderDate;
    private String itemCode;
    private int qtyForSell;
    private double discount;

    public OrderTM() {
    }

    public OrderTM(String orderID) {
        this.orderID = orderID;
    }

    public OrderTM(String orderID, String cstID, String orderDate, String itemCode, int qtyForSell, double discount) {
        this.orderID = orderID;
        this.cstID = cstID;
        this.orderDate = orderDate;
        this.itemCode = itemCode;
        this.qtyForSell = qtyForSell;
        this.discount = discount;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getCstID() {
        return cstID;
    }

    public void setCstID(String cstID) {
        this.cstID = cstID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQtyForSell() {
        return qtyForSell;
    }

    public void setQtyForSell(int qtyForSell) {
        this.qtyForSell = qtyForSell;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "OrderTM{" +
                "orderID='" + orderID + '\'' +
                ", custID='" + cstID + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", qtyForSell=" + qtyForSell +
                ", discount=" + discount +
                '}';
    }
}
