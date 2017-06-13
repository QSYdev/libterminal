package ar.com.qsy.model.patterns.observer;

public abstract class EventArgs {

	private final int event;
	private final Object[] args;

	public EventArgs(final int event, final Object[] args) {
		this.event = event;
		this.args = args;
	}

	public int getEvent() {
		return event;
	}

	public Object getArg(final int index) {
		return args[index];
	}

}
