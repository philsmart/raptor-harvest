package uk.ac.cardiff.raptor.harvest.parse;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cardiff.model.event.Event;
import uk.ac.cardiff.model.event.EzproxyAuthenticationEvent;

/**
 * Class to parse Shibboleth Identity Provider 2.x audit log files. Should
 * create a new instance of this class for each different logfile that is parsed
 * (can not be shared for different log files, as keeps track of last entry
 * parsed).
 */
public class EzproxyLogFileParser extends BaseLogFileParser {

	/**
	 * If supplied, the scope will be removed from the principal name field
	 */
	private String principalScope;

	/**
	 * Default logger.
	 */
	private static final Logger log = LoggerFactory.getLogger(EzproxyLogFileParser.class);

	@Override
	protected Event unmarshal(final String line) {
		final EzproxyAuthenticationEvent event = new EzproxyAuthenticationEvent();
		if (line == null) {
			return event;
		}

		final String[] splitLine = line.split(" ", 7);
		if (splitLine != null && splitLine.length == 7) {

			String scopedPrincipalName = splitLine[2];

			// if - principal, not a line, so return empty event;
			if (scopedPrincipalName == null || scopedPrincipalName.equals("-")) {
				return event;
			}

			if (principalScope != null) {
				scopedPrincipalName = scopedPrincipalName.replace(principalScope, "");
			}
			event.setPrincipalName(scopedPrincipalName);

			String completeDate = splitLine[3] + splitLine[4];
			completeDate = completeDate.replace("[", "").replace("]", "");
			event.setEventTime(ParseHelper.safeGetDateTime(new String[] { completeDate }, 0, "dd/MMM/yyyy:HH:mm:ssZ"));

			event.setSessionId(ParseHelper.safeGetString(splitLine, 1));
			event.setRequesterIp(ParseHelper.safeGetString(splitLine, 0));

			final String serviceHost = retain(splitLine[6], "https://[^:]*|http://[^:]*", false);
			event.setServiceHost(serviceHost);

			String resourceId = retain(splitLine[6],
					"url=http%3a%2f%2f[^%]*|url=https://[^/]*|url=https%3a%2f%2f[^%]*|url=http://[^/]*|url=%2520http%3a%2f%2f[^%]*|url=%2520https%3a%2f%2f[^%]*|url=%20http%3a%2f%2f[^%]*|url=%20https%3a%2f%2f[^%]*|url=http%253A%252F%252F[^%]*",
					true);

			try {
				resourceId = URLDecoder.decode(resourceId, StandardCharsets.UTF_8.toString());
			} catch (final UnsupportedEncodingException e) {
				log.warn("Could not URL decode the resourceID, resourceID was [{}]", resourceId);
			}

			if (resourceId != null) {
				resourceId = resourceId.replace("url=", "").replaceAll("%2520", "");
			}

			event.setResourceId(resourceId);

			event.setEventId(event.hashCode());

		}
		return event;
	}

	/**
	 * Returns only the substring from <code>value</code> that matches the given
	 * <code>regex<code>. If no expressions match, then
	 * a value of 'error' is returned. If more than one match is found, the
	 * first is chosen.
	 * 
	 * &#64;param value
	 *            the string from which a regex matching group is returned
	 * &#64;param regex
	 *            the regex pattern to match
	 * &#64;param caseSensitive
	 *            whether the regex is to be treated as case sensitive.
	 * @return the substring from <code>value</code> that matches the regex
	 *         pattern in <code>header</code>, or 'error' is no matches are
	 *         found.
	 */
	private String retain(final String value, @Nullable final String regex, final boolean caseSensitive) {
		if (regex == null) {
			return value;
		}

		Pattern p = null;
		if (caseSensitive) {
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		} else {
			p = Pattern.compile(regex);
		}

		final Matcher match = p.matcher(value);
		final ArrayList<String> allFound = new ArrayList<String>();
		while (match.find()) {
			allFound.add(match.group());
		}

		if (allFound.size() > 0) {
			return allFound.get(0);
		}
		return "error";
	}

	/**
	 * @return the principalScope
	 */
	public String getPrincipalScope() {
		return principalScope;
	}

	/**
	 * @param principalScope
	 *            the principalScope to set
	 */
	public void setPrincipalScope(final String principalScope) {
		this.principalScope = principalScope;
	}

}
