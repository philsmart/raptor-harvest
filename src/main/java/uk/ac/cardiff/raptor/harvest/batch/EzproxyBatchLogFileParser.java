package uk.ac.cardiff.raptor.harvest.batch;

import java.nio.file.Path;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.EzproxyLogFileParser;
import uk.ac.cardiff.raptor.harvest.parse.LogParserFactory;

@ThreadSafe
public class EzproxyBatchLogFileParser implements BatchLogFileParser {

	private static final Logger log = LoggerFactory.getLogger(EzproxyBatchLogFileParser.class);

	private String principalScope;

	/**
	 * Create a new instance of this class.
	 * 
	 * @param principalScope
	 *            used on each {@link EzproxyLogFileParser} creation to set the
	 *            {@link EzproxyLogFileParser#setPrincipalScope(String)}. Can be
	 *            null;
	 */
	public EzproxyBatchLogFileParser(@Nullable final String principalScope) {

		log.debug("Setting ezproxy batch file parser principal scope to [{}]", principalScope);
		this.principalScope = principalScope;
	}

	/**
	 * Creates a new instance of this class, used if {@code principalScope} should
	 * not be set.
	 */
	public EzproxyBatchLogFileParser() {

	}

	@Override
	public Set<Event> parse(final Path fileToParse) {
		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser(principalScope);
		parser.setLogfile(fileToParse.toString());
		return parser.parse();
	}

}
