package edu.thepower.psp.u5programacionsegura;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;

/**
 * CE5 - EJERCICIO 1 (CLIENTE)
 * --------------------------
 * OBJETIVO:
 *  - Cargar un certificado X.509 (servidor.crt) que contiene la CLAVE PÚBLICA del servidor.
 *  - Cifrar un mensaje corto con esa clave pública (RSA).
 *  - Guardar el mensaje cifrado en un fichero (mensaje.bin).
 *
 * IDEAS IMPORTANTES (SEGURIDAD / DIDÁCTICA):
 *  - El cliente NO necesita la clave privada.
 *  - El certificado permite distribuir la clave pública.
 *  - Si alguien intercepta mensaje.bin, verá bytes "incomprensibles" (confidencialidad),
 *    pero esto NO garantiza autenticidad del servidor (eso se aborda mejor con TLS).
 *
 * NOTA:
 *  - RSA no es para cifrar grandes volúmenes; aquí ciframos un mensaje muy corto.
 *  
 *  1) Generar el keystore con certificado (servidor)
 * keytool -genkeypair -alias servidor -keyalg RSA -keysize 2048 ^
 * -keystore servidor.jks -storepass changeit -keypass changeit ^
 * -dname "CN=ServidorPSP, OU=DAM, O=IES, L=Ciudad, S=Provincia, C=ES" ^
 * -validity 365

 * 2) Exportar el certificado (para el cliente)
 * keytool -exportcert -alias servidor -keystore servidor.jks -storepass changeit ^
 * -rfc -file servidor.crt
 */

public class U5E05CifradoAsimetricoRSACliente {

    private static final String CERT_FILE = "./recursos/servidor.crt";
    private static final String OUT_FILE = "./recursos/mensaje.bin";

    public static void main(String[] args) throws Exception {

        // 1) Cargar certificado X.509 desde fichero .crt
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert;
        try (FileInputStream fis = new FileInputStream(CERT_FILE)) {
            cert = cf.generateCertificate(fis);
        }

        // 2) Extraer la clave pública del certificado
        PublicKey publicKey = cert.getPublicKey();

        // 3) Mensaje a cifrar (corto)
        String message = "HOLA PSP - Mensaje confidencial (Ejercicio 1).";
        // No parece que sea necesario el charset
        // byte[] plaintext = message.getBytes(StandardCharsets.UTF_8);
        byte[] plaintext = message.getBytes();

        // 4) Cifrar con RSA
        // Para un ejercicio sencillo, usamos "RSA/ECB/PKCS1Padding" (muy común en ejemplos).
        // En sistemas reales se prefiere OAEP (por ejemplo: "RSA/ECB/OAEPWithSHA-256AndMGF1Padding").
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // IMPORTANTE: si plaintext es demasiado largo, lanzará IllegalBlockSizeException.
        byte[] ciphertext = cipher.doFinal(plaintext);

        // 5) Guardar el resultado en un fichero binario
        try (FileOutputStream fos = new FileOutputStream(OUT_FILE)) {
            fos.write(ciphertext);
        }

        System.out.println("Cliente: mensaje cifrado y guardado en " + OUT_FILE);
        System.out.println("Cliente: (texto original) " + message);
    }
}

