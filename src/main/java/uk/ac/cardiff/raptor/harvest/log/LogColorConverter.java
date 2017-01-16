package uk.ac.cardiff.raptor.harvest.log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.boot.logging.log4j2.ColorConverter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * Logback {@link CompositeConverter} colors output using the {@link AnsiOutput}
 * class. A single 'color' option can be provided to the converter, or if not
 * specified color will be picked based on the logging level. This is a copy
 * (and extension of) the Spring {@link ColorConverter} class.
 *
 */
public class LogColorConverter extends CompositeConverter<ILoggingEvent> {

	private static final Map<String, AnsiElement> ELEMENTS;

	static {
		final Map<String, AnsiElement> elements = new HashMap<String, AnsiElement>();
		elements.put("faint", AnsiStyle.FAINT);
		elements.put("red", AnsiColor.RED);
		elements.put("green", AnsiColor.GREEN);
		elements.put("yellow", AnsiColor.YELLOW);
		elements.put("blue", AnsiColor.BLUE);
		elements.put("magenta", AnsiColor.MAGENTA);
		elements.put("cyan", AnsiColor.CYAN);
		ELEMENTS = Collections.unmodifiableMap(elements);
	}

	private static final Map<Integer, AnsiElement> LEVELS;

	static {
		final Map<Integer, AnsiElement> levels = new HashMap<Integer, AnsiElement>();
		levels.put(Level.ERROR_INTEGER, AnsiColor.RED);
		levels.put(Level.WARN_INTEGER, AnsiColor.MAGENTA);
		levels.put(Level.TRACE_INTEGER, AnsiStyle.ITALIC);
		levels.put(Level.DEBUG_INTEGER, AnsiColor.BLUE);
		levels.put(Level.INFO_INTEGER, AnsiColor.GREEN);
		LEVELS = Collections.unmodifiableMap(levels);
	}

	@Override
	protected String transform(final ILoggingEvent event, final String in) {
		AnsiElement element = ELEMENTS.get(getFirstOption());
		if (element == null) {
			// Assume highlighting
			element = LEVELS.get(event.getLevel().toInteger());
			element = element == null ? AnsiColor.GREEN : element;
		}
		return toAnsiString(in, element);
	}

	protected String toAnsiString(final String in, final AnsiElement element) {
		return AnsiOutput.toString(element, in);
	}

}
