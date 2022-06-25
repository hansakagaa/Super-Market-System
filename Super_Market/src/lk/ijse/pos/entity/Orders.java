package lk.ijse.pos.entity;

import java.sql.Date;
import java.time.LocalDate;

public class Orders {
    private String orderID;
    private LocalDate orderDate;
    private String cstID;

    public Orders(String string, Date date, String rstString) {
    }

    public Orders(String orderID, LocalDate orderDate, String cstID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.cstID = cstID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getCstID() {
        return cstID;
    }

    public void setCstID(String cstID) {
        this.cstID = cstID;
    }
}
