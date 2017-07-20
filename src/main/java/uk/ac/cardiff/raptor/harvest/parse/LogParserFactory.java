package uk.ac.cardiff.raptor.harvest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.raptor.harvest.parse.filter.ExcludeLineFilter;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilter;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilterEngine;
import uk.ac.cardiff.raptor.harvest.parse.filter.RegexContainsLineFilter;

/**
 * As {@link LogParser}s can require involved construction, this factory
 * contains a central place to construct them. They should not be instantiated
 * directly.
 * 
 * @author philsmart
 *
 */
public class LogParserFactory {

	private static final Logger log = LoggerFactory.getLogger(LogParserFactory.class);

	public static EzproxyLogFileParser newEzproxyLogFileParser() {
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

	public static ShibbolethLogParser newShibbolethLogParser() {
		return new ShibbolethLogParser();
	}

}
