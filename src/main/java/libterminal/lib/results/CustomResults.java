package ar.com.qsy.src.app.results;

import ar.com.qsy.src.app.routine.Color;
import ar.com.qsy.src.app.routine.Routine;

public class CustomResults extends Results {
	private StringBuilder buffer;
	private Routine routine;

	public CustomResults(Routine routine){
		buffer = new StringBuilder(64);
		buffer.append("Custom Execution\n");
		buffer.append("Routine ID: "+/* TODO: ID de la rutina */"\n");
		this.routine=routine; // Para que despues se guarde junto con los resultados
	}

	@Override
	public void start(){
		buffer.append("Execution Started\n");
	}

	@Override
	public void touche(final int logicID, final Color color, final long delay){
		buffer.append("   Touche: \n");
		buffer.append("   Logic Color: "+logicID+"\n");
		buffer.append("   Delay: "+delay+"\n");
	}

	@Override
	public void stepTimeout(){
		buffer.append("   Step Timeout\n");
	}

	@Override
	public void finish(){
		//TODO evento de escribir el archivo y recibir el nombre de como guardarlo o manejarlo de alguna forma automatica el
		buffer.append("Execution finished");
		super.bufferToFile(buffer, "test");
	}
}
