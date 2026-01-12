package edu.thepower.psp.u5programacionsegura;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * OBJETIVO (SEGURO):
 *  - Registrar un usuario aplicando validación y saneamiento:
 *    - username: 3-20, alfanumérico + _
 *    - edad: entero 0-120
 *    - email: formato básico + longitud máxima
 *    - password: >= 10 y complejidad mínima (may/min/número/símbolo)
 *
 * CÓMO SE HA DESARROLLADO:
 *  - Se crea una clase Validator con reglas claras.
 *  - Se rechaza la entrada con mensajes comprensibles, sin detalles internos.
 *  - Se limita longitud y se elimina espacios extremos.
 */
public class U5E02ValidacionEntradaDatosSeguro {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Validator v = new Validator();

        System.out.println("=== CE2 SEGURO: Registro con validación ===");

        String username = prompt(sc, "Username: ");
        if (!v.validateUsername(username)) {
            System.out.println("Dato no válido: username (3-20, letras/números/_).");
            sc.close();
            return;
        }

        String ageRaw = prompt(sc, "Edad: ");
        Integer age = v.validateAge(ageRaw);
        if (age == null) {
            System.out.println("Dato no válido: edad (0-120).");
            sc.close();
            return;
        }

        String email = prompt(sc, "Email: ");
        if (!v.validateEmail(email)) {
            System.out.println("Dato no válido: email.");
            sc.close();
            return;
        }

        // En consola, usamos String por sencillez; en apps reales, mejor char[].
        String password = prompt(sc, "Password: ");
        if (!v.validatePassword(password)) {
            System.out.println("Dato no válido: password (>=10, may/min/número/símbolo).");
            sc.close();
            return;
        }

        // No imprimimos password.
        System.out.println("\n[REGISTRO OK - SEGURO]");
        System.out.println("username=" + username);
        System.out.println("edad=" + age);
        System.out.println("email=" + email);

        sc.close();
    }

    private static String prompt(Scanner sc, String label) {
        System.out.print(label);
        // Saneamiento mínimo: trim y límite de longitud razonable
        String s = sc.nextLine().trim();
        if (s.length() > 200) { // límite defensivo
            return s.substring(0, 200);
        }
        return s;
    }

    static class Validator {
        private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
        private static final Pattern EMAIL = Pattern.compile("^[^\\s@]{1,64}@[A-Za-z0-9.-]{1,189}\\.[A-Za-z]{2,20}$");

        public static boolean validateUsername(String username) {
            return USERNAME.matcher(username).matches();
        }

        public static Integer validateAge(String ageRaw) {
            try {
                int age = Integer.parseInt(ageRaw);
                if (age < 0 || age > 120) return null;
                return age;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public static boolean validateEmail(String email) {
            if (email.length() > 254) return false; // límite típico
            return EMAIL.matcher(email).matches();
        }

        public static boolean validatePassword(String password) {
            // Comprobación de longitud mínima y máxima
            if (password.length() < 10 || password.length() > 200) {
                return false;
            }

            boolean hasUpper = false;
            boolean hasLower = false;
            boolean hasDigit = false;
            boolean hasSymbol = false;

            // Recorremos carácter a carácter la contraseña
            for (int i = 0; i < password.length(); i++) {
                char ch = password.charAt(i);

                if (Character.isUpperCase(ch)) {
                    hasUpper = true;
                } else if (Character.isLowerCase(ch)) {
                    hasLower = true;
                } else if (Character.isDigit(ch)) {
                    hasDigit = true;
                } else {
                    // Si no es letra ni dígito, lo consideramos símbolo
                    hasSymbol = true;
                }

                // Optimización: si ya tenemos todos los requisitos, salimos
                if (hasUpper && hasLower && hasDigit && hasSymbol) {
                    return true;
                }
            }

            // Si termina el bucle sin cumplir todos los requisitos
            return false;
        }

    }
}