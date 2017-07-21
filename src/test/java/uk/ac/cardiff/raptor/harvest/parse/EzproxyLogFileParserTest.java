package uk.ac.cardiff.raptor.harvest.parse;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;

public class EzproxyLogFileParserTest {

	private static final Logger log = LoggerFactory.getLogger(EzproxyLogFileParserTest.class);

	/**
	 * Should not parse this line
	 * 
	 * @throws IOException
	 */
	@Test
	public void testOmittParse() throws IOException {
		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser();

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
		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser();

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
		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser();

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
		assertThat(ezevent.getServiceHost()).isEqualTo("https://abc.cardiff.ac.uk");

		// delete file on exit
		tmpLogFile.toFile().delete();

	}

	@Test
	public void testLineSplit() {

		final String lineOne = "31.205.0.202 Il2OF3fZnx43vl7 A2200011A@cardiff.ac.uk [17/Jun/2017:02:06:37 +0000] \"GET http://abc.cardiff.ac.uk:80/login?url=https://search.proquest.com/docview/1325668243?rfr_id=info%3Axri%2Fsid%3Aprimo HTTP/1.1\" 302 0";
		final String lineTwo = "86.3.201.124 - - [17/Jun/2017:00:01:09 +0000] \"GET http://abc.cardiff.ac.uk:80/login?url=http://spr.sagepub.com/content/24/6/819.full.pdf+html HTTP/1.1\" 200 0";
		final String lineThree = "86.151.184.129 1j4pAbFgBhzDNo8 A1891053M@cardiff.ac.uk [17/Jun/2017:00:12:01 +0000] \"GET https://abc.cardiff.ac.uk:443/connect?session=s1j4pAbFgBhzDNo8&qurl=http%3a%2f%2fwww.sciencedirect.com%2fscience%2farticle%2fpii%2fS0039914004003686 HTTP/1.1\" 302 0";

		final String[] splitLine = lineOne.split(" ", 7);
		Arrays.stream(splitLine).forEach(l -> log.debug("one [{}]", l));

		final String[] splitLineTwo = lineTwo.split(" ", 7);
		Arrays.stream(splitLineTwo).forEach(l -> log.debug("two [{}]", l));

		final String[] splitLineThree = lineThree.split(" ", 7);
		Arrays.stream(splitLineThree).forEach(l -> log.debug("three [{}]", l));
	}

	@Test
	public void testParseThird() throws IOException {
		final EzproxyLogFileParser parser = LogParserFactory.newEzproxyLogFileParser();

		// create tmp file
		final Path tmpLogFile = Files.createTempFile("ezproxy-test-logfile", "log");

		// populate with a know line
		Files.write(tmpLogFile,
				"31.205.0.202 Il2OF3fZnx43vl7 A2200011A@cardiff.ac.uk [17/Jun/2017:02:06:37 +0000] \"GET http://abc.cardiff.ac.uk:80/login?url=https://search.proquest.com/docview/1325668243?rfr_id=info%3Axri%2Fsid%3Aprimo HTTP/1.1\" 302 0"
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
		assertThat(ezevent.getEventTimeMillis()).isEqualTo(1497665197000L);

		assertThat(ezevent.getResourceId()).isEqualTo("https://search.proquest.com");
		assertThat(ezevent.getServiceHost()).isEqualTo("http://abc.cardiff.ac.uk");

		// delete file on exit
		tmpLogFile.toFile().delete();

	}

}
