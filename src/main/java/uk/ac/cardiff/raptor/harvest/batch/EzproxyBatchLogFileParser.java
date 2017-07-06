package uk.ac.cardiff.raptor.harvest.batch;

import java.nio.file.Path;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.parse.EzproxyLogFileParser;

@ThreadSafe
public class EzproxyBatchLogFileParser implements BatchLogFileParser {

	@Override
	public Set<Event> parse(final Path fileToParse) {
		final EzproxyLogFileParser parser = new EzproxyLogFileParser();
		parser.setLogfile(fileToParse.toString());
		return parser.parse();
	}

}
