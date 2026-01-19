package edu.thepower.psp.u5programacionsegura;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import javax.crypto.Cipher;

/**
 * CE5 - EJERCICIO 1 (SERVIDOR)
 * ---------------------------
 * OBJETIVO:
 *  - Cargar el keystore del servidor (servidor.jks) que contiene la CLAVE PRIVADA.
 *  - Leer el fichero mensaje.bin (mensaje cifrado).
 *  - Descifrar el mensaje con la clave privada (RSA) y mostrarlo.
 *
 * IDEAS IMPORTANTES:
 *  - La clave privada NO se comparte.
 *  - El servidor puede descifrar porque posee la privada.
 *  - Esto garantiza confidencialidad del mensaje frente a terceros.
 */
public class U5E05CifradoAsimetricoRSAServidor {

    private static final String KEYSTORE_FILE = "./recursos/servidor.jks";
    private static final char[] STORE_PASS = "changeit".toCharArray();
    private static final char[] KEY_PASS = "changeit".toCharArray();
    private static final String ALIAS = "servidor";

    private static final String IN_FILE = "./recursos/mensaje.bin";

    public static void main(String[] args) throws Exception {

        // 1) Cargar el keystore (JKS) del servidor
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            ks.load(fis, STORE_PASS);
        }

        // 2) Extraer la clave privada asociada al alias
        Key key = ks.getKey(ALIAS, KEY_PASS);
        if (!(key instanceof PrivateKey)) {
            throw new IllegalStateException("La clave obtenida no es una PrivateKey.");
        }
        PrivateKey privateKey = (PrivateKey) key;

        // 3) Leer el mensaje cifrado desde fichero
        byte[] ciphertext = Files.readAllBytes(Paths.get(IN_FILE));

        // 4) Descifrar con RSA usando la clave privada
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] plaintext = cipher.doFinal(ciphertext);

        // 5) Mostrar resultado
        String message = new String(plaintext, java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("Servidor: mensaje descifrado:");
        System.out.println(message);
    }
}
