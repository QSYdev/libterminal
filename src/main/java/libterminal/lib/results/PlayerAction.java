package libterminal.lib.results;

public class PlayerAction {
	private int logicId;
	private long delay;
	private int stepId;
	private int playerId;

	public PlayerAction(final int logicId, final long delay, final int stepId, final int playerId){
		this.logicId = logicId;
		this.delay = delay;
		this.stepId = stepId;
		this.playerId = playerId;
	}
}
