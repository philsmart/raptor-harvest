package uk.ac.cardiff.raptor.harvest.batch;

import java.util.Set;

import javax.annotation.Nonnull;

import uk.ac.cardiff.model.event.Event;

public interface BatchLogFileParser {

	/**
	 * Pares all events from all logfiles from the configured directory (see
	 * {@link BaseBatchLogFileParser}
	 * 
	 * @return
	 */
	@Nonnull
	Set<Event> parse();

}
