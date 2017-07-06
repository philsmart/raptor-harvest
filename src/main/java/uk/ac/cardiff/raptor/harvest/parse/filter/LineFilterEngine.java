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

/**
 * @author philsmart
 * 
 */
public class LineFilterEngine {

	/**
	 * Array of <code>LineFilter</code>s that describe lines (read from a file)
	 * that should be parsed
	 */
	private LineFilter[] includeLineFilters;

	/**
	 * Array of <code>LineFilter</code>s that describe lines (read from a file)
	 * that should not be parsed
	 */
	private LineFilter[] excludeLineFilters;

	/**
	 * Iff any of the set of inclusion filters return false, or the set of
	 * exclusion filters return false, then this line <code>line</code> is not
	 * parsable.
	 * 
	 * @param line
	 *            the <code>String</code> to test
	 * @return false if the line should not be parsed (blocked by one of the
	 *         lineFilters), true otherwise
	 */
	public boolean isParsableLine(final String line) {
		final boolean isParsableFromInclusionFilters = isParsableLineIncludes(line);
		final boolean isParsableFromExclusionFilters = isParsableLineExcludes(line);
		if (isParsableFromExclusionFilters == false || isParsableFromInclusionFilters == false) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * If ANY of the includes line filters return true, then this line should be
	 * parsed. Hence a logical OR on inclusion filters.
	 * 
	 * @param line
	 *            the <code>String</code> to test
	 * @return true if any of the line filters evaluate to true, false otherwise
	 */
	private boolean isParsableLineIncludes(final String line) {
		if (includeLineFilters == null) {
			return true;
		}
		boolean isParsable = false;
		for (final LineFilter lineFilter : includeLineFilters) {
			if (lineFilter.parsableLine(line) == true) {
				isParsable = true;
			}
		}
		return isParsable;
	}

	/**
	 * If ANY of the includes line filters return false, then this line should
	 * not be parsed. Hence a logical OR on exclusion filters.
	 * 
	 * @param line
	 *            the <code>String</code> to test
	 * @return false if any of the line filters evaluate to false, true
	 *         otherwise
	 */
	private boolean isParsableLineExcludes(final String line) {
		if (excludeLineFilters == null) {
			return true;
		}
		boolean isParsable = true;
		for (final LineFilter lineFilter : excludeLineFilters) {
			if (lineFilter.parsableLine(line) == false) {
				isParsable = false;
			}
		}
		return isParsable;
	}

	/**
	 * @param includeLineFilters
	 *            the includeLineFilters to set
	 */
	public void setIncludeLineFilters(final LineFilter[] includeLineFilters) {
		this.includeLineFilters = includeLineFilters;
	}

	/**
	 * @return the includeLineFilters
	 */
	public LineFilter[] getIncludeLineFilters() {
		return includeLineFilters;
	}

	/**
	 * @param excludeLineFilters
	 *            the excludeLineFilters to set
	 */
	public void setExcludeLineFilters(final LineFilter[] excludeLineFilters) {
		this.excludeLineFilters = excludeLineFilters;
	}

	/**
	 * @return the excludeLineFilters
	 */
	public LineFilter[] getExcludeLineFilters() {
		return excludeLineFilters;
	}

}
