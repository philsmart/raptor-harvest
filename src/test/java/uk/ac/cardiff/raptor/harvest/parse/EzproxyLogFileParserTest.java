package uk.ac.cardiff.raptor.harvest.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;
import uk.ac.cardiff.raptor.harvest.parse.filter.ExcludeLineFilter;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilter;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilterEngine;
import uk.ac.cardiff.raptor.harvest.parse.filter.RegexContainsLineFilter;

public class EzproxyLogFileParserTest {

	private static final Logger log = LoggerFactory.getLogger(EzproxyLogFileParserTest.class);

	public EzproxyLogFileParser ezproxyParser() {
		log.info("Creating Ezproxy Log File Parser");

		final LineFilterEngine lfe = new LineFilterEngine();
		final RegexContainsLineFilter rlf = new RegexContainsLineFilter();
		rlf.setIncludeIfContains("GET[\\p{Z}\\s][^\\p{Z}\\s]*login\\?url");
		final RegexContainsLineFilter rlfTwo = new RegexContainsLineFilter();
		rlfTwo.setIncludeIfContains("GET[\\p{Z}\\s][^\\p{Z}\\s]*connect\\?session");

		lfe.setIncludeLineFilters(new LineFilter[] { rlf, rlfTwo });

		final ExcludeLineFilter excludeLineFilter = new ExcludeLineFilter();
		excludeLineFilter.setExcludeIfContains("url=menu");

		lfe.setExcludeLineFilters(new LineFilter[] { excludeLineFilter });

		final EzproxyLogFileParser ezproxyParser = new EzproxyLogFileParser();
		ezproxyParser.setLineFilter(lfe);

		return ezproxyParser;
	}

	/**
	 * Should not parse this line
	 * 
	 * @throws IOException
	 */
	@Test
	public void testOmittParse() throws IOException {
		final EzproxyLogFileParser parser = ezproxyParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("ezproxy-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"80.229.254.181 7sYlKmc5o4E6wu5 A16593573@cardiff.ac.uk [23/Jun/2017:00:01:32 +0000] \"GET http://www.sciencedirect.com:80/science/article/pii/S0377027313002187?"
						.getBytes(StandardCharsets.UTF_8));

		// parse result
		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();

		// check against known result.
		log.info("Has parsed {} events", events.size());
		assertThat(events).hasSize(0);

		// delete file on exit
		tmpLogFile.toFile().delete();

	}

	@Test
	public void testParse() throws IOException {
		final EzproxyLogFileParser parser = new EzproxyLogFileParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("ezproxy-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"31.205.175.2 JOt2SNph8RJRtkb A2211362R@cardiff.ac.uk [23/Jun/2017:00:04:42 +0000] \"GET https://abc.cardiff.ac.uk:443/connect?session=sJOt2SNph8RJRtkb&qurl=http%3a%2f%2fwww.sciencedirect.com%2fscience%2farticle%2fpii%2fS1618866711000963 HTTP/1.1\" 302 0"
						.getBytes(StandardCharsets.UTF_8));

		// parse result
		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();

		// check against known result.
		events.forEach(e -> log.debug("Has Parsed {}", e));
		assertThat(events).hasSize(1);

		final Event event = events.iterator().next();
		assertThat(event).isInstanceOf(EzproxyAuthenticationEvent.class);

		final EzproxyAuthenticationEvent ezevent = (EzproxyAuthenticationEvent) event;
		assertThat(ezevent.getEventTimeMillis()).isEqualTo(1498176282000l);

		assertThat(ezevent.getResourceId()).isEqualTo("http://www.sciencedirect.com");

		// delete file on exit
		tmpLogFile.toFile().delete();

	}

	@Test
	public void testParseSecond() throws IOException {
		final EzproxyLogFileParser parser = new EzproxyLogFileParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("ezproxy-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"86.3.128.35 A9A2f94wYoqY0Tq A1969668V@cardiff.ac.uk [23/Jun/2017:00:09:55 +0000] \"GET https://abc.cardiff.ac.uk:443/connect?session=sA9A2f94wYoqY0Tq&qurl=https%3a%2f%2flink.springer.com%2farticle%2f10.1007%2fs00122-014-2371-2 HTTP/1.1\" 302 0"
						.getBytes(StandardCharsets.UTF_8));

		// parse result
		parser.setLogfile(tmpLogFile.toString());
		final Set<Event> events = parser.parse();

		// check against known result.
		events.forEach(e -> log.debug("Has Parsed {}", e));
		assertThat(events).hasSize(1);

		final Event event = events.iterator().next();
		assertThat(event).isInstanceOf(EzproxyAuthenticationEvent.class);

		final EzproxyAuthenticationEvent ezevent = (EzproxyAuthenticationEvent) event;
		assertThat(ezevent.getEventTimeMillis()).isEqualTo(1498176595000l);

		assertThat(ezevent.getResourceId()).isEqualTo("https://link.springer.com");

		// delete file on exit
		tmpLogFile.toFile().delete();

	}

}
