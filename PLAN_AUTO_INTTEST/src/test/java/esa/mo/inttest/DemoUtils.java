package esa.mo.inttest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
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
		
		protected int hasSuffix(String s, int at) {
			int r = at + name.length();
			if (r < s.length()) {
				char ch = s.charAt(r);
				if (!Character.isDigit(ch)) {
					r = -1;
				}
			} else {
				r = -1;
			}
			return r;
		}
		
		protected String findAndReplace(String s) {
			int at = (null != s) ? s.indexOf(name) : -1;
			String z = null;
			if (-1 != at) {
				int suf = hasSuffix(s, at);
				if (-1 != suf) {
					char ch = s.charAt(suf);
					if ('0' == ch || '1' == ch) { // exclude
					} else { // replace
						StringBuilder b = new StringBuilder(s);
						b.replace(suf, suf+1, "");
						z = b.toString();
					}
				} else { // found, no replace
					z = s;
				}
			}
			return z;
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
			Object[] p = record.getParameters();
			for (int j = 0; (null != p) && (j < p.length); ++j) {
				if ((null != p[j]) && (p[j] instanceof String)) {
					String z = findAndReplace((String)p[j]);
					if (null != z) {
						doLog = true;
						if (!z.equals(p[j])) {
							p[j] = z;
						}
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

		private static final String FORMAT = "[%1$d] %5$s%n";

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
			return String.format(FORMAT, lr.getSequenceNumber(), source, lr.getLoggerName(),
					lr.getLevel().getName(), message, throwable);
		}
	}

	/**
	 * Hidden ctor.
	 */
	private DemoUtils() {
	}

	/**
	 * Is log2file property set?
	 * @return
	 */
	public static boolean getLogFlag() {
		String val = System.getProperty("log2file");
		boolean doLog = (null != val) && "true".equalsIgnoreCase(val);
		System.out.println("writing to log files is turned on: "+doLog);
		return doLog;
	}
	
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
	 * @param name
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static FileHandler createHandler(final String name, final String path) throws IOException {
		String fn = name;
		if (null != path) {
			fn = path + name;
			File folder = Paths.get(fn).getParent().toFile();
			if (folder.exists() && folder.isDirectory()) {
				// ok
			} else {
				folder.mkdirs();
			}
		}
		FileHandler fh = new FileHandler(fn + ".log");
		fh.setFilter(new DemoFilter(name));
		fh.setFormatter(new DemoFormatter());
		return fh;
	}
}
