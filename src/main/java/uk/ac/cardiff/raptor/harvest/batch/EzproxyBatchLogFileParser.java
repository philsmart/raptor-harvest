package uk.ac.cardiff.raptor.harvest.batch;

import java.nio.file.Path;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.EzproxyLogFileParser;
import uk.ac.cardiff.raptor.harvest.parse.LogParserFactory;

@ThreadSafe
public class EzproxyBatchLogFileParser implements BatchLogFileParser {

	@Override
	public Set<Event> parse(final Path fileToParse) {
		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser();
		parser.setLogfile(fileToParse.toString());
		return parser.parse();
	}

}
