package services;

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class MainCarrera {
	static Client cliente;
	static URI uri;
	static WebTarget target;
	static int intAtletas;
	
	public static void main(String[] args) {
		if (args.length != 2) {
            System.out.println("Debes proporcionar exactamente 2 parámetros: la IP del servidor y el número de atletas (entre 1 y 16).");
            return;
        }
		
		try {
            intAtletas = Integer.parseInt(args[1]);
            if (intAtletas < 1 || intAtletas > 16) {
            	System.out.println("Error: El número de atletas no está en el rango permitido (1 a 16)");
                return;
            }
        } catch (NumberFormatException e) {
        	System.out.println("Error: El número de atletas no es un entero");
            return;
        }
		
		cliente = ClientBuilder.newClient();
		String uriFormat = "http://"+args[0]+"/apicienlisos/carrera100";
		uri = UriBuilder.fromUri(uriFormat).build();
		WebTarget target = cliente.target(uri);
		
		System.out.println(target.path("reinicio").queryParam("num", intAtletas).request(MediaType.TEXT_PLAIN).get(String.class));
		
		for(int i = 1; i<= intAtletas; i++) {
			Atleta atleta = new Atleta(i,args[0]);
			atleta.start();
		}
		
		System.out.println(target.path("resultados").request(MediaType.TEXT_PLAIN).get(String.class));
	}
}
