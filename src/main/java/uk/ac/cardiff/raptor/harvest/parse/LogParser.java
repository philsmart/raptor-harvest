package uk.ac.cardiff.raptor.harvest.parse;

import java.util.Set;

import javax.annotation.Nonnull;

import uk.ac.cardiff.model.event.Event;

@FunctionalInterface
public interface LogParser {

	/**
	 * Functional interface, called to execute the LogParser. A {@link Set} is
	 * returned, results are assumed unique. Implementation must only return
	 * *new* events that were not returned by the last call to this method.
	 * Should always return a Set, even if it is empty.
	 * 
	 * @return a nonnull {@link Set} of {@link Event}s
	 */
	@Nonnull
	Set<Event> parse();

	/**
	 * Name used for display. Is default, provides "Unknown LogParser" if no
	 * implementation provided.
	 * 
	 * @return a friendly name used for display.
	 */
	@Nonnull
	default String getName() {
		return "Unknown LogParser";
	}

}
