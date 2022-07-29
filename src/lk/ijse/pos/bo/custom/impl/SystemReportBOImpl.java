package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.SystemReportBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dao.custom.OrderDAO;
import lk.ijse.pos.dao.custom.OrderDetailsDAO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailsDTO;
import lk.ijse.pos.entity.Item;
import lk.ijse.pos.entity.OrderDetail;
import lk.ijse.pos.entity.Orders;

import java.sql.SQLException;
import java.util.ArrayList;

public class SystemReportBOImpl implements SystemReportBO {

    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);
    private final OrderDetailsDAO orderDetailsDAO = (OrderDetailsDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDERDETAILS);
    private final OrderDAO orderDAO = (OrderDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ORDER);

    @Override
    public ArrayList<OrderDTO> getOrderFromDate(String startDate, String endDate) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> orderFromDate = orderDAO.getOrderFromDate(startDate, endDate);
        ArrayList<OrderDTO> dtoS = new ArrayList<>();
        for (Orders orders : orderFromDate) {
            dtoS.add(new OrderDTO(orders.getOrderID(),orders.getOrderDate(),orders.getCstID()));
        }
        return dtoS;
    }

    @Override
    public ArrayList<OrderDetailsDTO> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException {
        ArrayList<OrderDetail> orderDetails = orderDetailsDAO.getOrderDetails(orderId);
        ArrayList<OrderDetailsDTO> dtoS = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            dtoS.add(new OrderDetailsDTO(orderDetail.getOrderID(),orderDetail.getItemCode(),orderDetail.getOrderQTY(),orderDetail.getDiscount()));
        }
        return dtoS;
    }

    @Override
    public ItemDTO searchItem(String id) throws SQLException, ClassNotFoundException {
        Item item = itemDAO.search(id);
        return new ItemDTO(item.getItemCode(),item.getDescription(),item.getPackSize(),item.getUnitPrice(),item.getQtyOnHand());
    }
}
