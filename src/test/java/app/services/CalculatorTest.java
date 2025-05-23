package app.services;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import app.entities.Order;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
class CalculatorTest {

    Calculator calculator = new Calculator(connectionPool);

    private static final String USER = "postgres";
    private static final String PASSWORD = "pxo73qna";
    private static final String URL = "jdbc:postgresql://46.101.133.216:5432/%s?currentSchema=test";
    private static final String DB = "carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);

    @BeforeEach
    void setUp() {
        String sql = """
        DROP SCHEMA IF EXISTS test CASCADE;
        CREATE SCHEMA test;

        CREATE TYPE test.status AS ENUM ('pending', 'accepted', 'denied');

        CREATE TABLE IF NOT EXISTS test.users (
            user_id serial PRIMARY KEY,
            user_name varchar NOT NULL,
            user_password varchar NOT NULL,
            user_mail varchar NOT NULL,
            user_phonenumber varchar NOT NULL,
            user_role boolean NOT NULL
        );

        CREATE TABLE IF NOT EXISTS test.orders (
            order_id serial PRIMARY KEY,
            user_id integer NOT NULL,
            carport_width numeric NOT NULL,
            carport_length numeric NOT NULL,
            date date NOT NULL DEFAULT current_date,
            price integer,
            status test.status DEFAULT 'pending',
            CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id) REFERENCES test.users (user_id)
        );

        CREATE TABLE IF NOT EXISTS test.parts (
            part_id serial PRIMARY KEY,
            part_name varchar NOT NULL,
            unit varchar,
            price integer NOT NULL
        );

        CREATE TABLE IF NOT EXISTS test.part_variant (
            part_variant_id serial PRIMARY KEY,
            length integer NOT NULL,
            part_id integer NOT NULL,
            CONSTRAINT part_variant_part_id_fkey FOREIGN KEY (part_id) REFERENCES test.parts (part_id)
        );

        CREATE TABLE IF NOT EXISTS test.parts_list (
            parts_list_id serial PRIMARY KEY,
            order_id integer NOT NULL,
            part_variant_id integer NOT NULL,
            amount integer,
            description varchar,
            CONSTRAINT parts_list_order_id_fkey FOREIGN KEY (order_id) REFERENCES test.orders (order_id),
            CONSTRAINT parts_list_part_variant_id_fkey FOREIGN KEY (part_variant_id) REFERENCES test.part_variant (part_variant_id)
        );

        INSERT INTO test.users (user_name, user_password, user_mail, user_phonenumber, user_role) VALUES
        ('test', 'password', 'test@user.com', '12345678', false);

        INSERT INTO test.parts (part_id, part_name, unit, price) VALUES
            (1, '97x97 mm. trykimp. Stolpe', 'stk', 42),
            (2, '45x195 mm. spærtræ ubh.', 'stk', 62);

        INSERT INTO test.part_variant (part_variant_id, length, part_id) VALUES
            (1, 300, 1),
            (2, 240, 2),
            (3, 270, 2),
            (4, 300, 2),
            (5, 330, 2),
            (6, 360, 2),
            (7, 390, 2),
            (8, 420, 2),
            (9, 450, 2),
            (10, 480, 2),
            (11, 510, 2),
            (12, 540, 2),
            (13, 570, 2),
            (14, 600, 2);
        """;

        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error setting up test database: " + e.getMessage(), e);
        }
    }





    @AfterEach
    void tearDown() {
    }

    @Test
    void calcPoles750() throws DatabaseException {
        Order order = new Order(1,null,510,750,"pending");

        int actual = calculator.countPoles(order);

        assertEquals(6, actual);
        System.out.println(actual + " poles");
    }

    @Test
    void calcBeams750() throws DatabaseException {
        Order order = new Order(1,null,510,750,"pending");

        int beams = calculator.countBeams(order);
        int extraBeams = calculator.countExtraBeams(order);

        int actual = beams + extraBeams;

        assertEquals(3, actual);
        System.out.println(actual + " beams");


    }

    @Test
    void calcRafters750() throws DatabaseException {
        Order order = new Order(1,null,510,750,"pending");

        int actual = calculator.countRafters(order);

        assertEquals(14, actual);
        System.out.println(actual + " rafters");
    }

    @Test
    void calcPrice() throws DatabaseException {
        Order order = new Order(1,null,510,750,"pending");

        int actual = calculator.calcPrice(order);

        assertTrue(actual > 0);
        System.out.println(actual);
    }

    @Test
    void getUserByEmail() throws DatabaseException {
        UserMapper userMapper;
        userMapper = new UserMapper(connectionPool);
        User user = userMapper.getUserByEmail("test@user.com");

        assertNotNull(user);
        assertEquals("test",user.getUsername());
        assertEquals("test@user.com",user.getEmail());
    }
}