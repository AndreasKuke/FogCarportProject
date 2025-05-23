package app.services;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import app.entities.Order;

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
}