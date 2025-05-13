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
                        rs.getInt("price")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Integer getPartIdByName(String partName) {
        String sql = "SELECT part_id from parts WHERE part_name = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, partName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getInt("part_id");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Part getPartByName(String partName) {
        String sql = "SELECT part_id, part_name, unit, price FROM parts WHERE part_name = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1,partName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return new Part(
                        rs.getInt("part_id"),
                        rs.getString("part_name"),
                        rs.getString("unit"),
                        rs.getInt("price")
                );
            }return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
