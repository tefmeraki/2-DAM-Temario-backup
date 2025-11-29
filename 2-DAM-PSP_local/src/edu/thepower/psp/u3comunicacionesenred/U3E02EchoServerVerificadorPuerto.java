package edu.thepower.psp.u3comunicacionesenred;

public final class U3E02EchoServerVerificadorPuerto {

	private U3E02EchoServerVerificadorPuerto() {
		// Evitar que se instancie
	}
	
	public static int verificarPuerto (String[] args) {
				
		if (args.length != 1)
			throw new IllegalArgumentException("Es necesario proporcionar el puerto como argumento en el que se recibirán las solicitudes (valores válidos: 1.024-65.535)");
		
		int puerto;
		
		try {
			puerto = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("El argumento proporcionad no es un número entero - " + e.getMessage());
		}
		
		if (puerto < 1_024 || puerto > 65_535)
			throw new IllegalArgumentException("El valor del número de puerto debe estar comprendido entre 1.024 y 65.535");
		
		return puerto;
	}
}
