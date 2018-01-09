package uk.ac.cardiff.raptor.harvest.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;

public class EzproxyBatchLogFileParserTest {

	private static final Logger log = LoggerFactory.getLogger(EzproxyBatchLogFileParserTest.class);

	/**
	 * Just tests the batch parser output, does not test any pipelining of the
	 * output.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testParse() throws IOException {
		final BatchLogFileParserProcessor ezproxyProcessor = new BatchLogFileParserProcessor();
		ezproxyProcessor.setParser(new EzproxyBatchLogFileParser("@cardiff.ac.uk"));
		ezproxyProcessor.setBatchParserName("Ezproxy Batch File Parser");

		// Create a new PushPipeline that just pushes the event to a callback class
		// which checks the event results.

		ezproxyProcessor.setPipeline(events -> {
			// once processed, events are present here
			events.forEach(e -> log.debug("callback {}", e));
			assertThat(events).hasSize(1);
			final Event event = events.iterator().next();
			assertThat(event).isInstanceOf(EzproxyAuthenticationEvent.class);
			final EzproxyAuthenticationEvent ezEvent = (EzproxyAuthenticationEvent) event;
			assertThat(ezEvent.getPrincipalName()).isEqualTo("A2211362R");
			assertThat(ezEvent.getResourceId()).isEqualTo("http://www.sciencedirect.com");
			assertThat(ezEvent.getServiceHost()).isEqualTo("https://abc.cardiff.ac.uk");
		});

		// create tmp file

		final Path tmpBatchDir = Files.createTempDirectory("batch-ezproxy-tmp-dir");

		final File tmpLogFile = File.createTempFile("ezproxy-test-batch-logfile", ".log", tmpBatchDir.toFile());

		log.debug("Temporary log file is {}", tmpLogFile);

		// populate with a know line
		Files.write(Paths.get(tmpLogFile.toURI()),
				"31.205.175.2 JOt2SNph8RJRtkb A2211362R@cardiff.ac.uk [23/Jun/2017:00:04:42 +0000] \"GET https://abc.cardiff.ac.uk:443/connect?session=sJOt2SNph8RJRtkb&qurl=http%3a%2f%2fwww.sciencedirect.com%2fscience%2farticle%2fpii%2fS1618866711000963 HTTP/1.1\" 302 0"
						.getBytes(StandardCharsets.UTF_8));

		ezproxyProcessor.setBatchDirectory(tmpLogFile.getParent().toString());

		// parse result
		ezproxyProcessor.parsePush();

		assertThat(tmpLogFile.exists()).isFalse();
		final File doneFile = new File(tmpLogFile.toString() + ".done");
		log.debug("Done file is {}", doneFile);
		assertThat(doneFile.exists()).isTrue();

	}

}
