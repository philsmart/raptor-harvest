package uk.ac.cardiff.raptor.harvest.batch;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.PushPipeline;

/**
 * 
 * 
 * @author philsmart
 *
 */
public class BatchLogFileParserProcessor {

	private static final Logger log = LoggerFactory.getLogger(BatchLogFileParser.class);

	private String batchDirectory;

	private String batchParserName;

	private BatchLogFileParser parser;

	@Inject
	private PushPipeline pipeline;

	/**
	 * Pareses all events from all logfiles from the configured directory (see
	 * {@link BatchLogFileParserProcessor}, and push them through the
	 * {@link PushPipeline} that is configured for all parsers that extend the
	 * {@link BatchLogFileParserProcessor}.
	 * 
	 * 
	 */
	public void parsePush() {

		log.info("Batch parser [{}] has started...", batchParserName);

		final Path directory = Paths.get(batchDirectory.trim());

		if (directory.toFile().isDirectory() == false) {
			log.warn("Batch directory [{}] is not a directory, nothing to parse", batchDirectory);
			return;
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory,
				path -> path.toString().endsWith(".log"))) {

			for (final Path file : stream) {
				log.debug("Parsing log file [{}]", file);
				final Set<Event> events = parser.parse(file);
				log.info("Has parsed {} events from {}, now sending", events.size(), file);
				pipeline.pushPipeline(new ArrayList<Event>(events));
			}

		} catch (final IOException e1) {
			log.error("Can not iterate through files in the directory, no parsing for {}ph", this, e1);
			return;
		}

	}

	/**
	 * @return the batchDirectory
	 */
	public String getBatchDirectory() {
		return batchDirectory;
	}

	/**
	 * @param batchDirectory
	 *            the batchDirectory to set
	 */
	public void setBatchDirectory(final String batchDirectory) {
		this.batchDirectory = batchDirectory;
	}

	/**
	 * @return the batchParserName
	 */
	public String getBatchParserName() {
		return batchParserName;
	}

	/**
	 * @param batchParserName
	 *            the batchParserName to set
	 */
	public void setBatchParserName(final String batchParserName) {
		this.batchParserName = batchParserName;
	}

	/**
	 * @return the parser
	 */
	public BatchLogFileParser getParser() {
		return parser;
	}

	/**
	 * @param parser
	 *            the parser to set
	 */
	public void setParser(final BatchLogFileParser parser) {
		this.parser = parser;
	}

}
