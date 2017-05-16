package uk.ac.cardiff.raptor.harvest.parse;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;

public abstract class BaseLogFileParser implements LogParser {

	/**
	 * Default logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(BaseLogFileParser.class);

	protected String logfile;

	/**
	 * The time in ms since Unix EPOCH of the last event parsed. Used as an
	 * indicator of progress.
	 */
	protected long latestTimeSinceEpochParsed;

	/**
	 * A set of eventIds which have the same timestamp as the
	 * {@code latestTimeSinceEpochParesed}
	 */
	protected Set<Integer> latestEntries = new HashSet<Integer>();

	/**
	 * Parse the {@code logfile} assuming a format defined by the implementing
	 * class of the {@link #unmarshal(String)} method. Any new loglines are
	 * converted to an {@link Event} or sub type thereof and returned as a
	 * {@link Set} of {@link Event}s.
	 * 
	 * @return a nonnull but possibly empty {@link Set} of {@link Event}s.
	 */
	@Override
	@Nonnull
	public Set<Event> parse() {

		final Set<Event> newEvents = new HashSet<Event>();

		try (final Stream<String> stream = Files.lines(Paths.get(logfile), Charset.defaultCharset())) {

			for (final Iterator<String> it = stream.iterator(); it.hasNext();) {
				final String line = it.next();
				final Event authE = unmarshal(line);
				if (isNewEvent(authE)) {
					newEvents.add(authE);
				}

			}

			log.info("Shibboleth Parsers has parsed {} new events", newEvents.size());

		} catch (final Exception e) {
			e.printStackTrace();
		}
		return newEvents;
	}

	/**
	 * Unmarshalls a line {@link String} into a {@link Event} or specialisation
	 * thereof (delegated to implementing class).
	 * 
	 * @param line
	 *            the String that represents the {@link Event} could be null.
	 * @return an {@link Event} converted from the input {@code line}. Must not
	 *         be null, can effectively be empty.
	 */
	@Nonnull
	protected abstract Event unmarshal(@Nullable final String line);

	/**
	 * Updates the {@code latestTimeSinceEpochParsed} if the event is newer (by
	 * checking the {@link Event#getEventTimeMillis()}) than the existing value.
	 * Also maintains the {@code latestEntries}, such that any newer event
	 * clears the set and its itself to it, and event equal in time is added to
	 * the set.
	 * 
	 * @param e
	 *            the {@link Event} to base the
	 *            {@code latestTimeSinceEpochParsed} on.
	 */
	protected void updateLastParsedTime(@Nonnull final Event e) {
		Objects.requireNonNull(e, "Can not update last parsed event that is null, null events should not be generated");

		if (e.getEventTimeMillis() > latestTimeSinceEpochParsed) {
			latestTimeSinceEpochParsed = e.getEventTimeMillis();
			latestEntries.clear();
			latestEntries.add(e.getEventId());

		}
		if (e.getEventTimeMillis() == latestTimeSinceEpochParsed) {

			latestEntries.add(e.getEventId());

		}
	}

	/**
	 * Determines if the event is a new (unseen) event. Does so by checking
	 * firstly if the {@link Event#getEventTimeMillis()} >
	 * {@code latestTimeSinceEpochParsed}, or secondly if the
	 * {@link Event#getEventTimeMillis()} == {@code latestTimeSinceEpochParsed}
	 * and is not in the set {@code latestEntries}. Both evaluations call the
	 * {@link #updateLastParsedTime(Event)} method to progress the
	 * {@code latestTimeSinceEpochParsed} and {@code latestEntries} set.
	 * 
	 * @param authE
	 *            the {@link Event} to check.
	 * @return true if new and unseen, false otherwise.
	 */
	protected boolean isNewEvent(@Nonnull final Event authE) {
		Objects.requireNonNull(authE, "Can not add events that are null, null events should not be generated");

		if (authE.getEventTimeMillis() > latestTimeSinceEpochParsed) {
			log.trace("New event {}", authE);

			updateLastParsedTime(authE);
			return true;
		} else if (authE.getEventTimeMillis() == latestTimeSinceEpochParsed) {
			boolean isUnseen = true;
			if (latestEntries.contains(authE.getEventId())) {
				isUnseen = false;
			}
			if (isUnseen) {
				log.trace("New event {}", authE);
			}
			updateLastParsedTime(authE);
			return isUnseen;

		}
		return false;
	}

	public String getLogfile() {
		return logfile;
	}

	public void setLogfile(final String logfile) {
		this.logfile = logfile;
	}

}
