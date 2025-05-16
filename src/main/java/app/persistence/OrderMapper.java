package app.persistence;

import app.entities.Order;
import app.entities.User;
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
    private UserMapper userMapper;

    public  OrderMapper(ConnectionPool connectionPool) {

        this.connectionPool = connectionPool;
        this.userMapper = new UserMapper(connectionPool);
    }

    public Order getOrderById(int id) throws DatabaseException , SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getInt("carport_width"),
                        rs.getInt("carport_length"),
                        rs.getDate("date"),
                        rs.getBoolean("status"),
                        rs.getInt("price")
                );
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertOrder(Order order) throws DatabaseException {
        String sql = "INSERT into orders (carport_width, carport_length, date, status, user_id, price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, order.getCarport_width());
            stmt.setInt(2, order.getCarport_length());
            stmt.setDate(3, order.getDate());
            stmt.setBoolean(4,order.isStatus());
            stmt.setInt(5, order.getUser_ID());
            stmt.setInt(6, order.getPrice());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrderPrice(Order order) throws DatabaseException {
        String sql = "UPDATE orders SET price = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, order.getPrice());
            stmt.setInt(2, order.getOrder_ID());
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
                        int user_id = rs.getInt("user_id");
                        int order_id = rs.getInt("order_id");
                        Date date = rs.getDate("date");
                        int width = rs.getInt("carport_width");
                        int length = rs.getInt("carport_length");
                        boolean status = rs.getBoolean("status");
                        int price = rs.getInt("price");

                String userEmail = userMapper.getUserEmailByID(user_id);
                Order order = new Order(order_id, user_id, width,length, date, price, status);
                order.setUserMail(userEmail);
                orders.add(order);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return orders;
    }

    public int getNewestOrderID() throws DatabaseException {
        String sql = "SELECT MAX(order_id) FROM orders";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            if (rs.next()){
                return rs.getInt(1);
            }
            throw new DatabaseException("Could not find any newest order ID");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateOrderStatus(Order order){
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setBoolean(1,order.isStatus());
            stmt.setInt(2,order.getOrder_ID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
