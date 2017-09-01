package libterminal.lib.results;

import libterminal.lib.routine.Color;

import java.util.ArrayList;

public class PlayersResults extends Results {
	private final StringBuilder buffer;
	private ArrayList<Color> playersAndColors;

	public PlayersResults(final int numberOfNodes, final ArrayList<Color> playersAndColors, final boolean waitForAllPlayers,
	                      final long timeOut, final long delay, final long maxExecTime, final int totalStep,
	                      final boolean stopOnTimeout){

		this.playersAndColors = playersAndColors;
		buffer = new StringBuilder(256);
		buffer.append("Players Execution\n");
		buffer.append("Initial configuration:\n");
		buffer.append("   Number of nodes: "+numberOfNodes+"\n");
		buffer.append("   Wait for all players: "+waitForAllPlayers+"\n");
		buffer.append("   Timeout: "+timeOut+"\n");
		buffer.append("   Delay: "+delay+"\n");
		buffer.append("   Maximum execution time: "+maxExecTime+"\n");
		buffer.append("   Total steps: "+totalStep+"\n");
		buffer.append("   Stop on timeout: "+stopOnTimeout+"\n");
		buffer.append("   Players and colors:\n");
		char i=0;
		while(i<playersAndColors.size()){
			buffer.append("      Player "+(i+1)+": "+playersAndColors.get(i).toString()+"\n");
			i++;
		}
	}

	@Override
	public void start(){
		buffer.append("Execution Started\n");
	}

	@Override
	public void touche(final int logicID, final Color color, final long delay){
		buffer.append("   Touche: \n");
		buffer.append("   Logic ID: "+logicID+"\n");
		buffer.append("   Player ID: "+playersAndColors.indexOf(color)+"\n");
		buffer.append("   Delay: "+delay+"\n");
	}

	@Override
	public void stepTimeout(){
		buffer.append("   Step Timeout\n");
	}

	@Override
	public void finish(){
		//TODO evento de escribir el archivo y recibir el nombre de como guardarlo o manejarlo de alguna forma automatica el nombramiento
		buffer.append("Execution finished");
		super.bufferToFile(buffer,"test");
	}
}
