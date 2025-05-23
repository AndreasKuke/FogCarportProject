package app.persistence;

import app.entities.User;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    private ConnectionPool connectionPool;

    public UserMapper(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public User getUserByID(int id){
        String sql = "select * from users where user_id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getString("user_mail"),
                        rs.getString("user_phonenumber"),
                        rs.getBoolean("user_role")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public User getUserByEmail (String email){
        String sql = "SELECT * FROM users WHERE user_mail = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getString("user_mail"),
                        rs.getString("user_phonenumber"),
                        rs.getBoolean("user_role")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }return null;
    }


    public void createUser(String email, String password, String phoneNumber, String name){
        String sql = "INSERT INTO users (user_name, user_password, user_mail, user_phonenumber, user_role) " +
                "VALUES (?, ?, ?, ?, false)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            System.out.println("HELLO");

            stmt.setString(1, name);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, phoneNumber);

            stmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserPasswordFromDB(String email){
        String sql = "SELECT user_password FROM users WHERE user_mail = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return rs.getString("user_password");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserEmailByID(int user_id){
        String sql = "SELECT user_mail from users WHERE user_id = ?";

        try (Connection conn = connectionPool.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                return rs.getString("user_mail");
            }else {
                throw new DatabaseException("No user with id:" + user_id);
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> userList = new ArrayList<>();

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getString("user_mail"),
                        rs.getString("user_phonenumber"),
                        rs.getBoolean("user_role")
                );
                userList.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return userList;
    }

}
