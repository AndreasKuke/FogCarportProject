package app.persistence;

import app.entities.Order;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class OrderMapper {

    private final ConnectionPool connectionPool;

    public  OrderMapper(ConnectionPool connectionPool) {
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
                        rs.getDate("date"),
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

    public void insertOrder(Order order) throws DatabaseException {
        String sql = "INSERT into orders (carport_height, carport_width, carport_length, date, status, user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, order.getCarport_height());
            stmt.setInt(2, order.getCarport_width());
            stmt.setInt(3, order.getCarport_length());
            stmt.setDate(4, order.getDate());
            stmt.setBoolean(5,order.isStatus());
            stmt.setInt(6, order.getUser_ID());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrder(Order order) throws DatabaseException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> getAllOrders() throws DatabaseException {
        String sql = "SELECT * FROM orders";

        List<Order> orders = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("user_id"),
                        rs.getInt("order_id"),
                        rs.getDate("date"),
                        rs.getInt("carport_height"),
                        rs.getInt("carport_width"),
                        rs.getInt("carport_length"),
                        rs.getBoolean("status"),
                        rs.getInt("price"));

                orders.add(order);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }
}
