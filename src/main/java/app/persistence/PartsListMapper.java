package app.persistence;

import app.entities.PartsList;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<PartsList> getPartList(int orderId) throws DatabaseException {
        List<PartsList> partsList = new ArrayList<>();
        String sql = "SELECT parts_list.order_id, parts_list.part_variant_id, parts_list.amount, parts_list.description, parts.part_name, part_variant.length " +
                "FROM parts_list JOIN part_variant ON parts_list.part_variant_id = part_variant.part_variant_id " +
                "JOIN parts ON part_variant.part_id = parts.part_id " +
                "WHERE parts_list.order_id = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                int partVariantId = rs.getInt("part_variant_id");
                int amount = rs.getInt("amount");
                String description = rs.getString("description");
                String partName = rs.getString("part_name");
                int length = rs.getInt("length");
                partsList.add(new PartsList(orderId,partVariantId,amount,description, partName, length));
            }
            return partsList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
