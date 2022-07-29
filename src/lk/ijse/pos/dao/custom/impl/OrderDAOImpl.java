package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.entity.Orders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public ArrayList<Orders> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT orderID FROM Orders ORDER BY orderID DESC LIMIT 1;");
        return rst.next() ? String.format("OI-%03d", (Integer.parseInt(rst.getString("orderID").replace("OI-", "")) + 1)) : "OI-001";
    }

    @Override
    public boolean save(Orders entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Orders VALUES (?,?,?)", entity.getOrderID(), entity.getOrderDate(), entity.getCstID());
    }

    @Override
    public Orders search(String id) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean update(Orders entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean exits(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT * FROM Orders WHERE orderID=?",id).next();
    }

    @Override
    public boolean exitsCusId(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT * FROM Orders WHERE cstID=?",id).next();
    }

    @Override
    public ArrayList<Orders> getOrderFromCusId(String id) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> entity = new ArrayList<>();
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Orders WHERE cstID=?", id);
        while (rst.next()) {
            entity.add(new Orders(rst.getString(1), rst.getDate(2), rst.getString(3)));
        }
        return entity;
    }

    @Override
    public ArrayList<Orders> getOrderFromDate(String startDate, String endDate) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> entity = new ArrayList<>();
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Orders WHERE orderDate BETWEEN ? AND ?", startDate, endDate);
        while (rst.next()){
            entity.add(new Orders(rst.getString("orderID"), rst.getDate("orderDate"), rst.getString("cstID")));
        }
        return entity;
    }
}
