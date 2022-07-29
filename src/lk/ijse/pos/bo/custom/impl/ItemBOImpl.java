package lk.ijse.pos.bo.custom.impl;

import lk.ijse.pos.bo.custom.ItemBO;
import lk.ijse.pos.dao.DAOFactory;
import lk.ijse.pos.dao.custom.ItemDAO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.entity.Item;

import java.sql.SQLException;
import java.util.ArrayList;

public class ItemBOImpl implements ItemBO {
    private final ItemDAO itemDAO = (ItemDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DAOTypes.ITEM);

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
    public String generateNewId() throws SQLException, ClassNotFoundException {
        return itemDAO.generateNewId();
    }

    @Override
    public boolean saveItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemDAO.save(new Item(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getQtyOnHand()));
    }

    @Override
    public boolean updateItem(ItemDTO dto) throws SQLException, ClassNotFoundException {
        return itemDAO.update(new Item(dto.getItemCode(), dto.getDescription(), dto.getPackSize(), dto.getUnitPrice(), dto.getQtyOnHand()));
    }

    @Override
    public boolean deleteItem(String id) throws SQLException, ClassNotFoundException {
        return itemDAO.delete(id);
    }

    @Override
    public boolean exitsItem(String id) throws SQLException, ClassNotFoundException {
        return itemDAO.exits(id);
    }
}
