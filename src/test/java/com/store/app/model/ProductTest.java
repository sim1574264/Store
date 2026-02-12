package com.store.app.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    @Test
    public void testDefaultConstructor() {
        Product product = new Product();
        assertEquals(0, product.getId());
        assertNull(product.getName());
        assertNull(product.getCategory());
        assertEquals(0.0, product.getPrice());
        assertNull(product.getImage());
    }

    @Test
    public void testParameterizedConstructor() {
        Product product = new Product(1, "Laptop", "Electronics", 999.99, "laptop.jpg");
        assertEquals(1, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(999.99, product.getPrice());
        assertEquals("laptop.jpg", product.getImage());
    }

    @Test
    public void testSettersAndGetters() {
        Product product = new Product();
        product.setId(2);
        product.setName("Phone");
        product.setCategory("Electronics");
        product.setPrice(599.99);
        product.setImage("phone.jpg");

        assertEquals(2, product.getId());
        assertEquals("Phone", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(599.99, product.getPrice());
        assertEquals("phone.jpg", product.getImage());
    }

    @Test
    public void testPriceWithDecimalValues() {
        Product product = new Product();
        product.setPrice(123.456);
        assertEquals(123.456, product.getPrice());
    }

    @Test
    public void testProductEquality() {
        Product product1 = new Product(1, "Test", "Cat", 10.0, "img.jpg");
        Product product2 = new Product(1, "Test", "Cat", 10.0, "img.jpg");
        assertEquals(product1.getId(), product2.getId());
        assertEquals(product1.getName(), product2.getName());
        assertEquals(product1.getCategory(), product2.getCategory());
        assertEquals(product1.getPrice(), product2.getPrice());
        assertEquals(product1.getImage(), product2.getImage());
    }
}
