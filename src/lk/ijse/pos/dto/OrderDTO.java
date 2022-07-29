package lk.ijse.pos.dto;

import java.time.LocalDate;
import java.util.List;

public class OrderDTO {
    private String orderID;
    private LocalDate orderDate;
    private String cstID;
    List<OrderDetailsDTO> orderDetails;

    public OrderDTO() {
    }

    public OrderDTO(String orderID, LocalDate orderDate, String cstID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.cstID = cstID;
    }

    public OrderDTO(String orderID, LocalDate orderDate, String cstID, List<OrderDetailsDTO> orderDetails) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.cstID = cstID;
        this.orderDetails = orderDetails;
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

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderDetailsDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailsDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID='" + orderID + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", cstID='" + cstID + '\'' +
                '}';
    }
}
