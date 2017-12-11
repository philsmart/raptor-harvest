package uk.ac.cardiff.raptor.harvest.batch;

import java.nio.file.Path;
import java.util.Set;

import uk.ac.cardiff.model.event.Event;

public interface BatchLogFileParser {

	/**
	 * <p>
	 * Method that parses {@link Event} messages from the {@code fileToParse} and
	 * returns them as a {@link Set}.
	 * </p>
	 * <p>
	 * It is left to the concrete implementation what constitutes an {@link Event},
	 * and hence what concrete {@link Event} subclass should be returned.
	 * </p>
	 * <p>
	 * Of note the concrete implementation of this method should be thread safe, as
	 * it *could* be parallel streamed by the calling {@link #parsePush()} method in
	 * the future.
	 * </p>
	 * 
	 * @param fileToParse
	 *            the {@link Path} of the file to parse
	 * @return a {@link Set} of {@link Event}s.
	 */
	Set<Event> parse(Path fileToParse);

}
