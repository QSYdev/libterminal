package libterminal.lib.results;

import libterminal.lib.routine.Color;

import java.util.Date;
import java.util.ArrayList;

public class PlayersResults extends Results{

	private StringBuilder buffer; //debug

	private Date start;
	private Date end;
	private ArrayList<PlayerAction> executionLog;
	private int numberOfNodes, totalSteps;
	private long stepTimeout, delay, maxExecTime;
	private boolean waitForAllPlayers, stopOnTimeout;
	private ArrayList<Color> playersAndColors;

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

		//debug
		buffer = new StringBuilder(256);
		buffer.append("Players Execution\n");
		buffer.append("Initial configuration:\n");
		buffer.append("   Number of nodes: "+numberOfNodes+"\n");
		buffer.append("   Wait for all players: "+waitForAllPlayers+"\n");
		buffer.append("   Timeout: "+stepTimeout+"\n");
		buffer.append("   Delay: "+delay+"\n");
		buffer.append("   Maximum execution time: "+maxExecTime+"\n");
		buffer.append("   Total steps: "+totalSteps+"\n");
		buffer.append("   Stop on timeout: "+stopOnTimeout+"\n");
		buffer.append("   Players and colors:\n");
		char i=0;
		while(i<playersAndColors.size()){
			buffer.append("      Player "+(i+1)+": "+playersAndColors.get(i).toString()+"\n");
			i++;
		}
	}

	@Override
	public void start() {
		start = new Date();
		executionLog = new ArrayList<PlayerAction>();
		//debug
		buffer.append("Started at: "+start.toString()+"\n");
	}

	@Override
	public void touche(final int logicId, final int stepId, final Color color, final long delay) {
		executionLog.add(new PlayerAction(logicId, delay, stepId, playersAndColors.indexOf(color)));

		//debug
		buffer.append("   Touche: \n");
		buffer.append("   Logic ID: "+logicId+"\n");
		buffer.append("   Player ID: "+playersAndColors.indexOf(color)+"\n");
		buffer.append("   Step Id: "+stepId+"\n");
		buffer.append("   Delay: "+delay+"\n");
	}

	@Override
	public void stepTimeout() {
		//TODO: por ahora action vacia representa el timeout
		executionLog.add(new PlayerAction(0,0,0,0));
		//debug
		buffer.append("   Step Timeout\n");
	}

	@Override
	public void finish() {
		end = new Date();
		//debug
		buffer.append("Finished at: "+end.toString()+"\n");
		super.bufferToFile(buffer, "test");
		classToFile("test2");
	}
	private void classToFile(String fileName){
		buffer = new StringBuilder(512);
		buffer.append("Players Results\n");
		buffer.append("Started at: "+start.toString()+"\n");
		buffer.append("Finished at: "+end.toString()+"\n");
		buffer.append("Initial configuration:\n");
		buffer.append("    Number of nodes: "+numberOfNodes+"\n");
		buffer.append("    Total steps: "+totalSteps+"\n");
		buffer.append("    Step timeout: "+stepTimeout+"\n");
		buffer.append("    Total timeout: "+maxExecTime+"\n");
		buffer.append("    Delay: "+delay+"\n");
		buffer.append("    Stop on timeout: "+stopOnTimeout+"\n");
		buffer.append("    Wait for all players: "+waitForAllPlayers+"\n");
		buffer.append("Players and colors:\n");
		int i=0;
		for(Color c : playersAndColors){
			buffer.append("    Player "+(i++)+": "+c.toString()+"\n");
		}
		buffer.append("Execution log:\n");
		for(PlayerAction aux : executionLog){
			buffer.append("    "+aux.toString()+"\n");
		}
		buffer.append("EOF");
		super.bufferToFile(buffer,fileName);
	}
}

