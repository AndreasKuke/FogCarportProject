package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PartsListMapper {

    private ConnectionPool connectionPool;

    public PartsListMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void insertPartListItem(int order_id, int partVariant_id, int amount, String description) throws DatabaseException {
        String sql = "INSERT INTO parts_list (order_id, part_variant_id, amount, description) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, order_id);
            stmt.setInt(2, partVariant_id);
            stmt.setInt(3, amount);
            stmt.setString(4, description);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
