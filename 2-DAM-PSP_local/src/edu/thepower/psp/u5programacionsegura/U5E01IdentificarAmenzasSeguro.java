package edu.thepower.psp.u5programacionsegura;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;

/**
 * EJERCICIO CE1 (VERSIÓN SEGURA)
 * --------------------------------
 * OBJETIVO:
 *  Simular un "servicio" de consola que recibe peticiones tipo:
 *     - list
 *     - read <ruta>
 *  y corregir los fallos de seguridad típicos del enfoque inseguro.
 *
 * QUÉ SE PRETENDE GARANTIZAR (EN TÉRMINOS DE SEGURIDAD):
 *  1) Evitar inyección de comandos:
 *     - No se deben aceptar comandos arbitrarios.
 *     - Se usa una LISTA BLANCA de comandos permitidos.
 *
 *  2) Evitar path traversal:
 *     - El usuario no debe poder acceder a rutas fuera de un directorio permitido.
 *     - Se normaliza la ruta y se comprueba que permanece dentro de BASE_DIR.
 *
 *  3) Evitar DoS por tamaño:
 *     - Se limita la longitud máxima de la request.
 *
 *  4) Evitar log forging / log injection:
 *     - Si se registran entradas del usuario tal cual, un atacante puede incluir
 *       caracteres de control como '\n' (salto de línea) o '\r' (retorno de carro)
 *       para "inyectar" líneas falsas en el log.
 *
 *       Ejemplo de ataque si NO se sanitiza:
 *         request = "list\n[LOG] Usuario=admin autenticado OK"
 *
 *       Log resultante (FALSO, inyectado por el atacante):
 *         [LOG] Request: list
 *         [LOG] Usuario=admin autenticado OK
 *
 *     - Para evitarlo, se sustituyen los caracteres de control por espacios y
 *       se trunca la longitud registrada.
 *
 * CÓMO SE HA DESARROLLADO:
 *  - Se definen defensas: allowed commands + tamaño máximo + control de rutas + log seguro.
 *  - No se ejecutan comandos reales (solo se simula), pero se implementa el patrón correcto.
 */
public class U5E01IdentificarAmenzasSeguro {

    // Lista blanca de comandos permitidos
    private static final Set<String> ALLOWED_COMMANDS = Set.of("list", "read");

    // Límite razonable de entrada para este ejercicio
    private static final int MAX_REQUEST_LEN = 200;

    // Directorio base permitido: todo "read" debe quedarse dentro de aquí.
    //private static final Path BASE_DIR = Paths.get("data").toAbsolutePath().normalize(); // Hasta Java 8
    private static final Path BASE_DIR = Path.of("./resources").toAbsolutePath().normalize(); // Preferido desde Java 11.

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CE1 SEGURO: Servicio de eco (con validación) ===");
        System.out.println("Comandos permitidos: list | read <ruta_relativa_en_data>");
        System.out.print("> ");

        String request = sc.nextLine();

        // 1) Mitigación DoS por tamaño: no aceptamos peticiones excesivamente largas.
        if (request.length() > MAX_REQUEST_LEN) {
            System.out.println("ERROR: petición demasiado larga.");
            sc.close();
            return;
        }

        // 2) Log seguro: evitamos log forging (inyección de líneas en logs)
        //    - \\p{Cntrl} coincide con caracteres de control (p.ej. \n, \r, \t, etc.)
        //    - Se sustituyen por espacios para mantener el log en una sola línea "limpia".
        //    - Además, truncamos para no saturar el log.
        System.out.println("[LOG] Request recibida: " + safeLog(request, 120));

        String[] parts = request.trim().split("\\s+", 2);
        String cmd = parts.length > 0 ? parts[0].toLowerCase() : "";
        String arg = parts.length == 2 ? parts[1] : "";

        // 3) Mitigación de inyección: lista blanca de comandos.
        if (!ALLOWED_COMMANDS.contains(cmd)) {
            System.out.println("ERROR: comando no permitido.");
            sc.close();
            return;
        }

        if ("list".equals(cmd)) {
            System.out.println("OK: listando recursos dentro de " + BASE_DIR + " (simulado)");
        } else if ("read".equals(cmd)) {
            // 4) Mitigación de traversal: control estricto de ruta
            if (arg.isBlank()) {
                System.out.println("ERROR: falta ruta.");
                sc.close();
                return;
            }

            // Resolvemos la ruta de usuario dentro de BASE_DIR y normalizamos (elimina ../, etc.)
            Path resolved = BASE_DIR.resolve(arg).normalize();

            // Comprobación clave: la ruta final debe empezar por BASE_DIR
            // Si no, el usuario ha intentado salir del directorio permitido (path traversal).
            if (!resolved.startsWith(BASE_DIR)) {
                System.out.println("ERROR: ruta no permitida (posible path traversal).");
                sc.close();
                return;
            }

            System.out.println("OK: leyendo fichero permitido: " + resolved + " (simulado)");
        }

        sc.close();
    }

    /**
     * safeLog:
     *  - Sustituye caracteres de control (\\p{Cntrl}) por espacios para impedir log forging:
     *    evita que el usuario inserte saltos de línea, retornos de carro, etc. en el log.
     *  - Trunca a maxLen para evitar saturación de logs.
     *
     * ¿Qué busca \\p{Cntrl}?
     *  - Coincide con caracteres de control Unicode: \n, \r, \t, \0, ESC, etc.
     */
    private static String safeLog(String input, int maxLen) {
        String cleaned = input.replaceAll("\\p{Cntrl}", " ");
        if (cleaned.length() > maxLen) {
            return cleaned.substring(0, maxLen) + "...(truncado)";
        }
        return cleaned;
    }
}
