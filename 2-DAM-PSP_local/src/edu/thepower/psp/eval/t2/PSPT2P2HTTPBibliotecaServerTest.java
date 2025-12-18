package edu.thepower.psp.eval.t2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PSPT2P2HTTPBibliotecaServerTest {

    static class FakeSocket extends Socket {
        private final InputStream in;
        private final ByteArrayOutputStream out;

        FakeSocket(String rawHttpRequest) {
            this.in = new ByteArrayInputStream(rawHttpRequest.getBytes(StandardCharsets.UTF_8));
            this.out = new ByteArrayOutputStream();
        }

        @Override public InputStream getInputStream() { return in; }
        @Override public OutputStream getOutputStream() { return out; }

        String getWrittenText() {
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static String callResponderHTML(String rawRequest) throws Exception {
        FakeSocket socket = new FakeSocket(rawRequest);

        Method m = PSPT2P2HTTPBibliotecaServer.class.getDeclaredMethod("responderHTML", Socket.class);
        m.setAccessible(true);
        m.invoke(null, socket);

        return socket.getWrittenText();
    }

    private static String httpGet(String path) {
        return "GET " + path + " HTTP/1.1\r\nHost: localhost\r\n\r\n";
    }

    private static String httpPost(String path) {
        return "POST " + path + " HTTP/1.1\r\nHost: localhost\r\n\r\n";
    }

    @Test
    @DisplayName("GET / returns 200 and shows index options")
    void getRootReturnsIndex() throws Exception {
        String response = callResponderHTML(httpGet("/"));

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html;charset=UTF-8"));
        assertTrue(response.contains("<h1>Catálogo de libros</h1>"));
        assertTrue(response.contains("Opciones disponibles:"));
        assertTrue(response.contains("href=\"/libros\""));
        assertTrue(response.contains("href=\"/libros_total\""));
    }

    @Test
    @DisplayName("GET /libros_total returns 200 and correct total (5)")
    void getLibrosTotalReturnsCount() throws Exception {
        String response = callResponderHTML(httpGet("/libros_total"));

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Total libros: 5"));
    }

    @Test
    @DisplayName("GET /libros_total/ also works")
    void getLibrosTotalTrailingSlashWorks() throws Exception {
        String response = callResponderHTML(httpGet("/libros_total/"));

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Total libros: 5"));
    }

    @Test
    @DisplayName("GET /libros returns 200 and lists all books")
    void getLibrosReturnsList() throws Exception {
        String response = callResponderHTML(httpGet("/libros"));

        assertTrue(response.startsWith("HTTP/1.1 200 OK"));
        assertTrue(response.contains("El Quijote"));
        assertTrue(response.contains("Cien años de soledad"));
        assertTrue(response.contains("1984"));
        assertTrue(response.contains("Pantaleón y las visitadoras"));
        assertTrue(response.contains("Dune"));
        assertTrue(response.contains("<ul>"));
        assertTrue(response.contains("</ul>"));
    }

    @Test
    @DisplayName("GET unknown route returns 404 Not Found")
    void getUnknownReturns404() throws Exception {
        String response = callResponderHTML(httpGet("/noexiste"));

        assertTrue(response.startsWith("HTTP/1.1 404 Not Found"));
        assertTrue(response.contains("ERROR: no es una opción aceptada."));
    }

    @Test
    @DisplayName("Non-GET method returns 405 Not Allowed")
    void postReturns405() throws Exception {
        String response = callResponderHTML(httpPost("/"));

        assertTrue(response.startsWith("HTTP/1.1 405 Not Allowed"));
        assertTrue(response.contains("ERROR: solo se admiten solicitudes de tipo GET"));
    }

    @Test
    @DisplayName("Response includes Content-Length header")
    void responseHasContentLengthHeader() throws Exception {
        String response = callResponderHTML(httpGet("/"));

        assertTrue(response.contains("Content-Length: "));
        assertTrue(response.matches("(?s).*Content-Length: \\d+\\s*\\R\\R.*"));
    }
}