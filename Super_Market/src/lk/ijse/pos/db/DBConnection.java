package lk.ijse.pos.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static DBConnection dbConnection = null;
    private Connection connection;

    private DBConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://127.0.0.1:3306/SuperMarket",
                "hasii_boy",
                "Ashen11@"
        );
    }

    public static DBConnection getInstance() throws ClassNotFoundException, SQLException {
        if (dbConnection==null){
            dbConnection= new DBConnection();
        }
        return dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }
}