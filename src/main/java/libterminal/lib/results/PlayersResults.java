package libterminal.lib.results;

import libterminal.lib.routine.Color;

import java.util.Date;
import java.util.ArrayList;

public class PlayersResults extends Results{

	private final String TYPE = "Player";
	private Date start;
	private Date end;
	private int numberOfNodes, totalSteps;
	private long stepTimeout, delay, maxExecTime;
	private boolean waitForAllPlayers, stopOnTimeout;
	private ArrayList<Color> playersAndColors;
	private ArrayList<PlayerAction> executionLog;

	public PlayersResults(final int numberOfNodes, final ArrayList<Color> playersAndColors, final boolean waitForAllPlayers,
	                      final long stepTimeout, final long delay, final long maxExecTime, final int totalSteps,
	                      final boolean stopOnTimeout){
		this.numberOfNodes = numberOfNodes;
		this.playersAndColors = playersAndColors;
		this.waitForAllPlayers = waitForAllPlayers;
		this.stepTimeout = stepTimeout;
		this.delay = delay;
		this.maxExecTime = maxExecTime;
		this.totalSteps = totalSteps;
		this.stopOnTimeout = stopOnTimeout;

		this.start = null;
		this.end = null;
		this.executionLog = null;
	}

	@Override
	public void start() {
		start = new Date();
		executionLog = new ArrayList<PlayerAction>();
	}

	@Override
	public void touche(final int logicId, final int stepId, final Color color, final long delay) {
		executionLog.add(new PlayerAction(logicId, delay, stepId, playersAndColors.indexOf(color)));
	}

	@Override
	public void stepTimeout() {
		//TODO: por ahora action vacia representa el timeout
		executionLog.add(null);
	}

	@Override
	public void finish() {
		end = new Date();
		//TODO: esto seria en el caso de que se desea guardar
//		super.classToJSON(this,"jsonFile");
	}
}

