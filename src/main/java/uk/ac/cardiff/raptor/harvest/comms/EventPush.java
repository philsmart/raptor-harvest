package uk.ac.cardiff.raptor.harvest.comms;

import java.util.List;

import uk.ac.cardiff.model.event.Event;

/**
 * Push {@link Event}s in some manor (left to the implementation) to a server.
 * 
 * @author philsmart
 *
 */
public interface EventPush {

	public void push(List<Event> events);

}
