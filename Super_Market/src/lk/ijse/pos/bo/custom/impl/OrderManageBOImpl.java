package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.OrderManageBO;
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

public class OrderManageBOImpl implements OrderManageBO{
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDetailsDAO orderDetailsDAO = (OrderDetailsDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAILS);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);
    private final CustomerDAO customerDAO = (CustomerDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.CUSTOMER);

    @Override
    public boolean exitsCustomer(String id) throws SQLException, ClassNotFoundException {
        return customerDAO.exits(id);
    }

    @Override
    public boolean exitsCusId(String id) throws SQLException, ClassNotFoundException {
        return orderDAO.exitsCusId(id);
    }

    @Override
    public boolean deleteOrder(String id) throws SQLException, ClassNotFoundException {
        return orderDAO.delete(id);
    }

    @Override
    public boolean updateItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemDAO.update(new Item(dto.getItemCode(),dto.getDescription(),dto.getPackSize(),dto.getUnitPrice(),dto.getQtyOnHand()));
    }

    @Override
    public boolean updateOrderDetails(OrderDetailsDTO dto) throws SQLException, ClassNotFoundException {
        return orderDetailsDAO.updateOrderDetails(new OrderDetail(dto.getOrderID(),dto.getItemCode(),dto.getOrderQty(),dto.getDiscount()));
    }

    @Override
    public boolean deleteOrderDetails(String orderId, String code) throws SQLException, ClassNotFoundException {
        return orderDetailsDAO.deleteOrderDetails(orderId, code);
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
    public ItemDTO searchItem(String id) throws SQLException, ClassNotFoundException {
        Item item = itemDAO.search(id);
        return new ItemDTO(item.getItemCode(),item.getDescription(),item.getPackSize(),item.getUnitPrice(),item.getQtyOnHand());
    }

    @Override
    public CustomerDTO searchCustomer(String id) throws SQLException, ClassNotFoundException {
        Customer customer = customerDAO.search(id);
        return new CustomerDTO(customer.getId(),customer.getTitle(),customer.getName(),customer.getAddress(),customer.getCity(),customer.getProvince(),customer.getPostalCode());
    }

    @Override
    public ArrayList<OrderDTO> getOrderFromCusId(String id) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> orderFromCusId = orderDAO.getOrderFromCusId(id);
        ArrayList<OrderDTO> dtoS = new ArrayList<>();
        for (Orders orders : orderFromCusId) {
            dtoS.add(new OrderDTO(orders.getOrderID(),orders.getOrderDate(),orders.getCstID()));
        }
        return dtoS;
    }

    @Override
    public ArrayList<OrderDetailsDTO> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException {
        ArrayList<OrderDetail> orderDetails = orderDetailsDAO.getOrderDetails(orderId);
        ArrayList<OrderDetailsDTO> orderDetailsDTOS= new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            orderDetailsDTOS.add(new OrderDetailsDTO(orderDetail.getOrderID(),orderDetail.getItemCode(),orderDetail.getOrderQTY(),orderDetail.getDiscount()));
        }
        return orderDetailsDTOS;
    }
}
