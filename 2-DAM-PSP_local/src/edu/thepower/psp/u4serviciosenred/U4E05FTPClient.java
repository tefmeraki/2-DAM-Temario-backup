package edu.thepower.psp.u4serviciosenred;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class U4E05FTPClient {

	public static void main(String[] args) {
		// 0. Servidor FTP temporal, https://sftpcloud.io/tools/free-ftp-server
		final String HOST = "eu-central-1.sftpcloud.io";
		final int PORT = 21;
		final String user = "8dfca5bdabfa4c8089e81fdb14701288";
		final String passwd = "XnpWgzXgpUZKrCbZ2EYqpoakFC0sFM4Y";

		FTPClient ftpCliente = new FTPClient();
		try {
			// 1. Conectarse al servidor
			ftpCliente.connect(HOST, PORT);
			System.out.println("Conectado con el servidor FTP");
			
			// 2. Acceder - Hacer login
			if (ftpCliente.login(user, passwd)) {
				System.out.println("Se ha accedido con usuario y password");
				
				// 3. Configurar: modo pasivo y tipo de archivo
				// Modo pasivo: cliente inicia conexiones de control y datos, para evitar problemas de firewalls con inicio de conexiones desde el servidor.
				ftpCliente.enterLocalPassiveMode();
				ftpCliente.setFileType(FTP.BINARY_FILE_TYPE);
				
				// 4. Subir archivo
				String archivoLocal = "./recursos/texto.txt";
				String archivoRemoto = "./texto.txt";
				boolean resultado;
				
				System.out.println("Subiendo archivo " + archivoLocal + " a servidor FTP...");
				try (InputStream is = new FileInputStream(archivoLocal)) {
					resultado = ftpCliente.storeFile(archivoRemoto, is);
					if (resultado)
						System.out.println("Se ha subido el archivo al servidor FTP.");
					else
						System.err.println("Error al subir el archivo al servidor.");
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// 5. Listar archivos
				FTPFile[] archivos = ftpCliente.listFiles();
				System.out.println("Volcado de contenidos del servidor FTP:");
				String nombre;
				for (int i = 0;i < archivos.length;i++) {
					nombre = new String();
					if (archivos[i].isDirectory())
						nombre += "[d] ";
					else
						nombre += i + ". ";
					nombre += archivos[i].getName();
					
					System.out.println(nombre);
				}
				
				// 6. Descarga archivo
				archivoLocal = "./recursos/textoD.txt";
				System.out.println("Descargando archivo " + archivoRemoto + " desde servidor FTP...");
				try (OutputStream os = new FileOutputStream(archivoLocal)) {
					resultado = ftpCliente.retrieveFile(archivoRemoto, os);
					if (resultado)
						System.out.println("Archivo descargado.");
					else
						System.err.println("Error en la descarga del archivo.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else
				System.err.println("Error al intentar acceder con usuario y password.");
		} catch (IOException e) {
			System.err.println("Error al accceder al servidor FTP: " + e.getMessage());
		} finally {
			if (ftpCliente.isConnected()) {
				try {
					ftpCliente.logout();
					ftpCliente.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
