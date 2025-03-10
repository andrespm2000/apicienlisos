package services;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ConcurrentHashMap;

@Path("carrera100")
@Singleton
public class Carrera100 {
	final int MAX_ATLETAS = 16;
	int numAtletas;
	int numAtletasPreparados = 0;
	int numAtletasListos = 0;
	int numAtletasLlegados = 0;
	long tiempoIni;
	String listaResultados = "";
	ConcurrentHashMap<Integer,Long> listaAtletas = new ConcurrentHashMap<Integer, Long>(MAX_ATLETAS);
	
	final Object resultadosLock = new Object();
	
	@Path("reinicio")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String reinicio(@QueryParam(value="num") int numAtletas) {
		this.listaAtletas.clear();
		this.numAtletas = numAtletas;
		numAtletasPreparados = 0;
		numAtletasListos = 0;
		numAtletasLlegados = 0;
		listaResultados = "";
		return "La carrera se ha reiniciado";
	}
	
	@Path("preparado")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public synchronized String preparado()  throws InterruptedException {
			this.numAtletasPreparados++;
			if(this.numAtletasPreparados < numAtletas) {
				this.wait();
			}else {
				this.notifyAll();
			}
		return "Preparado";

	}
	
	@Path("listo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public synchronized String listo() throws InterruptedException {
			this.numAtletasListos++;
			if(this.numAtletasListos < numAtletas) {
				this.wait();
			}else {
				tiempoIni = System.currentTimeMillis();
				this.notifyAll();
			}
		return "Listo";
	}
	
	@Path("llegada")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String llegada(@QueryParam(value="dorsal") int dorsal) {
		numAtletasLlegados++;
		long tiempoLlegada = System.currentTimeMillis();
		long tiempoTranscurrido = tiempoLlegada - tiempoIni;
		listaAtletas.put(dorsal, tiempoLlegada);
		
		if (numAtletasLlegados == numAtletas) {
			synchronized(resultadosLock) {resultadosLock.notifyAll();}
	    }
		
		return "El atleta "+dorsal+" ha llegado en "+ tiempoTranscurrido + " ms";
	}
	
	@Path("resultados")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String resultados() throws InterruptedException {
		synchronized(resultadosLock) {
			while (this.numAtletasLlegados < numAtletas) {
		        resultadosLock.wait();
		    }
			
			StringBuilder resultados = new StringBuilder();
		    resultados.append("=== Resultados de la Carrera ===\n\n");

		    listaAtletas.entrySet().stream()
	        .sorted((entry1, entry2) -> Long.compare(entry1.getValue(), entry2.getValue()))
	        .forEach(entry -> {
	            resultados.append("Atleta " + entry.getKey() + ": " 
	                + formatTime(entry.getValue()) + " (ms)\n");
	        });
		    return resultados.toString();
		}
	}
	
	private String formatTime(long tiempoMs) {
		long minutos = tiempoMs/60000;
		long segundos = (tiempoMs%60000)/1000;
		long milisegundos = tiempoMs%1000;
		String formato = String.format("%02d:%02d:%03d",minutos,segundos,milisegundos);
		return formato;
	}
}
