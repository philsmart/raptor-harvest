package uk.ac.cardiff.raptor.harvest.parse;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ParseHelper {

	@Nullable
	public static DateTime safeGetDateTime(@Nullable final String[] array, @Nonnull final int index,
			@Nonnull final String dateFormat) {
		Objects.requireNonNull(dateFormat, "requires dateFormat to parse date with");

		if (array == null) {
			return null;
		}

		if (index < 0 || index >= array.length) {
			return null;
		}

		final DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormat);
		return fmt.parseDateTime(array[index]);

	}

	@Nullable
	public static String safeGetString(@Nullable final String[] array, @Nonnull final int index) {
		if (array == null) {
			return null;
		}

		if (index < 0 || index >= array.length) {
			return null;
		}

		return array[index];

	}

	@Nullable
	public static String[] safeGetStringArray(@Nullable final String[] array, @Nonnull final int index,
			final String delimeter) {

		if (array == null) {
			return null;
		}

		if (index < 0 || index >= array.length) {
			return null;
		}

		final String toSplit = array[index];

		final String[] splitArray = toSplit.split(delimeter);

		return splitArray;

	}

}
