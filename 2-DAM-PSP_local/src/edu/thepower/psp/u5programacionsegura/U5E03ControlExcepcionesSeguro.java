package edu.thepower.psp.u5programacionsegura;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CE3 (SEGURO) - Control de errores sin fugas + logging interno
 * ------------------------------------------------------------ OBJETIVO:
 * Simular una "API" por consola con operaciones que pueden fallar: 1) Leer
 * config.txt con FileReader 2) Parsear JSON (simulado) 3) Conectar a BD
 * (simulado)
 *
 * QUÉ SE PRETENDE GARANTIZAR (SEGURIDAD): 1) Confidencialidad de información
 * interna: - NO mostrar stack traces ni detalles de rutas/clases al usuario.
 *
 * 2) Disponibilidad: - Un error no termina la app de forma abrupta; se devuelve
 * un error controlado.
 *
 * 3) Observabilidad controlada: - Se registra internamente (Logger) el detalle
 * necesario para diagnóstico.
 *
 * CÓMO SE HA DESARROLLADO: - main se limita a: - leer opción - llamar a
 * handleRequest(...) - capturar excepciones y mapearlas a códigos + mensajes
 * controlados - handleRequest(...) NO usa clases auxiliares (Result): -
 * devuelve String si todo va bien - lanza excepción si algo falla
 */
public class U5E03ControlExcepcionesSeguro {
	private static final Logger LOG = Logger.getLogger(U5E03ControlExcepcionesSeguro.class.getName());

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.println("=== CE3 SEGURO: API sin fugas (FileReader, sin Result) ===");
		System.out.println("1) Leer fichero config.txt");
		System.out.println("2) Parsear JSON (muy simple)");
		System.out.println("3) Conectar a BD (simulada)");
		System.out.print("> ");

		int option = 0;
		try {
			option = Integer.parseInt(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			// Error de entrada del usuario: mensaje claro pero sin detalles internos
			System.out.println("ERROR [ERR_INPUT]: la opción debe ser numérica.");
		}

		if (option != 0) {
			try {
				// Si va bien, handleRequest devuelve el mensaje de éxito
				String okMessage = handleRequest(sc, option);
				System.out.println("OK: " + okMessage);
			} catch (IllegalArgumentException e) {
				// Entrada inválida / parse inválido / opción no válida
				// Usuario: mensaje genérico
				System.out.println("ERROR [ERR_INPUT]: petición no válida.");

				// Log interno: guardamos detalle técnico (sin mostrarlo al usuario)
				LOG.log(Level.INFO, "Petición inválida en opción " + option + ": " + e.getMessage());

			} catch (IOException e) {
				// Fallos de IO (archivo no existe, permisos, etc.) o “conexión” simulada
				System.out.println("ERROR [ERR_IO]: error al acceder a un recurso.");

				// Log interno con detalle
				LOG.log(Level.WARNING, "Fallo IO en opción " + option + ": " + e.getMessage());

			} catch (RuntimeException e) {
				// Cualquier error inesperado
				System.out.println("ERROR [ERR_UNKNOWN]: error interno.");

				// Aquí sí registramos stack trace, pero SOLO en logs internos
				LOG.log(Level.SEVERE, "Error inesperado en opción " + option, e);

			}
		}
		
		sc.close();
	}

	/**
	 * handleRequest: - NO usa clases auxiliares para devolver resultados. -
	 * Devuelve un String con el mensaje de éxito. - Lanza excepciones si algo
	 * falla.
	 */
	private static String handleRequest(Scanner sc, int option) throws IOException {
		if (option == 1) {
			// Leemos config.txt con FileReader (y BufferedReader)
			String firstLine = readFirstLine("config.txt");

			// Medida de seguridad habitual:
			// No mostrar el contenido de config en consola, podría contener secretos.
			// (En un sistema real, mostrarías solo si procede, o en modo debug controlado).
			if (firstLine == null) {
				return "Config leída (vacía o sin primera línea).";
			}
			return "Config leída correctamente (contenido omitido).";

		} else if (option == 2) {
			System.out.print("JSON: ");
			String json = sc.nextLine();

			// Validación mínima de ejemplo (parse "simulado")
			if (!json.trim().startsWith("{")) {
				throw new IllegalArgumentException("JSON no empieza por '{'");
			}
			return "JSON válido (simulado).";

		} else if (option == 3) {
			// Simulamos una conexión fallida (IO)
			throw new IOException("Conexión BD rechazada (simulada)");

		} else {
			// Opción fuera de rango => error de entrada
			throw new IllegalArgumentException("Opción fuera de rango");
		}
	}

	/**
	 * Lectura con FileReader: - try-with-resources para cerrar correctamente el
	 * recurso. - Se lee solo la primera línea para mantener el ejercicio sencillo.
	 */
	private static String readFirstLine(String filename) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			return br.readLine();
		}
	}
}
