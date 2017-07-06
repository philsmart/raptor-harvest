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

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilterEngine;

public abstract class BaseLogFileParser implements LogParser {

	/**
	 * Default logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(BaseLogFileParser.class);

	/**
	 * UNC Path to the logfile. If appended with DATE, todays date in format X
	 * replaces it.
	 */
	protected String logfile;

	/**
	 * if the {@code logfile} has the string DATE in it, todays date in the
	 * format specified by {@code logFileNameDateFormat} replaces it.
	 */
	private String logfileNameDateFormat;

	/**
	 * The time in ms since Unix EPOCH of the last event parsed. Used as an
	 * indicator of progress.
	 */
	protected long latestTimeSinceEpochParsed;

	/**
	 * Engine that determines (if configured) if the line is suitable for
	 * unmarshalling.
	 */
	private LineFilterEngine lineFilter;

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

		final String logfileName = construct();

		try (final Stream<String> stream = Files.lines(Paths.get(logfileName), Charset.defaultCharset())) {

			for (final Iterator<String> it = stream.iterator(); it.hasNext();) {
				final String line = it.next();

				boolean parseLine = true;
				if (lineFilter != null) {

					parseLine = lineFilter.isParsableLine(line);
				}
				log.trace("Parse {}: {}", parseLine, line);
				if (parseLine) {
					final Event authE = unmarshal(line);

					if (authE.getEventId() != 0 && isNewEvent(authE)) {
						newEvents.add(authE);
					}
				}

			}

			log.info("Parser has parsed {} new events", newEvents.size());

		} catch (final Exception e) {
			log.error("Error parsing log file", e);

		}
		return newEvents;
	}

	private String construct() {
		if (logfile != null && logfile.contains("DATE") && logfileNameDateFormat != null) {
			log.info("Constructing logfile name with DATE, using format {}", logfileNameDateFormat);
			final DateTime now = DateTime.now();
			try {
				final String dateString = now.toString(logfileNameDateFormat);
				return logfile.replace("DATE", dateString);
			} catch (final IllegalArgumentException e) {
				log.error("Could not construct logfile name from [{},using date {}]", logfile, logfileNameDateFormat,
						e);
				return logfile;
			}
		} else {
			return logfile;
		}
	}

	/**
	 * Unmarshalls a line {@link String} into a {@link Event} or specialisation
	 * thereof (delegated to implementing class). Any event that is returned
	 * must include an eventID otherwise it is omitted - this can be used to
	 * exclude certain events if they are deemed parsable by the
	 * {@link LineFilterEngine} but not by the concrete parsers.
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

	/**
	 * @return the lineFilter
	 */
	public LineFilterEngine getLineFilter() {
		return lineFilter;
	}

	/**
	 * @param lineFilter
	 *            the lineFilter to set
	 */
	public void setLineFilter(final LineFilterEngine lineFilter) {
		this.lineFilter = lineFilter;
	}

	/**
	 * @return the logFileNameDateFormat
	 */
	public String getLogfileNameDateFormat() {
		return logfileNameDateFormat;
	}

	/**
	 * @param logFileNameDateFormat
	 *            the logFileNameDateFormat to set
	 */
	public void setLogfileNameDateFormat(final String logfileNameDateFormat) {
		this.logfileNameDateFormat = logfileNameDateFormat;
	}

}
