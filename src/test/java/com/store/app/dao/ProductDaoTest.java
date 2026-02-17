package com.store.app.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.store.app.model.Cart;
import com.store.app.model.Product;

public class ProductDaoTest {

    @Mock
    private Connection con;
    
    @Mock
    private PreparedStatement smt;
    
    @Mock
    private ResultSet rs;

    private ProductDao productDao;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        productDao = new ProductDao(con);
    }

    @Test
    public void testShowProductsReturnsEmptyListWhenNoData() throws SQLException {
        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<Product> products = productDao.showProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    public void testShowProductsReturnsListWithProducts() throws SQLException {
        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getInt("id")).thenReturn(1, 2);
        when(rs.getString("name")).thenReturn("Laptop", "Phone");
        when(rs.getString("category")).thenReturn("Electronics", "Electronics");
        when(rs.getDouble("price")).thenReturn(999.99, 599.99);
        when(rs.getString("image")).thenReturn("laptop.jpg", "phone.jpg");

        List<Product> products = productDao.showProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("Laptop", products.get(0).getName());
        assertEquals("Phone", products.get(1).getName());
    }

    @Test
    public void testGetSingleProductReturnsProduct() throws SQLException {
        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Laptop");
        when(rs.getString("category")).thenReturn("Electronics");
        when(rs.getDouble("price")).thenReturn(999.99);

        Product product = productDao.getSingleProduct(1);

        assertNotNull(product);
        assertEquals(1, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertEquals(999.99, product.getPrice());
    }

    @Test
    public void testGetSingleProductReturnsNullWhenNotFound() throws SQLException {
        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        Product product = productDao.getSingleProduct(999);

        assertNull(product);
    }

    @Test
    public void testGetCartProductReturnsEmptyListWhenCartIsEmpty() {
        ArrayList<Cart> cartList = new ArrayList<>();
        
        List<Cart> result = productDao.getCartProduct(cartList);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetCartProductReturnsProductsFromCart() throws SQLException {
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setId(1);
        cart.setQuantity(2);
        cartList.add(cart);

        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("Laptop");
        when(rs.getString("category")).thenReturn("Electronics");
        when(rs.getDouble("price")).thenReturn(500.0);

        List<Cart> result = productDao.getCartProduct(cartList);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    public void testTotalPriceReturnsZeroWhenCartIsEmpty() {
        ArrayList<Cart> cartList = new ArrayList<>();
        
        double total = productDao.totalPrice(cartList);
        
        assertEquals(0.0, total, 0.001);
    }

    @Test
    public void testTotalPriceCalculatesCorrectly() throws SQLException {
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setId(1);
        cart.setQuantity(2);
        cartList.add(cart);

        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getDouble("price")).thenReturn(100.0);

        double total = productDao.totalPrice(cartList);

        assertEquals(200.0, total, 0.001);
    }

    @Test
    public void testTotalPriceWithMultipleItems() throws SQLException {
        ArrayList<Cart> cartList = new ArrayList<>();
        
        Cart cart1 = new Cart();
        cart1.setId(1);
        cart1.setQuantity(2);
        cartList.add(cart1);
        
        Cart cart2 = new Cart();
        cart2.setId(2);
        cart2.setQuantity(3);
        cartList.add(cart2);

        when(con.prepareStatement(anyString())).thenReturn(smt);
        when(smt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true);
        when(rs.getDouble("price")).thenReturn(100.0, 50.0);

        double total = productDao.totalPrice(cartList);

        assertEquals(350.0, total, 0.001);
    }
}
