package app.persistence;

import app.entities.Part;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PartMapper {

    private final ConnectionPool connectionPool;

    public PartMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Part getPartByID(int id) {
        String sql = "select * from parts where part_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return new Part(
                        rs.getInt("part_id"),
                        rs.getString("part_name"),
                        rs.getString("part_unit"),
                        rs.getInt("part_amount"),
                        rs.getInt("part_length"),
                        rs.getDouble("price")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
