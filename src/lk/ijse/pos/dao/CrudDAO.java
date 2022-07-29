package lk.ijse.pos.dao;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CrudDAO<DTO,ID> extends SuperDAO{
    ArrayList<DTO> getAll() throws SQLException, ClassNotFoundException;

    boolean save(DTO dto) throws SQLException, ClassNotFoundException;

    DTO search(ID id) throws SQLException, ClassNotFoundException;

    boolean update(DTO dto) throws SQLException, ClassNotFoundException;

    boolean delete(ID id) throws SQLException, ClassNotFoundException;

    boolean exits(ID id) throws SQLException, ClassNotFoundException;

    String generateNewId() throws SQLException, ClassNotFoundException;
}
