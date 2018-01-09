package uk.ac.cardiff.raptor.harvest;

import java.util.List;

import uk.ac.cardiff.model.event.Event;

/**
 * A functional interface for definning a push pipeline for lists of
 * {@link Event}s. A push pipeline may mutate one ore more {@link Event}s in the
 * input list. The pipeline should then send the List to some Listening endpoint
 * -but is not guaranteed to.
 * 
 * @author philsmart
 *
 */
// annotation is only for compiler warnings if more than one method specified,
// otherwise with only one method, it will function as a functional interface
// anyway
@FunctionalInterface
public interface PushPipeline {

	/**
	 * <p>
	 * Push the {@link List} of {@link Event}s to some listening endpoint via a
	 * defined set of integration stages (pipeline).
	 * </p>
	 * 
	 * <p>
	 * There is no requirement that any intermediate processing stages are defined,
	 * and the endpoint is not strictly defined.
	 * </p>
	 * 
	 * @param events
	 *            the {@link Event}s to process and push to some endpoint.
	 */
	void pushPipeline(List<Event> events);

}