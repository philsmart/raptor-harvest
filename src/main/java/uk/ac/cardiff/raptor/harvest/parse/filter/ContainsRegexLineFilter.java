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
 * The Class ContainsRegexLineFilter.
 * 
 * @author philsmart
 */
public class ContainsRegexLineFilter implements LineFilter {

	/** The class logger. */
	private final Logger log = LoggerFactory.getLogger(ContainsRegexLineFilter.class);

	/** The regular expression to be matched. */
	private String includeIfContainsRegex;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.cardiff.raptor.parse.external.file.format.LineFilter#parsableLine(
	 * java.lang.String)
	 */
	@Override
	public boolean parsableLine(final String line) {
		final Pattern p = Pattern.compile(includeIfContainsRegex);
		final Matcher m = p.matcher(line);
		return m.find();
	}

	/**
	 * Sets the include if contains regex.
	 * 
	 * @param includeIfContainsRegex
	 *            the new include if contains regex
	 */
	public void setIncludeIfContainsRegex(final String includeIfContainsRegex) {
		this.includeIfContainsRegex = includeIfContainsRegex;
	}

	/**
	 * Gets the include if contains regex.
	 * 
	 * @return the includeIfContains
	 */
	public String getIncludeIfContainsRegex() {
		return includeIfContainsRegex;
	}

}
