/**
 * Copyright (C) 2010 Cardiff University, Wales <smartp@cf.ac.uk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */

package uk.ac.cardiff.raptor.harvest.parse.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author philsmart
 * 
 */
public class RegexContainsLineFilter implements LineFilter {

	/** The class logger */
	private final Logger log = LoggerFactory.getLogger(RegexContainsLineFilter.class);

	/**
	 * The regex <code>String</code> to be found.
	 */
	private String includeIfContains;

	@Override
	public boolean parsableLine(final String line) {
		final Pattern p = Pattern.compile(includeIfContains);
		final Matcher m = p.matcher(line);
		final boolean found = m.find();
		if (found) {
			log.trace("Regex matched to {}", m.group());
		}
		return found;
	}

	/**
	 * @param includeIfContains
	 *            the includeIfContains to set
	 */
	public void setIncludeIfContains(final String includeIfContains) {
		this.includeIfContains = includeIfContains;
	}

	/**
	 * @return the includeIfContains
	 */
	public String getIncludeIfContains() {
		return includeIfContains;
	}

}
