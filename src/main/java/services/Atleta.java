package services;

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;


public class Atleta extends Thread {
	int dorsal;
	String ipServidor;
	Client clienteAtleta;
	URI uriServicio;
	WebTarget targetAtleta;
	
	public Atleta(int dorsal, String ipServidor) {
		this.dorsal = dorsal;
		this.ipServidor = ipServidor;
		clienteAtleta = ClientBuilder.newClient();
		uriServicio = UriBuilder.fromUri("http://"+this.ipServidor+"/apicienlisos/carrera100").build();
		targetAtleta = clienteAtleta.target(uriServicio);
	}
	
	public void run() {
    	try {
    			System.out.println(targetAtleta.path("preparado").request(MediaType.TEXT_PLAIN).get(String.class));
    			
    			System.out.println(targetAtleta.path("listo").request(MediaType.TEXT_PLAIN).get(String.class));
    			
                int tiempoCarrera = (int) (Math.random() * 2000) + 9000;
                Thread.sleep(tiempoCarrera);
                
                System.out.println(targetAtleta.path("llegada").queryParam("dorsal", dorsal).request(MediaType.TEXT_PLAIN).get(String.class));
    			
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
