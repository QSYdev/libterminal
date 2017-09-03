package libterminal.lib.results;

import libterminal.lib.routine.Color;
import libterminal.lib.routine.Routine;

import java.util.Date;
import java.util.ArrayList;

public class CustomResults extends Results{

	private final String TYPE = "Custom";
	private Date start;
	private Date end;
	private long totalTimeout; //TODO: implementar totalTimeout del lado de executor
	private ArrayList<CustomAction> executionLog;
	private final Routine routine;// no se usa pero se guarda junto a los resultados

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
		//TODO: esto seria en el caso de que se desea guardar
		super.classToJSON(this, "customResults");
	}
}

