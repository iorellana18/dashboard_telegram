package cl.citiaps.dashboard.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cl.citiaps.dashboard.eda.Message;

public class ClienteHTTP {
	private static final Logger logger = LoggerFactory.getLogger(ClienteHTTP.class);

	private String ip;
	private int port;

	public ClienteHTTP(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void sendPost(Message message) {
		
		try {
			String url = "http://" + this.ip + ":" + this.port + "/message/";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Add Request Header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			// Send POST Request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(message.toString());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			logger.info("Sending 'POST' request to URL : {}", url);
			logger.info("Post parameters : {}", message.toString());
			logger.info("Response Code : {}", responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			System.out.println(response.toString());
		} catch (MalformedURLException mue) {
			System.out.println(mue);
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}
}
