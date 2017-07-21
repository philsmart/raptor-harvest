package uk.ac.cardiff.raptor.harvest.batch;

import java.nio.file.Path;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.LogParserFactory;
import uk.ac.cardiff.raptor.harvest.parse.ShibbolethLogParser;

@ThreadSafe
public class ShibbolethIdPBatchLogFileParser implements BatchLogFileParser {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethIdPBatchLogFileParser.class);

	@Override
	public Set<Event> parse(final Path fileToParse) {
		final ShibbolethLogParser parser = LogParserFactory.newShibbolethLogParser();
		parser.setLogfile(fileToParse.toString());
		return parser.parse();

	}

}
