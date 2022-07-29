package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface PurchaseOrderBO extends SuperBO {

    String getOrderId() throws SQLException, ClassNotFoundException;

    String getCustomerId() throws SQLException, ClassNotFoundException;

    boolean exitsItem(String id) throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String id) throws SQLException, ClassNotFoundException;

    ArrayList<ItemDTO> getAllItem() throws SQLException, ClassNotFoundException;

    boolean exitsCustomer(String id) throws SQLException, ClassNotFoundException;

    CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException;

    boolean saveCustomer(CustomerDTO dto) throws SQLException, ClassNotFoundException;

    ArrayList<CustomerDTO> getAllCustomer() throws SQLException, ClassNotFoundException;

    boolean purchaseOrder(OrderDTO dto) throws SQLException, ClassNotFoundException;

    }
