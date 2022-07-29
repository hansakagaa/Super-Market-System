package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.OrderDTO;
import lk.ijse.pos.dto.OrderDetailsDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface SystemReportBO extends SuperBO {

    ArrayList<OrderDTO> getOrderFromDate(String startDate, String endDate) throws SQLException, ClassNotFoundException;

    ArrayList<OrderDetailsDTO> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException;

    ItemDTO searchItem(String id) throws SQLException, ClassNotFoundException;


}
