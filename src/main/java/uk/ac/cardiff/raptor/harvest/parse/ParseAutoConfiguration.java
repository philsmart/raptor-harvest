package uk.ac.cardiff.raptor.harvest.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import uk.ac.cardiff.raptor.harvest.parse.filter.ExcludeLineFilter;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilter;
import uk.ac.cardiff.raptor.harvest.parse.filter.LineFilterEngine;
import uk.ac.cardiff.raptor.harvest.parse.filter.RegexContainsLineFilter;

public class ParseAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@ConditionalOnProperty(prefix = "harvest.shibboleth", name = "logfile")
	@ConfigurationProperties(prefix = "harvest.shibboleth")
	@Bean("shibIdpParser")
	public LogParser shibbolethParser() {
		log.info("Creating Shibboleth Idp Log File Parser");

		return new ShibbolethLogParser();
	}

	@ConditionalOnProperty(prefix = "harvest.ezproxy", name = "logfile")
	@ConfigurationProperties(prefix = "harvest.ezproxy")
	@Bean("ezproxyParser")
	public LogParser ezproxyParser() {
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

}
