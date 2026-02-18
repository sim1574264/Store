package com.store.app.servlet;

import com.store.app.model.Cart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServletTest {

    private CartServlet servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @BeforeEach
    void setUp() {
        servlet = new CartServlet();
    }

    @Test
    void doGet_whenNoCartInSession_shouldCreateCartAndRedirectHome() throws Exception {
        // given
        when(request.getParameter("id")).thenReturn("5");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("cart-session")).thenReturn(null);

        // response writer (même si ce chemin n'écrit pas forcément)
        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // when
        servlet.doGet(request, response);

        // then
        // redirection
        verify(response).sendRedirect("home.jsp");

        // session setAttribute avec une liste contenant l'item
        @SuppressWarnings("unchecked")
        ArgumentCaptor<ArrayList<Cart>> captor = ArgumentCaptor.forClass(ArrayList.class);

        verify(session).setAttribute(eq("cart-session"), captor.capture());
        ArrayList<Cart> saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(1, saved.size());
        assertEquals(5, saved.get(0).getId());
        assertEquals(1, saved.get(0).getQuantity());
    }

    @Test
    void doGet_whenItemAlreadyInCart_shouldPrintMessageAndNotRedirect() throws Exception {
        // given
        when(request.getParameter("id")).thenReturn("7");
        when(request.getSession()).thenReturn(session);

        ArrayList<Cart> existing = new ArrayList<>();
        Cart c = new Cart();
        c.setId(7);
        c.setQuantity(1);
        existing.add(c);

        when(session.getAttribute("cart-session")).thenReturn(existing);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // when
        servlet.doGet(request, response);

        // then
        String output = sw.toString();
        assertTrue(output.contains("Item Already in Cart"), "Doit afficher le message d'existence");
        assertTrue(output.contains("cart.jsp"), "Doit contenir le lien vers cart.jsp");

        verify(response, never()).sendRedirect(anyString());
        verify(session, never()).setAttribute(eq("cart-session"), any());
    }

    @Test
    void doGet_whenCartExistsAndItemNotInCart_shouldAddAndRedirectHome() throws Exception {
        // given
        when(request.getParameter("id")).thenReturn("9");
        when(request.getSession()).thenReturn(session);

        ArrayList<Cart> existing = new ArrayList<>();
        Cart c = new Cart();
        c.setId(1);
        c.setQuantity(1);
        existing.add(c);

        when(session.getAttribute("cart-session")).thenReturn(existing);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        // when
        servlet.doGet(request, response);

        // then
        // il doit rediriger
        verify(response).sendRedirect("home.jsp");

        // et l'élément doit être ajouté dans la liste existante
        assertEquals(2, existing.size());
        assertEquals(9, existing.get(1).getId());
        assertEquals(1, existing.get(1).getQuantity());

        // pas de setAttribute dans ce chemin (ton code n’en fait pas)
        verify(session, never()).setAttribute(eq("cart-session"), any());
    }
}
