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
	}

	@Override
	public void start() {
		start = new Date();
		executionLog = new ArrayList<CustomAction>();
	}

	@Override
	public void touche(final int logicId, final int stepId, final Color color, final long delay) {
		executionLog.add(new CustomAction(logicId, delay, stepId));
	}

	@Override
	public void stepTimeout() {
		//TODO: por ahora action vacia representa el timeout
		executionLog.add(new CustomAction(0,0,0));
	}

	@Override
	public void finish() {
		end = new Date();
		//debug
		classToFile("customLog");
	}

	private void classToFile(String fileName){
		buffer = new StringBuilder(512);
		buffer.append("Players Results\n");
		buffer.append("Started at: "+start.toString()+"\n");
		buffer.append("Finished at: "+end.toString()+"\n");
		buffer.append("Initial configuration:\n");
		buffer.append("    Total timeout: "+totalTimeout+"\n");
		buffer.append("Execution log:\n");
		for(CustomAction aux : executionLog){
			buffer.append("    "+aux.toString()+"\n");
		}
		buffer.append("EOF");
		super.bufferToFile(buffer,fileName);
	}
}

