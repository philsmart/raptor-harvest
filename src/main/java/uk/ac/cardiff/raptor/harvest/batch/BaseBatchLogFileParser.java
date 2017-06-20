package uk.ac.cardiff.raptor.harvest.batch;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;

public class BaseBatchLogFileParser implements BatchLogFileParser {

	private static final Logger log = LoggerFactory.getLogger(BatchLogFileParser.class);

	private String batchDirectory;

	@Override
	public Set<Event> parse() {

		final Path directory = Paths.get(batchDirectory);

		if (directory.toFile().isDirectory() == false) {
			log.warn("Batch directory [{}] is not a directory, nothing to parse", batchDirectory);
			return Collections.emptySet();
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory,
				path -> path.toString().endsWith(".log"))) {

			for (final Path file : stream) {
				log.debug("Parsing log file [{}]", file);
			}

		} catch (final IOException e1) {
			log.error("Can not iterate through files in the directory, no parsing for {}", this, e1);
			return Collections.emptySet();
		}

		return null;
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

}
