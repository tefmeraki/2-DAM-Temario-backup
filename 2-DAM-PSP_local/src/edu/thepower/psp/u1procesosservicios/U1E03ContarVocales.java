package edu.thepower.psp.u1procesosservicios;

/*
 * U1E03: clase para contar una vocal determinada en un archivo de texto.
 * Tema 1. Programación multiproceso.
 * https://oscarmaestre.github.io/servicios/textos/tema1.html
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class U1E03ContarVocales {
	private final static Map<Character, Character> VOCALES = Map.of('a', 'á',
																	'e', 'é',
																	'i', 'í',
																	'o', 'ó',
																	'u', 'ú');
	
	public void contarVocal (String inputFile, char vocal) {
		String linea;
		int contador = 0;
		try (BufferedReader bis = new BufferedReader(new FileReader(inputFile))){
			while ((linea = bis.readLine()) != null) {
				linea = linea.toLowerCase();
				for (int i = 0; i < linea .length(); i++) {
					char car = linea.charAt(i);
					if (car == vocal || car == VOCALES.get(vocal))
						contador++;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(contador);
	}

	public static void main(String[] args) {
		U1E03ContarVocales cv = new U1E03ContarVocales();
		cv.contarVocal(args[0], args[1].charAt(0));
	}

}
