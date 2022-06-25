package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDetailsDAO;
import lk.ijse.pos.entity.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDetailsDAOImpl implements OrderDetailsDAO {
    @Override
    public ArrayList<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO `Order Detail` VALUES (?,?,?,?)", entity.getOrderID(), entity.getItemCode(), entity.getOrderQTY(), entity.getDiscount());
    }

    @Override
    public OrderDetail search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM `Order Detail` WHERE orderID=?",id);
    }

    @Override
    public boolean exits(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean updateOrderDetails(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE `Order Detail` SET orderQTY=? WHERE orderID=? AND itemCode=?", entity.getOrderQTY(),entity.getOrderID(),entity.getItemCode());
    }

    @Override
    public boolean deleteOrderDetails(String orderId, String code) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM `Order Detail` WHERE orderID=? AND itemCode=?",orderId, code);
    }

    @Override
    public ArrayList<OrderDetail> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException {
        ArrayList<OrderDetail> entity = new ArrayList<>();
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM `Order Detail` WHERE orderID=?", orderId);
        while (rst.next()) {
            entity.add(new OrderDetail(rst.getString(1),rst.getString(2),rst.getInt(3), rst.getBigDecimal(4)));
        }
        return entity;
    }


}
