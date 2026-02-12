package com.store.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    public void testDefaultConstructor() {
        Cart cart = new Cart();
        assertEquals(0, cart.getQuantity());
        assertEquals(0, cart.getId());
        assertNull(cart.getName());
        assertNull(cart.getCategory());
        assertEquals(0.0, cart.getPrice());
    }

    @Test
    public void testSetQuantity() {
        Cart cart = new Cart();
        cart.setQuantity(5);
        assertEquals(5, cart.getQuantity());
    }

    @Test
    public void testCartInheritsProductProperties() {
        Cart cart = new Cart();
        cart.setId(1);
        cart.setName("Tablet");
        cart.setCategory("Electronics");
        cart.setPrice(299.99);
        cart.setImage("tablet.jpg");
        cart.setQuantity(2);

        assertEquals(1, cart.getId());
        assertEquals("Tablet", cart.getName());
        assertEquals("Electronics", cart.getCategory());
        assertEquals(299.99, cart.getPrice());
        assertEquals("tablet.jpg", cart.getImage());
        assertEquals(2, cart.getQuantity());
    }

    @Test
    public void testQuantityBoundaries() {
        Cart cart = new Cart();
        cart.setQuantity(0);
        assertEquals(0, cart.getQuantity());

        cart.setQuantity(1000);
        assertEquals(1000, cart.getQuantity());

        cart.setQuantity(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, cart.getQuantity());
    }

    @Test
    public void testNegativeQuantity() {
        Cart cart = new Cart();
        cart.setQuantity(-5);
        assertEquals(-5, cart.getQuantity());
    }
}
