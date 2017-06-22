package uk.ac.cardiff.raptor.harvest.batch;

import java.nio.file.Path;
import java.util.Set;

import uk.ac.cardiff.model.event.Event;

public interface BatchLogFileParser {

	/**
	 * Call to the concrete implementation to do the actual parsing. Of note the
	 * concrete implementation of this method should be thread safe, as it
	 * *could* be parallel streamed by the {@link #parsePush()} method in the
	 * future.
	 * 
	 * @param fileToParse
	 *            the {@link Path} of the file to parse
	 * @return a {@link Set} of {@link Event}s.
	 */
	Set<Event> parse(Path fileToParse);

}
