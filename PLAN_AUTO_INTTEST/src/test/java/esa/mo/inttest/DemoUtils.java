package esa.mo.inttest;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Demo logging helpers.
 */
public class DemoUtils {

	/**
	 * Custom filter to log different stuff to different file.
	 */
	public static final class DemoFilter implements Filter {

		private final String name;

		/**
		 * Ctor.
		 * 
		 * @param n
		 */
		public DemoFilter(String n) {
			name = n;
		}

		/**
		 * Does LogRecord contain a specific name?
		 * 
		 * @param s
		 * @return
		 */
		protected boolean hasName(String s) {
			boolean doLog = false;
			int i = (null != s) ? s.indexOf(name) : -1;
			if (-1 != i && name.length() >= s.length()) {
				i = -2; // matches and not long enough to check suffix
			}
			int ch = (0 <= i) ? s.charAt(name.length()) : -1;
			// include "PrProvider", but exclude "PrProvider0"
			if (-1 != ch && '0' != ch && '1' != ch) {
				doLog = true;
			}
			return doLog;
		}

		/**
		 * Overrides isLoggable() for additional filtering.
		 * 
		 * @see java.util.logging.Filter#isLoggable(java.util.logging.LogRecord)
		 */
		@Override
		public boolean isLoggable(LogRecord record) {
			boolean doLog = false;
			// "message" is format string with {}
			for (int j = 0; (null != record.getParameters()) && (j < record.getParameters().length); ++j) {
				Object o = record.getParameters()[j];
				if (o instanceof String) {
					String s = (String) o;
					if (hasName(s)) {
						doLog = true;
						break;
					}
				}
			}
			return doLog;
		}
	}

	/**
	 * Custom formatter to output LogRecord sequence number.
	 */
	public static final class DemoFormatter extends SimpleFormatter {

		private final String form = "[%1$d] %5$s%n";

		public DemoFormatter() {
			super();
		}

		/**
		 * Overrides format() method.
		 * 
		 * @see java.util.logging.SimpleFormatter#format(java.util.logging.LogRecord)
		 */
		@Override
		public synchronized String format(LogRecord lr) {
			String source;
			if (lr.getSourceClassName() != null) {
				source = lr.getSourceClassName();
				if (lr.getSourceMethodName() != null) {
					source += " " + lr.getSourceMethodName();
				}
			} else {
				source = lr.getLoggerName();
			}
			String message = formatMessage(lr);
			String throwable = "";
			if (lr.getThrown() != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				pw.println();
				lr.getThrown().printStackTrace(pw);
				pw.close();
				throwable = sw.toString();
			}
			return String.format(form, lr.getSequenceNumber()/* ts */, source, lr.getLoggerName(),
					lr.getLevel().getName(), message, throwable);
		}
	}

	/**
	 * Hidden ctor.
	 */
	private DemoUtils() {
	}

	// /**
	// * Set logfiles format.
	// */
	// public static void setFormat() {
	// System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
	// }

	/**
	 * Set less spammy Logger levels for MAL classes.
	 */
	public static void setLevels() {
		Logger.getLogger("org.ccsds.moims.mo.mal.transport.gen").setLevel(Level.WARNING);
		Logger.getLogger("org.ccsds.moims.mo.mal.transport.rmi").setLevel(Level.WARNING);
		Logger.getLogger("org.ccsds.moims.mo.mal.impl.broker").setLevel(Level.WARNING);
		Logger.getLogger("org.ccsds.moims.mo.mal.impl").setLevel(Level.WARNING);
	}

	/**
	 * Set additional handlers to logging to files.
	 * 
	 * @param n
	 * @return
	 * @throws IOException
	 */
	public static FileHandler createHandler(final String n) throws IOException {
		FileHandler fh = new FileHandler(n + ".log");
		fh.setFilter(new DemoFilter(n));
		fh.setFormatter(new DemoFormatter());
		return fh;
	}
}
