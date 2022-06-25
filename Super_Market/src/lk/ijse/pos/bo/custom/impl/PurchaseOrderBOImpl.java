package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.PurchaseOrderBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailsDAO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailsDTO;
import lk.ijse.pos.entity.Customer;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.entity.OrderDetail;
import lk.ijse.pos.entity.Orders;

import java.sql.SQLException;
import java.util.ArrayList;

public class PurchaseOrderBOImpl implements PurchaseOrderBO {
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);
    private final OrderDetailsDAO orderDetailsDAO = (OrderDetailsDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAILS);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);

    @Override
    public boolean exitsCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.exits(id);
    }

    @Override
    public String getOrderId() throws SQLException, ClassNotFoundException {
        return orderDAO.generateNewId();
    }

    @Override
    public String getCustomerId() throws SQLException, ClassNotFoundException {
        return customerDAO.generateNewId();
    }

    @Override
    public boolean exitsItem(String id) throws SQLException, ClassNotFoundException {
        return itemDAO.exits(id);
    }

    @Override
    public ItemDTO searchItem(String id) throws SQLException, ClassNotFoundException {
        Item item = itemDAO.search(id);
        return new ItemDTO(item.getItemCode(),item.getDescription(),item.getPackSize(),item.getUnitPrice(),item.getQtyOnHand());
    }

    @Override
    public ArrayList<ItemDTO> getAllItem() throws SQLException, ClassNotFoundException {
        ArrayList<Item> items = itemDAO.getAll();
        ArrayList<ItemDTO> dtoS = new ArrayList<>();
        for (Item item : items) {
            dtoS.add(new ItemDTO(item.getItemCode(),item.getDescription(),item.getPackSize(),item.getUnitPrice(),item.getQtyOnHand()));
        }
        return dtoS;
    }

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer customer = customerDAO.search(id);
        return new CustomerDTO(customer.getId(),customer.getTitle(),customer.getName(),customer.getAddress(),customer.getCity(),customer.getProvince(),customer.getPostalCode());
    }

    @Override
    public boolean saveCustomer(CustomerDTO dto) throws SQLException, ClassNotFoundException {
        return customerDAO.save(new Customer(dto.getId(), dto.getTitle(), dto.getName(), dto.getAddress(), dto.getCity(), dto.getProvince(), dto.getPostalCode()));
    }

    @Override
    public ArrayList<CustomerDTO> getAllCustomer() throws SQLException, ClassNotFoundException {
        ArrayList<Customer> customers = customerDAO.getAll();
        ArrayList<CustomerDTO> dtoS = new ArrayList<>();
        for (Customer customer : customers) {
            dtoS.add(new CustomerDTO(customer.getId(),customer.getTitle(),customer.getName(),customer.getAddress(),customer.getCity(),customer.getProvince(),customer.getPostalCode()));
        }
        return dtoS;
    }

    @Override
    public boolean purchaseOrder(OrderDTO dto) throws SQLException, ClassNotFoundException {

        boolean saveOrder = orderDAO.save(new Orders(dto.getOrderID(), dto.getOrderDate(), dto.getCstID()));

        if (!saveOrder) {
            return false;
        }

        for (OrderDetailsDTO detail : dto.getOrderDetails()) {
            boolean saveOrderDetails = orderDetailsDAO.save(new OrderDetail(detail.getOrderID(), detail.getItemCode(), detail.getOrderQty(), detail.getDiscount()));
            if (!saveOrderDetails) {
                return false;
            }

            //Search & Update Item
            Item item = itemDAO.search(detail.getItemCode());
            item.setQtyOnHand(item.getQtyOnHand() - detail.getOrderQty());

            boolean update = itemDAO.update(item);

            if (!update) {
                return false;
            }
        }
        return true;
    }

}
