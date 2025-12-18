package edu.thepower.psp.eval.t2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class PSPT2P1CalcJUnitTests {

	private static final String HOST = "127.0.0.1";
	private static final int PUERTO = 5000;

	@BeforeAll
	static void comprobarServidorDisponible() {
		assertTrue(isPortOpen(HOST, PUERTO, 300), "El servidor no parece estar escuchando en " + HOST + ":" + PUERTO);
	}

	@Test
	@DisplayName("1) SUMA devuelve RESULTADO")
	void suma_ok() throws IOException {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(HOST, PUERTO), 500);
			s.setSoTimeout(1200);

			BufferedReader in = reader(s);
			PrintWriter out = writer(s);

			out.println("SUMA 2 3");
			String resp = in.readLine();

			//assertEquals("RESULTADO: 5.0", resp);
			assertTrue(resp.contains("5.0"));
		}
	}

	@Test
	@DisplayName("2) DIV por cero devuelve ERROR")
	void divisionPorCero_error() throws IOException {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(HOST, PUERTO), 500);
			s.setSoTimeout(1200);

			BufferedReader in = reader(s);
			PrintWriter out = writer(s);

			out.println("DIV 10 0");
			String resp = in.readLine();

			assertNotNull(resp);
			assertTrue(resp.startsWith("ERROR:"), "Esperaba ERROR:, pero fue: " + resp);
		}
	}

	@Test
	@DisplayName("3) Operación desconocida devuelve ERROR")
	void operacionDesconocida_error() throws IOException {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(HOST, PUERTO), 500);
			s.setSoTimeout(1200);

			BufferedReader in = reader(s);
			PrintWriter out = writer(s);

			out.println("POTENCIA 2 3");
			String resp = in.readLine();

			assertNotNull(resp);
			assertTrue(resp.startsWith("ERROR:"), "Esperaba ERROR:, pero fue: " + resp);
		}
	}

	@Test
	@DisplayName("4) Formato incorrecto devuelve ERROR")
	void formatoIncorrecto_error() throws IOException {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(HOST, PUERTO), 500);
			s.setSoTimeout(1200);

			BufferedReader in = reader(s);
			PrintWriter out = writer(s);

			out.println("SUMA 1");
			String resp = in.readLine();

			assertNotNull(resp);
			assertTrue(resp.startsWith("ERROR:"), "Esperaba ERROR:, pero fue: " + resp);
		}
	}

	@Test
	@DisplayName("5) FIN devuelve despedida y no cuelga")
	void fin_devuelveDespedida() throws IOException {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(HOST, PUERTO), 500);
			s.setSoTimeout(1200);

			BufferedReader in = reader(s);
			PrintWriter out = writer(s);

			out.println("FIN");
			String resp = in.readLine();

			assertEquals("Hasta la vista", resp);

			// No exigimos que el servidor cierre inmediatamente el socket,
			// pero sí que el test no se quede colgado.
			assertTimeoutPreemptively(Duration.ofMillis(400), () -> {
				in.readLine(); // puede devolver null o bloquear poco (timeout del socket)
			});
		}
	}

	@Test
	@DisplayName("6) Concurrencia: dos clientes en paralelo reciben respuesta")
	void concurrencia_dosClientes() {
		assertTimeoutPreemptively(Duration.ofSeconds(3), () -> {
			Thread t1 = new Thread(() -> assertClientOperation("SUMA 100 5", "RESULTADO = 105.0"));
			Thread t2 = new Thread(() -> assertClientOperation("MULT 7 6", "RESULTADO = 42.0"));

			t1.start();
			t2.start();
			t1.join();
			t2.join();
		});
	}

	// ------------------- Helpers -------------------

	private static void assertClientOperation(String op, String expected) {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(HOST, PUERTO), 500);
			s.setSoTimeout(1200);

			BufferedReader in = reader(s);
			PrintWriter out = writer(s);

			out.println(op);
			String resp = in.readLine();

			assertEquals(expected, resp);

			// Cerrar educadamente
			out.println("FIN");
			in.readLine();

		} catch (IOException e) {
			fail("Error de IO en cliente paralelo: " + e.getMessage());
		}
	}

	private static BufferedReader reader(Socket s) throws IOException {
		return new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
	}

	private static PrintWriter writer(Socket s) throws IOException {
		return new PrintWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8), true);
	}

	private static boolean isPortOpen(String host, int port, int timeoutMs) {
		try (Socket s = new Socket()) {
			s.connect(new InetSocketAddress(host, port), timeoutMs);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
