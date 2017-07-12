package cl.citiaps.dashboard.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cl.citiaps.dashboard.eda.Message;
import cl.citiaps.dashboard.eda.Mision;

public class ClienteHTTP {
	
	public ClienteHTTP(){}

	public void sendPost(Message message) {
		try{
	        String url = "http://158.170.140.100:8080/message/";
	        URL obj = new URL(url);
	        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
	        //add reuqest header
	        con.setRequestMethod("POST");
	        con.setRequestProperty("User-Agent", "Mozilla/5.0");
	 
	 
	        // Send post request
	        con.setDoOutput(true);
	        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	        wr.writeBytes(message.toString());
	        wr.flush();
	        wr.close();
	 
	        int responseCode = con.getResponseCode();
	        System.out.println("\nSending 'POST' request to URL : " + url);
	        System.out.println("Post parameters : " + message.toString());
	        System.out.println("Response Code : " + responseCode);
	 
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	 
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	 
	        //print result
	        System.out.println(response.toString());
		}catch(MalformedURLException mue){
			System.out.println(mue);
		}catch(IOException ioe){
			System.out.println(ioe);
		}
	}
}
