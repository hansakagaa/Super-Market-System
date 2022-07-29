package lk.ijse.pos.dao.custom.impl;

import lk.ijse.pos.dao.SQLUtil;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.entity.Item;


import java.sql.*;
import java.util.ArrayList;

public class ItemDAOImpl implements ItemDAO {

    @Override
    public ArrayList<Item> getAll() throws SQLException, ClassNotFoundException {
        ArrayList<Item> allItem = new ArrayList<>();
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Item");
        while (rst.next()) {
            allItem.add(new Item(rst.getString(1),rst.getString(2),rst.getString(3), rst.getBigDecimal(4),rst.getInt(5)));
        }
        return allItem;
    }

    @Override
    public String generateNewId() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.executeQuery("SELECT itemCode FROM Item ORDER BY itemCode DESC LIMIT 1;");
        if (rst.next()) {
            String id = rst.getString("ItemCode");
            int newItemId = Integer.parseInt(id.replace("I-", "")) + 1;
            return String.format("I-%04d", newItemId);
        } else {
            return "I-0001";
        }
    }

    @Override
    public  boolean save(Item entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("INSERT INTO Item VALUES (?,?,?,?,?)", entity.getItemCode(), entity.getDescription(), entity.getPackSize(), entity.getUnitPrice(), entity.getQtyOnHand());
    }

    @Override
    public Item search(String id) throws SQLException, ClassNotFoundException {
        Item entity = new Item();
        ResultSet rst = SQLUtil.executeQuery("SELECT * FROM Item WHERE itemCode=?", id);
        while (rst.next()) {
            entity = new Item(rst.getString(1),rst.getString(2),rst.getString(3), rst.getBigDecimal(4),rst.getInt(5));
        }
        return entity;
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("UPDATE Item SET description=?, packSize=?, unitPrice=?, qtyOnHand=? WHERE itemCode=?", entity.getDescription(), entity.getPackSize(), entity.getUnitPrice(), entity.getQtyOnHand(), entity.getItemCode());
    }

    @Override
    public boolean delete(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeUpdate("DELETE FROM Item WHERE itemCode=?", id);
    }

    @Override
    public boolean exits(String id) throws SQLException, ClassNotFoundException {
        return SQLUtil.executeQuery("SELECT itemCode FROM Item WHERE itemCode=?", id).next();
    }
}
