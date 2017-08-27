package functional.executor;

import ar.com.qsy.src.app.executor.Executor;
import ar.com.qsy.src.patterns.observer.AsynchronousListener;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.EventListener;

public class ExecutorRunner implements EventListener {
	private final AsynchronousListener internalListener;
	private int commandRequest;
	private int executorDoneExecuting;
	private int executorStepTimeout;
	private Executor executor;

	public ExecutorRunner(Executor executor) {
		this.internalListener = new AsynchronousListener();
		this.commandRequest = 0;
		this.executorDoneExecuting = 0;
		this.executorStepTimeout = 0;
		this.executor = executor;
	}

	@Override
	public void receiveEvent(Event event) {
		switch (event.getEventType()) {
			case executorDoneExecuting:
				executor.stop();
				executorDoneExecuting++;
				break;
			case executorStepTimeout:
				executorStepTimeout++;
				break;
			case commandRequest:
				commandRequest++;
				break;
		default:
			break;
		}
	}

	public int getNumberOfRequestedCommands() { return this.commandRequest; }

	public int getNumberOfTimeouts() { return this.executorStepTimeout; }

	public int getNumberOfDoneExecuting() { return this.executorDoneExecuting; }

	public Event getEvent() throws InterruptedException {
		return internalListener.getEvent();
	}
}
