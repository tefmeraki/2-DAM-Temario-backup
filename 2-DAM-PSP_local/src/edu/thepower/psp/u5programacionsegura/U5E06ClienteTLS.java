package edu.thepower.psp.u5programacionsegura;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

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

    private static final String TRUSTSTORE_FILE = "./recursos/cliente-truststore.jks";
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
            
            // Recuperamos sesión para obtener información sobre protcolo TLS
            SSLSession session = socket.getSession();
            System.out.println("= INFO CRIPTO CLIENTE =");
            System.out.println("Protocolo TLS negociado: " + session.getProtocol());
            System.out.println("Suite de cifrado negociada: " + session.getCipherSuite());
            
            Certificate[]  certs = session.getPeerCertificates();
            if (certs != null && certs.length > 0 && certs[0] instanceof X509Certificate x509) {
            	// X509Certificate x509 = (X509Certificate) certs[0]; -> equivalente a crear la variable x509 con el instanceof (Java 16)
            	System.out.println("Subject (CN,...): " + x509.getSubjectX500Principal());
            	System.out.println("Emisor: " + x509.getIssuerX500Principal());
            	System.out.println("Válido desde: " + x509.getNotBefore());
            	System.out.println("Válido hasta: " + x509.getNotAfter());
            	System.out.println("Clave pública: " + x509.getPublicKey());
            }

            String response = in.readLine(); // leo respuesta
            System.out.println("Cliente TLS envió: " + message);
            System.out.println("Cliente TLS recibió: " + response);
        }
    }
}

