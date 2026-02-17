package com.store.app.servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.store.app.connection.dbConnection;
import com.store.app.dao.OrderDao;
import com.store.app.model.Cart;
import com.store.app.model.Order;
import com.store.app.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class OpsServletTest {

    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private PrintWriter writer;
    
    @Mock
    private Connection connection;
    
    @Mock
    private OrderDao orderDao;

    private OpsServlet opsServlet;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        opsServlet = new OpsServlet();
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGetWithIncAction() throws Exception {
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setId(1);
        cart.setQuantity(1);
        cartList.add(cart);

        when(request.getParameter("action")).thenReturn("inc");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(cartList);

        opsServlet.doGet(request, response);

        verify(response).sendRedirect("cart.jsp");
        assertEquals(2, cart.getQuantity());
    }

    @Test
    public void testDoGetWithDecAction() throws Exception {
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setId(1);
        cart.setQuantity(3);
        cartList.add(cart);

        when(request.getParameter("action")).thenReturn("dec");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(cartList);

        opsServlet.doGet(request, response);

        verify(response).sendRedirect("cart.jsp");
        assertEquals(2, cart.getQuantity());
    }

    @Test
    public void testDoGetWithRemoveAction() throws Exception {
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setId(1);
        cart.setQuantity(1);
        cartList.add(cart);

        when(request.getParameter("action")).thenReturn("remove");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(cartList);

        opsServlet.doGet(request, response);

        verify(response).sendRedirect("cart.jsp");
        assertTrue(cartList.isEmpty());
    }

    @Test
    public void testDoGetWithNullAction() throws Exception {
        when(request.getParameter("action")).thenReturn(null);
        when(request.getParameter("id")).thenReturn("1");

        opsServlet.doGet(request, response);

        verify(response).sendRedirect("cart.jsp");
    }

    @Test
    public void testDoGetWithInvalidId() throws Exception {
        when(request.getParameter("action")).thenReturn("inc");
        when(request.getParameter("id")).thenReturn("0");

        opsServlet.doGet(request, response);

        verify(response).sendRedirect("cart.jsp");
    }

    @Test
    public void testDoPostWithAuthenticatedUser() throws Exception {
        User user = new User();
        user.setId(1);
        
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(user);
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("quantity")).thenReturn("2");
        
        when(dbConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(mock(java.sql.PreparedStatement.class));
        
        // Create a mock OrderDao that returns true
        OrderDao mockOrderDao = mock(OrderDao.class);
        when(mockOrderDao.submitOrder(any(Order.class))).thenReturn(true);
        
        opsServlet.doPost(request, response);

        verify(response).sendRedirect("orders.jsp");
    }

    @Test
    public void testDoPostWithUnauthenticatedUser() throws Exception {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(null);

        opsServlet.doPost(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    public void testDoPostWithZeroQuantity() throws Exception {
        User user = new User();
        user.setId(1);
        
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(user);
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("quantity")).thenReturn("0");
        
        when(dbConnection.getConnection()).thenReturn(connection);
        
        opsServlet.doPost(request, response);

        // Should redirect to orders.jsp even with quantity set to 1 (default)
        // The servlet sets productQ to 1 if it's <= 0
        verify(response).sendRedirect("orders.jsp");
    }

    @Test
    public void testDoPostWithNegativeQuantity() throws Exception {
        User user = new User();
        user.setId(1);
        
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(user);
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("quantity")).thenReturn("-5");
        
        when(dbConnection.getConnection()).thenReturn(connection);
        
        opsServlet.doPost(request, response);

        // Should redirect to orders.jsp even with negative quantity (becomes 1)
        verify(response).sendRedirect("orders.jsp");
    }

    @Test
    public void testDoGetWithPostAction() throws Exception {
        // When action is "post", it calls doPost
        when(request.getParameter("action")).thenReturn("post");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("quantity")).thenReturn("2");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(null);

        opsServlet.doGet(request, response);

        verify(response).sendRedirect("login.jsp");
    }

    @Test
    public void testDoGetWithMultipleCartItems() throws Exception {
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart cart1 = new Cart();
        cart1.setId(1);
        cart1.setQuantity(1);
        cartList.add(cart1);
        
        Cart cart2 = new Cart();
        cart2.setId(2);
        cart2.setQuantity(1);
        cartList.add(cart2);

        when(request.getParameter("action")).thenReturn("inc");
        when(request.getParameter("id")).thenReturn("1");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(cartList);

        opsServlet.doGet(request, response);

        // Only the matching item should be incremented
        assertEquals(2, cart1.getQuantity());
        assertEquals(1, cart2.getQuantity());
    }
}
