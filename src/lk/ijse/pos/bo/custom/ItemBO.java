package lk.ijse.pos.bo.custom;

import lk.ijse.pos.bo.SuperBO;
import lk.ijse.pos.dto.ItemDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ItemBO extends SuperBO {

    ArrayList<ItemDTO> getAllItem() throws SQLException, ClassNotFoundException;

    String generateNewId() throws SQLException, ClassNotFoundException;

    boolean saveItem(ItemDTO dto) throws SQLException, ClassNotFoundException;

    boolean updateItem(ItemDTO dto) throws SQLException, ClassNotFoundException;

    boolean deleteItem(String id) throws SQLException, ClassNotFoundException;

    boolean exitsItem(String id) throws SQLException, ClassNotFoundException ;
}
