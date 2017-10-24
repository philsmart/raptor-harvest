package uk.ac.cardiff.raptor.harvest.parse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.ShibbolethIdpAuthenticationEvent;

/**
 * Class to parse Shibboleth Identity Provider 2.x audit log files. Should
 * create a new instance of this class for each different logfile that is parsed
 * (can not be shared for different log files, as keeps track of last entry
 * parsed).
 * 
 * @author philsmart
 *
 */
public class ShibbolethV3LogParser extends BaseLogFileParser {

	/**
	 * Default logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(ShibbolethV3LogParser.class);

	@PostConstruct
	public void init() {
		log.info("Created Shibboleth version 3.x log file parser, for log file [{}]", getLogfile());

	}

	@Override
	@Nonnull
	protected ShibbolethIdpAuthenticationEvent unmarshal(@Nullable final String line) {
		final ShibbolethIdpAuthenticationEvent event = new ShibbolethIdpAuthenticationEvent();
		if (line == null) {
			return event;
		}

		final String[] splitLine = line.split("\\|", 12);

		// log.debug("Has split event line into {} fields", splitLine.length);

		if (splitLine != null && splitLine.length == 12) {
			// Atom (ISO 8601)
			event.setEventTime(ParseHelper.safeGetDateTime(splitLine, 0, "yyyyMMdd'T'HHmmss'Z'"));

			event.setRequestBinding(ParseHelper.safeGetString(splitLine, 1));
			event.setRequestId(ParseHelper.safeGetString(splitLine, 2));
			event.setResourceId(ParseHelper.safeGetString(splitLine, 3));

			final String messageProfileId = ParseHelper.safeGetString(splitLine, 4);

			final String idpEntityId = ParseHelper.safeGetString(splitLine, 5);

			event.setResponseBinding(ParseHelper.safeGetString(splitLine, 6));
			event.setPrincipalName(ParseHelper.safeGetString(splitLine, 8));
			event.setAuthenticationType(ParseHelper.safeGetString(splitLine, 9));
			event.setAttributes(ParseHelper.safeGetStringArray(splitLine, 10, ","));
			event.setServiceId(idpEntityId);

			event.setEventId(event.hashCode());

			if (messageProfileId.contains(":sso") == false && messageProfileId.contains("sso/browser") == false) {
				log.trace(
						"Did NOT include ShibEvent, as the message profile id did not contain ':sso', returning an empty event for dismissal. Event was [{}]",
						event);
				return new ShibbolethIdpAuthenticationEvent();
			}

			log.trace("ShibEvent [{}]", event);
		} else {
			log.warn("Line does not have 12 fields or is null, failing to capture, returning empty event, line is [{}]",
					line);
		}

		return event;
	}

	@Override
	public String getName() {
		return "Shibboleth IDP 3.x LogFile Parser";

	}

}
