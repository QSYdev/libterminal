package libterminal.lib.results;

public class CustomAction {
	private int logicId;
	private long delay;
	private int stepId;

	public CustomAction(final int logicId, final long delay, final int stepId){
		this.logicId = logicId;
		this.delay = delay;
		this.stepId = stepId;
	}

	public String toString(){
		return "Step: "+stepId+" / Logic Id: "+logicId+" / Delay: "+delay;
	}
}
