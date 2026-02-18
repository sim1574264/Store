package com.store.app.servlet;

import com.store.app.connection.dbConnection;
import com.store.app.dao.UserDao;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthTest {

    private Auth servlet;

    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @BeforeEach
    void setUp() {
        servlet = new Auth();
    }

    @Test
    void doGet_whenPathIsLogout_andAuthExists_shouldRemoveAuth_andRedirectLogin() throws Exception {
        // given
        when(request.getServletPath()).thenReturn("logout");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(new User());

        // when
        servlet.doGet(request, response);

        // then
        verify(session).removeAttribute("auth");
        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void doGet_whenPathIsLogout_andNoAuth_shouldRedirectLogin() throws Exception {
        // given
        when(request.getServletPath()).thenReturn("logout");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("auth")).thenReturn(null);

        // when
        servlet.doGet(request, response);

        // then
        verify(session, never()).removeAttribute("auth");
        verify(response).sendRedirect("login.jsp");
    }

    @Test
    void doPost_whenCredentialsValid_shouldSetSessionAuth_andRedirectHome() throws Exception {
        // given
        when(request.getParameter("login-email")).thenReturn("a@b.com");
        when(request.getParameter("login-password")).thenReturn("secret");
        when(request.getSession()).thenReturn(session);

        User returnedUser = new User();

        // writer (au cas où)
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        Connection fakeConn = mock(Connection.class);

        try (MockedStatic<dbConnection> mockedDb = mockStatic(dbConnection.class);
             MockedConstruction<UserDao> mockedCtor = mockConstruction(UserDao.class,
                     (mockDao, ctx) -> when(mockDao.userLogin(anyString(), anyString())).thenReturn(returnedUser))) {

            mockedDb.when(dbConnection::getConnection).thenReturn(fakeConn);

            // when
            servlet.doPost(request, response);

            // then
            verify(session).setAttribute("auth", returnedUser);
            verify(response).sendRedirect("home.jsp");
        }
    }

    @Test
    void doPost_whenUserNotFound_shouldPrintMessage_andNotRedirect() throws Exception {
        // given
        when(request.getParameter("login-email")).thenReturn("x@y.com");
        when(request.getParameter("login-password")).thenReturn("bad");
        when(request.getSession()).thenReturn(session);

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        Connection fakeConn = mock(Connection.class);

        try (MockedStatic<dbConnection> mockedDb = mockStatic(dbConnection.class);
             MockedConstruction<UserDao> mockedCtor = mockConstruction(UserDao.class,
                     (mockDao, ctx) -> when(mockDao.userLogin(anyString(), anyString())).thenReturn(null))) {

            mockedDb.when(dbConnection::getConnection).thenReturn(fakeConn);

            // when
            servlet.doPost(request, response);

            // then
            assertTrue(sw.toString().contains("USER NOT FOUND"));
            verify(response, never()).sendRedirect(anyString());
            verify(session, never()).setAttribute(eq("auth"), any());
        }
    }

    @Test
    void doGet_whenPathNotLogout_shouldDelegateToDoPost() throws Exception {
        // given
        when(request.getServletPath()).thenReturn("/"); // ou "login" selon ton mapping
        when(request.getParameter("login-email")).thenReturn("a@b.com");
        when(request.getParameter("login-password")).thenReturn("secret");
        when(request.getSession()).thenReturn(session);

        User returnedUser = new User();
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        Connection fakeConn = mock(Connection.class);

        try (MockedStatic<dbConnection> mockedDb = mockStatic(dbConnection.class);
             MockedConstruction<UserDao> mockedCtor = mockConstruction(UserDao.class,
                     (mockDao, ctx) -> when(mockDao.userLogin(anyString(), anyString())).thenReturn(returnedUser))) {

            mockedDb.when(dbConnection::getConnection).thenReturn(fakeConn);

            // when
            servlet.doGet(request, response);

            // then (effet de doPost)
            verify(session).setAttribute("auth", returnedUser);
            verify(response).sendRedirect("home.jsp");
        }
    }
}
