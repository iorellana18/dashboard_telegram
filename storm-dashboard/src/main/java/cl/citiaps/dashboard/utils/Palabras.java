package cl.citiaps.dashboard.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Palabras {
	public boolean contiene(List<String> palabras, String texto) {
		for (String palabra : palabras) {
			String patron = ".*\\b" + palabra + "\\b.*";
			if (texto.toLowerCase().matches(patron.toLowerCase()))
				return true;
		}
		return false;
	}

	public String eliminar(List<String> palabras, String texto) {

		for (String palabra : palabras) {
			String patron = "\\b" + palabra + "\\b";
			texto = texto.replaceAll(patron, "");
		}

		return texto.trim();
	}

	public List<String> palabraContenidas(List<String> palabras, String texto) {
		List<String> palabrasCont = new ArrayList<String>();
		for (String palabra : palabras) {
			String patron = ".*\\b" + palabra + "\\b.*";
			if (texto.toLowerCase().matches(patron))
				palabrasCont.add(palabra);
		}
		return palabrasCont;
	}

	public int numPalabrasContenidas(List<String> palabras, String texto) {
		int cont = 0;
		for (String palabra : palabras) {
			String patron = ".*\\b" + palabra + "\\b.*";
			if (texto.toLowerCase().matches(patron))
				cont++;
		}
		return cont;
	}

	public Stack<String> leerDiccionario(String ruta) {
		Stack<String> palabras = new Stack<String>();

		String linea = null;
		try {
			File file = new File(ruta);
			InputStream input = new FileInputStream(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));

			while ((linea = bufferedReader.readLine()) != null) {
				palabras.add(linea);
			}

			bufferedReader.close();
			input.close();
		} catch (FileNotFoundException ex) {
			System.out.println("No se puede abrir el archivo '" + ruta + "'");
		} catch (IOException ex) {
			System.out.println("Error al leer el archivo '" + ruta + "'");
		}

		return palabras;
	}
}
