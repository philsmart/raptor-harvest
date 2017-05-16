package uk.ac.cardiff.raptor.harvest.enrich;

import java.util.List;

import uk.ac.cardiff.model.event.Event;

/**
 * Implementations of this class add/modify/remove values from {@link Event}
 * instances.
 * 
 * @author philsmart
 *
 */
public interface AttributeEnrichment {

	/**
	 * Enrich the {@link List} of {@link Event}s in whichever way the
	 * implementing class describes.
	 * 
	 * @param events
	 *            the {@link Event} {@link List} to enrich.
	 */
	void enrich(List<Event> events);

	/**
	 * Name used for display.
	 * 
	 * @return a friendly name used for display.
	 */
	String getName();

}
