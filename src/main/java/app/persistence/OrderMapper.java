package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper {

    private final ConnectionPool connectionPool;

    public OrderMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Order getOrder(int id) throws DatabaseException , SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return new Order(
                        rs.getInt("order_id"),
                        rs.getDouble("date"),
                        rs.getInt("carport_height"),
                        rs.getInt("carport_width"),
                        rs.getInt("carport_length"),
                        rs.getBoolean("status")
                );
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
