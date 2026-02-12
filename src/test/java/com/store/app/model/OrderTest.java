package com.store.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void testDefaultConstructor() {
        Order order = new Order();
        assertEquals(0, order.getOrder_id());
        assertEquals(0, order.getUser_id());
        assertEquals(0, order.getQuantity());
        assertNull(order.getDate());
    }

    @Test
    public void testConstructorWithAllParameters() {
        Order order = new Order(1, 100, 5, "2024-01-15");
        assertEquals(1, order.getOrder_id());
        assertEquals(100, order.getUser_id());
        assertEquals(5, order.getQuantity());
        assertEquals("2024-01-15", order.getDate());
    }

    @Test
    public void testConstructorWithoutOrderId() {
        Order order = new Order(200, 3, "2024-02-20");
        assertEquals(0, order.getOrder_id());
        assertEquals(200, order.getUser_id());
        assertEquals(3, order.getQuantity());
        assertEquals("2024-02-20", order.getDate());
    }

    @Test
    public void testSettersAndGetters() {
        Order order = new Order();
        order.setOrder_id(10);
        order.setUser_id(500);
        order.setQuantity(15);
        order.setDate("2024-03-10");

        assertEquals(10, order.getOrder_id());
        assertEquals(500, order.getUser_id());
        assertEquals(15, order.getQuantity());
        assertEquals("2024-03-10", order.getDate());
    }

    @Test
    public void testToString() {
        Order order = new Order(1, 100, 5, "2024-01-15");
        String expected = "Order [order_id=1, user_id=100, quantity=5, date=2024-01-15]";
        assertEquals(expected, order.toString());
    }

    @Test
    public void testOrderInheritsProductProperties() {
        Order order = new Order();
        order.setId(50);
        order.setName("Headphones");
        order.setCategory("Accessories");
        order.setPrice(79.99);

        assertEquals(50, order.getId());
        assertEquals("Headphones", order.getName());
        assertEquals("Accessories", order.getCategory());
        assertEquals(79.99, order.getPrice());
    }

    @Test
    public void testDifferentDateFormats() {
        Order order = new Order();
        order.setDate("2024-12-31");
        assertEquals("2024-12-31", order.getDate());

        order.setDate("01-01-2024");
        assertEquals("01-01-2024", order.getDate());
    }
}
