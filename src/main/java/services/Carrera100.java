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
		return "Reiniciado";
	}
	
	@Path("preparado")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String preparado()  throws InterruptedException {
		synchronized(this.getClass()) {
			this.numAtletasPreparados++;
			if(this.numAtletasPreparados < numAtletas) {
				this.wait();
			}else {
				this.notifyAll();
			}
		}
		return "Preparados todos";
	}
	
	@Path("listo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String listo() throws InterruptedException {
		synchronized(this.getClass()) {
			this.numAtletasListos++;
			if(this.numAtletasListos < numAtletas) {
				this.wait();
			}else {
				tiempoIni = System.currentTimeMillis();
				this.notifyAll();
			}
		}
		return "Listos todos";
	}
	
	@Path("llegada")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String llegada(@QueryParam(value="dorsal") int dorsal) {
		numAtletasLlegados++;
		long tiempoLlegada = System.currentTimeMillis();
		listaAtletas.put(dorsal, tiempoLlegada);
		return "El atleta "+dorsal+" ha llegado en "+(tiempoLlegada - tiempoIni);
	}
	
	@Path("resultados")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String resultados() throws InterruptedException {
		synchronized(this.getClass()) {
			if(this.numAtletasLlegados < numAtletas) {
				this.wait();
			}else {
				this.notifyAll();
			}
		}
		listaAtletas.forEach((k,v) -> listaResultados = listaResultados.concat(k.toString()+": "+v.toString()+"\n"));
		return listaResultados;
	}
}
