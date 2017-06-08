//package cl.citiaps.dashboard.utils;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;
//
//public class Comunas {
//	private Map<String, Comuna> listadoComunas;
//
//	public Comunas() {
//		this.setListadoComunas(new HashMap<String, Comuna>());
//	}
//
//	public void leerCiudades(String ruta) {
//
//		InputStream input;
//		try {
//			input = getClass().getResourceAsStream(ruta);
//			Reader reader = new InputStreamReader(input);
//
//			JSONParser jsonParser = new JSONParser();
//			JSONArray comunasJsonArray = (JSONArray) jsonParser.parse(reader);
//			for (Object comunaObject : comunasJsonArray) {
//				JSONObject comunaJson = (JSONObject) comunaObject;
//				String nombre = (String) comunaJson.get("NOM_COM");
//				Long codCom = (Long) comunaJson.get("COD_COMUNA");
//				Long codReg = (Long) comunaJson.get("COD_REG");
//
//				Comuna comuna = new Comuna(nombre, codCom.intValue(), codReg.intValue());
//				this.listadoComunas.put(nombre, comuna);
//			}
//
//		} catch (FileNotFoundException ex) {
//			ex.printStackTrace();
//		} catch (ParseException ex) {
//			ex.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public List<String> listadoNombres() {
//		return new ArrayList<String>(this.listadoComunas.keySet());
//	}
//
//	public int getCodComuna(String comuna) {
//		return this.listadoComunas.get(comuna).getCodComuna();
//	}
//
//	public int getCodRegion(String comuna) {
//		return this.listadoComunas.get(comuna).getCodRegion();
//	}
//
//	public Map<String, Comuna> getListadoComunas() {
//		return listadoComunas;
//	}
//
//	public void setListadoComunas(Map<String, Comuna> listadoComunas) {
//		this.listadoComunas = listadoComunas;
//	}
//
//	public class Comuna {
//		private String nombre;
//		private int codComuna;
//		private int codRegion;
//
//		public Comuna() {
//			super();
//			this.setNombre(null);
//			this.setCodComuna(0);
//			this.setCodRegion(0);
//		}
//
//		public Comuna(String nombre, int codComuna, int codRegion) {
//			super();
//			this.setNombre(nombre);
//			this.setCodComuna(codComuna);
//			this.setCodRegion(codRegion);
//		}
//
//		public String getNombre() {
//			return nombre;
//		}
//
//		public void setNombre(String nombre) {
//			this.nombre = nombre;
//		}
//
//		public int getCodComuna() {
//			return codComuna;
//		}
//
//		public void setCodComuna(int codComuna) {
//			this.codComuna = codComuna;
//		}
//
//		public int getCodRegion() {
//			return codRegion;
//		}
//
//		public void setCodRegion(int codRegion) {
//			this.codRegion = codRegion;
//		}
//
//	}
//
//}
