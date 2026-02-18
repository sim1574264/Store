package com.store.app.servlet;

import com.store.app.connection.dbConnection;
import com.store.app.dao.OrderDao;
import com.store.app.model.Cart;
import com.store.app.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServletTest {

    private OrderServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @BeforeEach
    void setUp() {
        servlet = new OrderServlet();
    }

    @Test
    void doGet_whenCartAndAuthPresent_shouldSubmitAllOrders_clearCart_andRedirectOrders() throws Exception {
        // given
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("action")).thenReturn("checkout"); // pas utilisé mais OK

        // cart-session
        ArrayList<Cart> cartList = new ArrayList<>();
        Cart c1 = new Cart(); c1.setId(10); c1.setQuantity(2);
        Cart c2 = new Cart(); c2.setId(11); c2.setQuantity(1);
        cartList.add(c1);
        cartList.add(c2);

        // auth user
        User auth = mock(User.class);
        when(auth.getId()).thenReturn(99);

        when(session.getAttribute("cart-session")).thenReturn(cartList);
        when(session.getAttribute("auth")).thenReturn(auth);

        // writer
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // mock static dbConnection.getConnection()
        Connection fakeConn = mock(Connection.class);

        try (MockedStatic<dbConnection> mockedDb = mockStatic(dbConnection.class);
             MockedConstruction<OrderDao> mockedCtor = mockConstruction(OrderDao.class,
                     (mockDao, ctx) -> when(mockDao.submitOrder(any())).thenReturn(true))) {

            mockedDb.when(dbConnection::getConnection).thenReturn(fakeConn);

            // when
            servlet.doGet(request, response);

            // then: 2 orders -> submitOrder appelé 2 fois
            List<OrderDao> constructed = mockedCtor.constructed();
            // Ton code construit un OrderDao à chaque item => 2 instances attendues
            assertEquals(2, constructed.size());

            verify(constructed.get(0), times(1)).submitOrder(any());
            verify(constructed.get(1), times(1)).submitOrder(any());

            // panier vidé
            assertTrue(cartList.isEmpty(), "Le panier doit être vidé après la soumission");

            // redirect
            verify(response).sendRedirect("orders.jsp");
        }
    }

    @Test
    void doGet_whenSubmitOrderFails_shouldStopLoop_clearCart_andRedirectOrders() throws Exception {
        // given
        when(request.getSession()).thenReturn(session);

        ArrayList<Cart> cartList = new ArrayList<>();
        Cart c1 = new Cart(); c1.setId(1); c1.setQuantity(1);
        Cart c2 = new Cart(); c2.setId(2); c2.setQuantity(1);
        cartList.add(c1);
        cartList.add(c2);

        User auth = mock(User.class);
        when(auth.getId()).thenReturn(7);

        when(session.getAttribute("cart-session")).thenReturn(cartList);
        when(session.getAttribute("auth")).thenReturn(auth);

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        Connection fakeConn = mock(Connection.class);

        try (MockedStatic<dbConnection> mockedDb = mockStatic(dbConnection.class);
             MockedConstruction<OrderDao> mockedCtor = mockConstruction(OrderDao.class,
                     (mockDao, ctx) -> {
                         // 1er OK, 2e échoue
                         when(mockDao.submitOrder(any()))
                                 .thenReturn(true)
                                 .thenReturn(false);
                     })) {

            mockedDb.when(dbConnection::getConnection).thenReturn(fakeConn);

            // when
            servlet.doGet(request, response);

            // then
            List<OrderDao> constructed = mockedCtor.constructed();
            // Ton code crée un OrderDao par item jusqu'au break => 2 items => 2 constructions
            assertEquals(2, constructed.size());

            verify(constructed.get(0), times(1)).submitOrder(any());
            verify(constructed.get(1), times(1)).submitOrder(any());

            // même si échec, ton code fait cart_list.clear() et redirect orders.jsp
            assertTrue(cartList.isEmpty());
            verify(response).sendRedirect("orders.jsp");
        }
    }

    @Test
    void doGet_whenAuthMissing_shouldRedirectLogin() throws Exception {
        // given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(new ArrayList<Cart>()); // peu importe
        when(session.getAttribute("auth")).thenReturn(null);

        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // when
        servlet.doGet(request, response);

        // then
        verify(response).sendRedirect("login.jsp");
        verify(response, never()).sendRedirect("orders.jsp");
    }

    @Test
    void doPost_shouldDelegateToDoGet() throws Exception {
        // given
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(null);
        when(session.getAttribute("auth")).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // when
        servlet.doPost(request, response);

        // then
        verify(response).sendRedirect("login.jsp");
    }
}
