package lk.ijse.pos.dao.custom.impl;


import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.entity.Customer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDAOImpl implements CustomerDAO {

    @Override
    public ArrayList<Customer> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<Customer> allCustomer = new ArrayList<>();
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Customer");
        while (rst.next()) {
            allCustomer.add(new Customer(rst.getString(1),rst.getString(2),rst.getString(3),rst.getString(4),rst.getString(5),rst.getString(6),rst.getString(7)));
        }
        return allCustomer;
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {

        ResultSet rst = SQLUtil.executeQuery("SELECT id FROM Customer ORDER BY id DESC LIMIT 1;");
        if (rst.next()) {
            String id = rst.getString("id");
            int newCustomerId = Integer.parseInt(id.replace("C-", "")) + 1;
            return String.format("C-%04d", newCustomerId);
        } else {
            return "C-0001";
        }
    }

    @Override
    public  boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Customer VALUES (?,?,?,?,?,?,?)", entity.getId(), entity.getTitle(), entity.getName(), entity.getAddress(), entity.getCity(), entity.getProvince(), entity.getPostalCode());
    }

    @Override
    public Customer search(String id) throws SQLException, ClassNotFoundException {
        Customer entity = new Customer();
        ResultSet rSt = SQLUtil.executeQuery("SELECT * FROM Customer WHERE id=?",id);
        while (rSt.next()) {
            entity = new Customer(rSt.getString(1),rSt.getString(2),rSt.getString(3),rSt.getString(4),rSt.getString(5),rSt.getString(6),rSt.getString(7));
        }
        return entity;
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Customer SET title=?, name=?, address=?, city=?,  province=?, postalCode=? WHERE id=?", entity.getTitle(), entity.getName(), entity.getAddress(), entity.getCity(), entity.getProvince(), entity.getPostalCode(), entity.getId());
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM Customer WHERE id=?", id);
    }

    @Override
    public boolean exits(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT id FROM Customer WHERE id=?", id).next();
    }
}
