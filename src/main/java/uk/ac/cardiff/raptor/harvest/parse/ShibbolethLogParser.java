package uk.ac.cardiff.raptor.harvest.parse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

/**
 * Class to parse Shibboleth Identity Provider 2.x audit log files.
 * 
 * @author philsmart
 *
 */
public class ShibbolethLogParser extends BaseLogFileParser {

	/**
	 * Default logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(ShibbolethLogParser.class);

	@PostConstruct
	public void init() {
		log.info("Created Shibboleth log file parser, for log file [{}]", getLogfile());

	}

	@Override
	@Nonnull
	protected ShibbolethIdpAuthenticationEvent unmarshal(@Nullable final String line) {
		final ShibbolethIdpAuthenticationEvent event = new ShibbolethIdpAuthenticationEvent();
		if (line == null) {
			return event;
		}
		log.trace("{}", line);
		final String[] splitLine = line.split("\\|", 12);

		// log.debug("Has split event line into {} fields", splitLine.length);

		if (splitLine != null && splitLine.length == 12) {
			// Atom (ISO 8601)
			event.setEventTime(ParseHelper.safeGetDateTime(splitLine, 0, "yyyyMMdd'T'HHmmss'Z'"));

			event.setRequestBinding(ParseHelper.safeGetString(splitLine, 1));
			event.setRequestId(ParseHelper.safeGetString(splitLine, 2));
			event.setResourceId(ParseHelper.safeGetString(splitLine, 3));
			event.setMessageProfileId(ParseHelper.safeGetString(splitLine, 4));
			event.setResponseBinding(ParseHelper.safeGetString(splitLine, 6));
			event.setResponseId(ParseHelper.safeGetString(splitLine, 7));
			event.setPrincipalName(ParseHelper.safeGetString(splitLine, 8));
			event.setAuthenticationType(ParseHelper.safeGetString(splitLine, 9));
			event.setAttributes(ParseHelper.safeGetStringArray(splitLine, 10, ","));
			event.setAssertions(ParseHelper.safeGetStringArray(splitLine, 12, ","));

			event.setEventId(event.hashCode());

			log.trace("ShibEvent [{}]", event);
		} else {
			log.warn("Line does not have 12 fields or is null, failing to capture, line is [{}]", line);
		}

		return event;
	}

	@Override
	public String getName() {
		return "Shibboleth IDP 2.x LogFile Parser";

	}

}
