package edu.thepower.psp.u5programacionsegura;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 ************ VERSIÓN COSECHA PROPIA ************
 ************ sobre la base de propuesta IA
 * 
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

public class U5E02ValidacionEntradaDatosSeguroR {
	private static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		boolean todoBien = true;
        
        System.out.println("=== CE2 SEGURO: Registro con validación ===");

        String username = prompt("Username: ", 30);
        if (!Validator.validarUser(username))
        	// Mensaje de error en el usuario
        	todoBien = false;
        
        String age = null;
        if (todoBien) {
        	age = prompt("Edad: ", 2);
        	if (Validator.validarEdad(age) == null)
        		todoBien = false;
        }

        String email = null;
        if (todoBien) {
        	email = prompt("Email: ", 282);
        	if (!Validator.validarEmail(email))
        		todoBien = false;
        }

        String password = null;
        if (todoBien) {
        	password = prompt("Password: ", 20);
        	if (!Validator.validarPassword(password)) {
        		System.err.println("Error passwd...");
        		todoBien = false;
        	}
        }

        if (todoBien) {
        	System.out.println("\n[REGISTRO OK - SEGURO]");
        	System.out.println("username=" + username);
        	System.out.println("edad=" + age);
        	System.out.println("email=" + email);
        	//System.out.println("password=" + password);
        }

        sc.close();
    }
	
	private static String prompt (String texto, int longitud) {
		System.out.println(texto);
		String valor = sc.nextLine().trim();
		if (valor.length() > longitud)
			valor = valor.substring(0, longitud);
		
		return valor;
	}
	
	static class Validator {
		private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{3,30}$");
		private static final Pattern EMAIL = Pattern.compile("^[^\\s@]{1,64}@[a-zA-Z0-9.-]{1,198}\\.[a-zA-Z]{2,20}$");
		
		public static boolean validarUser (String username) {
			return USERNAME.matcher(username).matches();
		}
		
		public static boolean validarEmail (String email) {
			return EMAIL.matcher(email).matches();
		}
		
		public static Integer validarEdad (String edad) {
			Integer edadNum = null;
			try {
				edadNum = Integer.parseInt(edad);
				if (edadNum < 18 || edadNum > 60)
					edadNum = null;
			} catch (NumberFormatException e) {
				System.err.println("Edad no numérica.");
			}
			
			return edadNum;
		}
		
		public static boolean validarPassword (String passwd) {
			boolean tieneMay = false;
			boolean tieneMin = false;
			boolean tieneNum = false;
			boolean tieneSim = false;
			
			if (passwd.length() > 11) {
				char[] caractares = passwd.toCharArray();		
				
				boolean salir = false;
				for (int i = 0;i < caractares.length && !salir;i++) {
					char c = caractares[i];
					if (!tieneMay || !tieneMin || !tieneNum || !tieneSim) {
						if (Character.isUpperCase(c))
							tieneMay = true;
						else if (Character.isLowerCase(c))
								tieneMin = true;
							 else if (Character.isDigit(c))
								 	tieneNum = true;
							 	  else tieneSim = true;
					} else 
						salir = true;
				}
			}
			
			return tieneMay && tieneMin && tieneNum && tieneSim;
		}
	}
}