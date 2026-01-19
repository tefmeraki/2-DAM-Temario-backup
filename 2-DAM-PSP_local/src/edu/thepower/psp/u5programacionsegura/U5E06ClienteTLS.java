package edu.thepower.psp.u5programacionsegura;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.KeyStore;

/**
 * CE5 - EJERCICIO 2 (CLIENTE TLS)
 * ------------------------------
 * OBJETIVO:
 *  - Conectar a un servidor usando TLS.
 *  - Confiar en el certificado del servidor mediante un truststore local.
 *  - Enviar un mensaje y leer la respuesta.
 *
 * IDEAS IMPORTANTES:
 *  - El truststore NO contiene la clave privada del servidor.
 *  - Contiene certificados en los que el cliente confía.
 *  - Si el cliente no confía, TLS fallará (SSLHandshakeException).
 */
public class U5E06ClienteTLS {

    private static final String HOST = "localhost";
    private static final int PORT = 8443;

    private static final String TRUSTSTORE_FILE = "cliente-truststore.jks";
    private static final char[] TRUST_PASS = "changeit".toCharArray();

    public static void main(String[] args) throws Exception {

        // 1) Cargar truststore del cliente (contiene el certificado del servidor)
        KeyStore ts = KeyStore.getInstance("JKS");
        try (FileInputStream fis = new FileInputStream(TRUSTSTORE_FILE)) {
            ts.load(fis, TRUST_PASS);
        }

        // 2) Crear TrustManagerFactory para validar el certificado del servidor
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ts);

        // 3) Crear SSLContext con los TrustManagers
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);

        // 4) Crear socket TLS y comunicar
        SSLSocketFactory sf = ctx.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) sf.createSocket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            String message = "HOLA SEGURO (TLS) - Ejercicio 2";
            out.println(message); // envío línea

            String response = in.readLine(); // leo respuesta
            System.out.println("Cliente TLS envió: " + message);
            System.out.println("Cliente TLS recibió: " + response);
        }
    }
}

