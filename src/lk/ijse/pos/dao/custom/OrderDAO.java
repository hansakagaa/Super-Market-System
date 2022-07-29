package lk.ijse.pos.dao.custom;

import lk.ijse.pos.dao.CrudDAO;
import lk.ijse.pos.entity.Orders;

import java.sql.SQLException;
import java.util.ArrayList;

public interface OrderDAO extends CrudDAO<Orders,String> {

    boolean exitsCusId(String id) throws SQLException, ClassNotFoundException;

    ArrayList<Orders> getOrderFromCusId(String id) throws SQLException, ClassNotFoundException;

    ArrayList<Orders> getOrderFromDate(String startDate, String endDate) throws SQLException, ClassNotFoundException;

}
