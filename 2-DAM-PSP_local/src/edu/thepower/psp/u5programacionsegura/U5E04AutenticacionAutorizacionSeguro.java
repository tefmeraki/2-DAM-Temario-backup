package edu.thepower.psp.u5programacionsegura;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * CE4 (SEGURO - PEDAGÓGICO) - Autenticación y autorización con roles
 * -----------------------------------------------------------------
 * OBJETIVO DEL EJERCICIO:
 *  - Practicar la diferencia entre:
 *      1) AUTENTICACIÓN: "¿Quién eres?" (comprobar credenciales)
 *      2) AUTORIZACIÓN: "¿Qué puedes hacer?" (comprobar permisos / roles)
 *
 *  - Introducir una buena práctica fundamental:
 *      ❌ NUNCA guardar contraseñas en texto plano.
 *
 *  - Como es un ejercicio pedagógico, usaremos un método "ligero" para NO guardar
 *    la password en claro. No buscamos criptografía real, sino el concepto.
 *
 * IMPORTANTE SOBRE "PASSWORD ENCRIPTADA":
 *  - En sistemas reales, las contraseñas NO deberían "cifrarse" para poder recuperarlas,
 *    sino almacenarse como una transformación irreversible (hash) con sal, usando algoritmos
 *    adecuados (PBKDF2, bcrypt, scrypt, Argon2, etc.).
 *
 *  - Aquí haremos una versión simplificada:
 *      - Generamos una SAL (valor aleatorio) por usuario.
 *      - Guardamos SOLO:
 *          (1) la sal
 *          (2) una "transformación" (huella) de password+sal
 *      - Así:
 *          ✅ No guardamos la password original
 *          ✅ Para verificar, repetimos la transformación y comparamos
 *
 *  - AVISO: Usar hashCode() NO es seguro para producción. Se usa SOLO para aprendizaje.
 *
 * CÓMO SE HA DESARROLLADO:
 *  - Users en memoria (simula base de datos)
 *  - Login con 3 intentos máximos (bloqueo local simple)
 *  - Session con rol (USER/ADMIN)
 *  - Menú con comprobación de permisos antes de ejecutar acciones (RBAC simple)
 *  - Mensajes de error genéricos para no revelar si el usuario existe
 */
public class U5E04AutenticacionAutorizacionSeguro {

    // Roles básicos (control de acceso)
    enum Rol { USER, ADMIN }

    /**
     * Registro de usuario (simula una fila de BD):
     *  - username: identificador
     *  - role: permisos
     *  - salt: valor aleatorio por usuario (para que passwords iguales no den el mismo resultado)
     *  - passwordValue: transformación de password+salt (NO es la password en claro)
     */
    
    /**
     * Utilidad pedagógica para NO guardar passwords en claro.
     *
     * IDEA CLAVE:
     *  - Guardar "huellas" en lugar de la password original.
     *  - Con una SAL por usuario.
     *
     * ¿Por qué SAL?
     *  - Si dos usuarios tienen la misma password:
     *      - SIN sal: quedarían valores guardados iguales (mala idea)
     *      - CON sal: el valor guardado cambia porque password+sal es distinto
     *
     * ¿Por qué esto es "ligero"?
     *  - Usamos hashCode() (simple de entender) en vez de algoritmos criptográficos.
     *
     * AVISO:
     *  - En sistemas reales, NO usar hashCode(): es rápido y predecible.
     *  - Se usan hashes lentos (PBKDF2, bcrypt, Argon2...) para resistir fuerza bruta.
     */

    static class UserRecord {
        private static final Random RND = new Random();
    	
    	final String username;
        final Rol role;
        final int salt;
        final int passwordValue;
        int failedAttempts;
        boolean locked;

        UserRecord(String username, String password, Rol role) {
            this.username = username;
            this.role = role;
            /* Genera SAL distinta por usuario.
             * Calcula la transformación de password+sal.
             * Guarda SOLO sal + valor transformado (NO la password).
            */
            this.salt = generarSalt();
            this.passwordValue = transformarPassword(salt, password);
            failedAttempts = 0;
            locked = false;
        }
        
        private static int generarSalt() {
        	return RND.nextInt(10_000); // Devuelve un valor entero entre 0 y 9.999
        }
        
        private static int transformarPassword(int salt, String password) {
        	return (password + salt).hashCode(); // Se devuelve una "huella" para la concatenación password + salt
        }
        
        public boolean validarPassword (String password) {
        	return transformarPassword(salt, password) == passwordValue;
        }
        
        public boolean isLocked() {
            return locked;
        }
        
        public void manageLocking (boolean valor) {
        	locked = valor;
        }
        
        public int incFailedAttemps() {
        	return ++failedAttempts;
        }
        
        public void resetFailedAttemps () {
        	failedAttempts = 0;
        }
    }

    /**
     * Sesión activa tras autenticación correcta:
     *  - username y role: necesarios para autorizar acciones.
     */
    static class Session {
        final String username;
        final Rol role;

        Session(String username, Rol role) {
            this.username = username;
            this.role = role;
        }
    }

    /**
     * Control de acceso:
     *  - USER puede acceder a acciones USER
     *  - ADMIN puede acceder a todo
     */
    static class AccessControl {
        public static boolean canAccess(Rol required, Session session) {
        	boolean permitido = false;
        	if (session != null) {
        		if (required == Rol.USER)
        			permitido = true;
        		else
        			permitido = (session.role == Rol.ADMIN); 
        	}
        	
        	return permitido;
        }
    }

    /**
     * Servicio de autenticación:
     *  - login con máximo 3 intentos fallidos
     *  - devuelve Session si OK, null si falla
     */
    static class AuthService {
        private final Map<String, UserRecord> users;
        
        AuthService(Map<String, UserRecord> users) {
            this.users = users;
        }

        /**
         * Autenticación:
         *  - Mensaje genérico: no decir si el usuario existe o no.
         *  - Si el usuario existe, se verifica la password con salt + transformación.
         */
        Session login(String username, String password) {
            UserRecord record = users.get(username);
            Session session = null;
            
            boolean ok = false;
            if (record != null) {
            	// Comprobamos que el usuario no está bloqueado y puede acceder al sistema.
            	if (record.isLocked()) {
            		System.out.println("Acceso bloqueado por demasiados intentos fallidos.");
            	} else {
            		// Validamos password
            		ok =  record.validarPassword(password);
            		if (!ok) {
            			System.out.println("Credenciales inválidas.");
            			if (record.incFailedAttemps() > 2) { // Si se introduce 3 veces la password mal, se bloque al usuario
            				System.out.println("Usuario bloqueado.Superado intentos acceso.");
            				record.manageLocking(true);
            			}
            		} else {
            			record.resetFailedAttemps(); // Reiniciar el contador de fallos de login antes de salir
            			session = new Session(record.username, record.role);
            		}
            	}
            } else
            	System.out.println("Credenciales inválidas.");
            
            return session;
        }
    }

    public static void main(String[] args) {
        // "Base de datos" en memoria
        Map<String, UserRecord> users = new HashMap<>();

        // Creamos usuarios (simulando alta en un sistema real)
        users.put("admin", new UserRecord("admin", "123", Rol.ADMIN)); // Admin123!
        users.put("ana", new UserRecord("ana", "123", Rol.USER)); // Ana123!!aa 

        AuthService auth = new AuthService(users);
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CE4 SEGURO (PEDAGÓGICO): Login + Roles ===");

        // --- LOGIN ---
        Session session = null;
        while (session == null) {

            System.out.print("Usuario: ");
            String u = sc.nextLine().trim();

            System.out.print("Password: ");
            String p = sc.nextLine(); // en real, mejor char[]; aquí se simplifica

            session = auth.login(u, p);
        }

        System.out.println("Login OK. Rol=" + session.role);

        // --- MENÚ DE ACCIONES (AUTORIZACIÓN) ---
        System.out.println("1) Ver perfil (USER o ADMIN)");
        System.out.println("2) Ver lista de usuarios (ADMIN)");
        System.out.println("3) Apagar servicio (ADMIN)");
        System.out.print("> ");

        int opt;
        try {
            opt = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida.");
            sc.close();
            return;
        }

        if (opt == 1) {
            // Requiere USER (cualquiera autenticado)
            if (!AccessControl.canAccess(Rol.USER, session)) {
                System.out.println("Acceso denegado.");
            } else {
                System.out.println("Perfil: " + session.username + " (rol=" + session.role + ")");
            }

        } else if (opt == 2) {
            // Requiere ADMIN
            if (!AccessControl.canAccess(Rol.ADMIN, session)) {
                System.out.println("Acceso denegado (requiere ADMIN).");
            } else {
                System.out.println("Usuarios: " + users.keySet());
            }

        } else if (opt == 3) {
            // Requiere ADMIN
            if (!AccessControl.canAccess(Rol.ADMIN, session)) {
                System.out.println("Acceso denegado (requiere ADMIN).");
            } else {
                System.out.println("Servicio apagado (simulado).");
            }

        } else {
            System.out.println("Opción inválida.");
        }

        sc.close();
    }
}