package uk.ac.cardiff.raptor.harvest.batch;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.raptor.harvest.PushPipeline;
import uk.ac.cardiff.raptor.harvest.SimplePushPipeline;

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
	 * Parses all events from a single logfile (the first it finds from the
	 * configured directory {@code batchDirectory} (see
	 * {@link BatchLogFileParserProcessor}, and pushes them through the
	 * {@link SimplePushPipeline} that is configured for all parsers that extend the
	 * {@link BatchLogFileParserProcessor}.
	 * 
	 * 
	 */
	public void parsePush() {

		final Path directory = Paths.get(batchDirectory.trim());

		if (directory.toFile().isDirectory() == false) {
			log.warn("Batch directory [{}] is not a directory, nothing to parse", batchDirectory);
			return;
		}

		try (final DirectoryStream<Path> stream = Files.newDirectoryStream(directory,
				path -> path.toString().endsWith(".log"))) {

			final Iterator<Path> pathIt = stream.iterator();
			if (pathIt.hasNext()) {
				final Path file = pathIt.next();

				log.debug("[{}] Parsing log file [{}]", batchParserName, file);
				final Set<Event> events = parser.parse(file);
				log.info("Has parsed {} events from {}, now sending", events.size(), file);
				pipeline.pushPipeline(new ArrayList<Event>(events));
				Files.move(file, Paths.get(file.toString() + ".done"));
			}

		} catch (final IOException e1) {
			log.error("Error trying to stream directory {}", batchDirectory, e1);
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("BatchLogFileParserProcessor [batchDirectory=");
		builder.append(batchDirectory);
		builder.append(", batchParserName=");
		builder.append(batchParserName);
		builder.append("]");
		return builder.toString();
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

	/**
	 * @return the pipeline
	 */
	public PushPipeline getPipeline() {
		return pipeline;
	}

	/**
	 * @param pipeline
	 *            the pipeline to set
	 */
	public void setPipeline(final PushPipeline pipeline) {
		this.pipeline = pipeline;
	}

}
