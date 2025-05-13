package app.persistence;

import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PartVariantMapper {

    private ConnectionPool connectionPool;

    public PartVariantMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Integer findVariantByLengthAndPart(int length, int part_id) throws DatabaseException {
        String sql = "SELECT part_variant_id FROM part_variant WHERE length=? AND part_id=?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, length);
            stmt.setInt(2, part_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getInt("part_variant_id");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer findLengthByVariantId(int variant_id) throws DatabaseException {
        String sql = "SELECT length from part_variant WHERE part_variant_id = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1,variant_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getInt("length");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
