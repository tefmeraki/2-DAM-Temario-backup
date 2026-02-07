package edu.thepower.psp.u5programacionsegura;

import javax.net.ssl.SSLContext;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.KeyStore;

/**
 * CE5 - EJERCICIO 2 (SERVIDOR TLS)
 * -------------------------------
 * OBJETIVO:
 *  - Crear un servidor con sockets seguros (TLS).
 *  - Cargar su certificado/clave privada desde servidor.jks.
 *  - Aceptar una conexión TLS, leer un mensaje y responder.
 *
 * IDEAS IMPORTANTES:
 *  - En TLS, el servidor "presenta" su certificado.
 *  - El canal queda cifrado automáticamente tras el handshake TLS.
 *  - Aquí usamos un certificado autofirmado (válido para práctica).
 */
public class U5E06ServidorTLS {

    private static final int PORT = 8443;

    private static final String KEYSTORE_FILE = "./recursos/servidor.jks";
    private static final char[] STORE_PASS = "changeit".toCharArray();
    private static final char[] KEY_PASS = "changeit".toCharArray();

    public static void main(String[] args) throws Exception {

        // 1) Cargar el keystore del servidor (contiene certificado + clave privada)
        KeyStore ks = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
            ks.load(fis, STORE_PASS);
        }

        // 2) Crear KeyManagerFactory para que TLS pueda usar la clave privada del servidor
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, KEY_PASS);

        // 3) Crear SSLContext con los KeyManagers
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(kmf.getKeyManagers(), null, null);

        // 4) Crear SSLServerSocket
        SSLServerSocketFactory ssf = ctx.getServerSocketFactory();
        try (SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(PORT)) {

            System.out.println("Servidor TLS escuchando en puerto " + PORT + " ...");

            // 5) Aceptar un cliente (1 conexión para el ejercicio)
            try (SSLSocket socket = (SSLSocket) serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

                // Al leer/escribir, el handshake TLS se realiza automáticamente si hace falta.
                String line = in.readLine();
                System.out.println("Servidor TLS recibió: " + line);

                out.println("ECO SEGURO: " + line);
                System.out.println("Servidor TLS respondió y cerró.");
            }
        }
    }
}

