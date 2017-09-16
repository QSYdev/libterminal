package libterminal.patterns.visitor;

import libterminal.patterns.observer.Event.CommandIssuedEvent;
import libterminal.patterns.observer.Event.RoutineFinishedEvent;
import libterminal.patterns.observer.Event.RoutineStartedEvent;
import libterminal.patterns.observer.Event.ToucheReceivedEvent;

public abstract class ExternaleventHandler {

	public void handle(final RoutineStartedEvent routineStartedEvent) {
	}

	public void handle(final RoutineFinishedEvent routineFinishedEvent) {
	}

	public void handle(final ToucheReceivedEvent toucheReceivedEvent) {
	}

	public void handle(final CommandIssuedEvent commandIssuedEvent) {
	}

}
