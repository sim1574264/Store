package com.store.app.dao;

import com.store.app.model.Cart;
import com.store.app.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductDaoTest {

    private Connection con;
    private PreparedStatement ps;
    private ResultSet rs;
    private ProductDao dao;

    @BeforeEach
    void setup() {
        con = mock(Connection.class);
        ps  = mock(PreparedStatement.class);
        rs  = mock(ResultSet.class);
        dao = new ProductDao(con);
    }

    @Test
    void showProducts_shouldReturnListOfProducts() throws Exception {
        // given
        when(con.prepareStatement("select * from products")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // 2 rows
        when(rs.next()).thenReturn(true, true, false);

        when(rs.getInt("id")).thenReturn(1, 2);
        when(rs.getString("name")).thenReturn("P1", "P2");
        when(rs.getString("category")).thenReturn("C1", "C2");
        when(rs.getDouble("price")).thenReturn(10.0, 20.0);
        when(rs.getString("image")).thenReturn("img1.png", "img2.png");

        // when
        List<Product> products = dao.showProducts();

        // then
        assertEquals(2, products.size());
        assertEquals(1, products.get(0).getId());
        assertEquals("P1", products.get(0).getName());
        assertEquals(10.0, products.get(0).getPrice());

        assertEquals(2, products.get(1).getId());
        assertEquals("P2", products.get(1).getName());
        assertEquals(20.0, products.get(1).getPrice());

        verify(con).prepareStatement("select * from products");
        verify(ps).executeQuery();
    }

    @Test
    void getSingleProduct_whenIdInvalid_shouldReturnNull_andNotHitDb() throws Exception {
        // when
        Product p0 = dao.getSingleProduct(0);
        Product pNeg = dao.getSingleProduct(-1);

        // then
        assertNull(p0);
        assertNull(pNeg);
        verifyNoInteractions(con);
    }

    @Test
    void getSingleProduct_whenFound_shouldReturnProduct() throws Exception {
        // given
        String sql = "SELECT id, name, category, price FROM products WHERE id = ?";
        when(con.prepareStatement(sql)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        when(rs.getInt("id")).thenReturn(5);
        when(rs.getString("name")).thenReturn("Laptop");
        when(rs.getString("category")).thenReturn("Electronics");
        when(rs.getDouble("price")).thenReturn(999.99);

        // when
        Product p = dao.getSingleProduct(5);

        // then
        assertNotNull(p);
        assertEquals(5, p.getId());
        assertEquals("Laptop", p.getName());
        assertEquals("Electronics", p.getCategory());
        assertEquals(999.99, p.getPrice());

        verify(ps).setInt(1, 5);
        verify(ps).executeQuery();
    }

    @Test
    void getSingleProduct_whenNotFound_shouldReturnNull() throws Exception {
        // given
        String sql = "SELECT id, name, category, price FROM products WHERE id = ?";
        when(con.prepareStatement(sql)).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        // when
        Product p = dao.getSingleProduct(99);

        // then
        assertNull(p);
        verify(ps).setInt(1, 99);
        verify(ps).executeQuery();
    }

    @Test
    void getCartProduct_whenCartEmpty_shouldReturnEmpty_andNotHitDb() {
        // given
        ArrayList<Cart> cart = new ArrayList<>();

        // when
        List<Cart> out = dao.getCartProduct(cart);

        // then
        assertNotNull(out);
        assertTrue(out.isEmpty());
        verifyNoInteractions(con);
    }

    @Test
    void getCartProduct_whenOneItem_shouldReturnCartRowWithComputedPrice() throws Exception {
        // given
        ArrayList<Cart> cart = new ArrayList<>();
        Cart item = new Cart();
        item.setId(3);
        item.setQuantity(2);
        cart.add(item);

        when(con.prepareStatement("select * from products where id=?")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id")).thenReturn(3);
        when(rs.getString("name")).thenReturn("Mouse");
        when(rs.getString("category")).thenReturn("Accessories");
        when(rs.getDouble("price")).thenReturn(50.0);

        // when
        List<Cart> out = dao.getCartProduct(cart);

        // then
        assertEquals(1, out.size());
        Cart row = out.get(0);
        assertEquals(3, row.getId());
        assertEquals("Mouse", row.getName());
        assertEquals("Accessories", row.getCategory());
        assertEquals(2, row.getQuantity());
        assertEquals(100.0, row.getPrice()); // 50 * 2

        verify(ps).setInt(1, 3);
        verify(ps).executeQuery();
    }

    @Test
    void totalPrice_shouldSumPricesTimesQuantities() throws Exception {
        // given
        ArrayList<Cart> cart = new ArrayList<>();
        Cart i1 = new Cart(); i1.setId(1); i1.setQuantity(2);
        Cart i2 = new Cart(); i2.setId(2); i2.setQuantity(3);
        cart.add(i1);
        cart.add(i2);

        when(con.prepareStatement("select price from products where id=?")).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // 1er item: 1 row (price=10)
        // 2e item: 1 row (price=20)
        when(rs.next()).thenReturn(true, false, true, false);
        when(rs.getDouble("price")).thenReturn(10.0, 20.0);

        // when
        double total = dao.totalPrice(cart);

        // then
        // 10*2 + 20*3 = 20 + 60 = 80
        assertEquals(80.0, total, 0.0001);

        // setInt appelé pour chaque item
        verify(ps).setInt(1, 1);
        verify(ps).setInt(1, 2);
        verify(ps, times(2)).executeQuery();
    }
}
