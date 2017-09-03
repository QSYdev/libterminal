package libterminal.lib.results;

import libterminal.lib.routine.Color;
import libterminal.lib.routine.Routine;

import java.util.Date;
import java.util.ArrayList;

public class CustomResults extends Results{

	private StringBuilder buffer; //debug

	private final Routine routine;
	//TODO: implementar totalTimeout del lado de executor
	private long totalTimeout;
	private Date start;
	private Date end;
	private ArrayList<CustomAction> executionLog;

	public CustomResults(final Routine routine, final long totalTimeout){
		this.routine = routine;
		this.totalTimeout = totalTimeout;
		this.start = null;
		this.end = null;
		this.executionLog = null;
		//debug
		buffer = new StringBuilder(64);
		buffer.append("Custom Execution\n");
		buffer.append("Routine ID: "+/* TODO: ID de la rutina */"\n");
		buffer.append("Total Timeout: "+totalTimeout+"\n");
	}


	@Override
	public void start() {
		start = new Date();
		executionLog = new ArrayList<CustomAction>();
		//debug
		buffer.append("Started at: "+start.toString()+"\n");
	}

	@Override
	public void touche(final int logicId, final int stepId, final Color color, final long delay) {
		executionLog.add(new CustomAction(logicId, delay, stepId));
		//debug
		buffer.append("   Touche: \n");
		buffer.append("   Step Id: "+stepId+"\n");
		buffer.append("   Logic Id: "+logicId+"\n");
		buffer.append("   Delay: "+delay+"\n");
	}

	@Override
	public void stepTimeout() {
		//TODO: por ahora action vacia representa el timeout
		executionLog.add(new CustomAction(0,0,0));
		//debug
		buffer.append("   Step Timeout\n");
	}

	@Override
	public void finish() {
		end = new Date();
		//debug
		buffer.append("Finished at: "+end.toString()+"\n");
		super.bufferToFile(buffer, "test");
	}
}

