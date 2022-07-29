package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.OrderDetail;

import java.sql.SQLException;
import java.util.ArrayList;

public interface OrderDetailsDAO extends CrudDAO<OrderDetail,String> {

    boolean updateOrderDetails(OrderDetail dto) throws SQLException, ClassNotFoundException;

    boolean deleteOrderDetails(String orderId, String code) throws SQLException, ClassNotFoundException;

    ArrayList<OrderDetail> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException;
}
