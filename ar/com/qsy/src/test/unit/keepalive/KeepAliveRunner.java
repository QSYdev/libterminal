package ar.com.qsy.src.test.unit.keepalive;

import ar.com.qsy.src.app.node.Node;
import ar.com.qsy.src.patterns.observer.Event;
import ar.com.qsy.src.patterns.observer.EventListener;

public class KeepAliveRunner implements EventListener{
	private int keepAliveErrorCountNode1;
	private int keepAliveErrorCountNode2;

	public KeepAliveRunner() {
		this.keepAliveErrorCountNode1 = 0;
		this.keepAliveErrorCountNode2 = 0;
	}

	@Override
	public void receiveEvent(Event event) throws Exception {
		Event.EventType eventType = event.getEventType();
		Node node = (Node) event.getContent();

		switch (eventType) {
			case keepAliveError:
				if(node.getNodeId() == 1) {
					keepAliveErrorCountNode1++;
				} else if(node.getNodeId() == 2)
					keepAliveErrorCountNode2++;
				break;
		}
	}

	public int getKeepAliveErrorCountNode1() {
		return this.keepAliveErrorCountNode1;
	}

	public int getKeepAliveErrorCountNode2() {
		return this.keepAliveErrorCountNode2;
	}
}