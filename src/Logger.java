import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public final class Logger {
	// Private Constants
	private static final String CLASS_NAME = "Logger";
	private static final int LOG_ERROR = 1000;
	private static final String LOG_FILE_PATH = "./zout/log.txt";
	private static final File LOG_FILE = new File(LOG_FILE_PATH);
	private static final Date TIMESTAMP = new Date();
	private static final SimpleDateFormat SDF = new SimpleDateFormat("MM-dd-YYYY hh:mm:ss", Locale.ENGLISH);
	
	// Private Members
	private static BufferedWriter logger = null;
	
	/**
	 *  Start logging, if logging has not already started.
	 */
	public static void startLog() {
		if(logger != null) {
			return;
		}
		
		FileWriter fw = null;

		// create log file if it does not exist, exit on error
		if (!LOG_FILE.exists()) {
			try {
				LOG_FILE.createNewFile();
			} catch (IOException e) {
				System.err.printf("Log Error: Could not create log file with path '%s'.\n",
								LOG_FILE.getAbsolutePath());

				e.printStackTrace();
				System.exit(LOG_ERROR);
			}
		}

		// create FileWriter with log file, exit on error
		try {
			fw = new FileWriter(LOG_FILE, true);
		} catch (IOException e) {
			System.err.printf("Log Error: Could not instantiate FileWriter with file '%s'.\n",
							LOG_FILE.getAbsolutePath());

			e.printStackTrace();
			System.exit(LOG_ERROR);
		}

		// wrap the FileWriter with a BufferedWriter, exit on error
		if (fw != null) {
			logger = new BufferedWriter(fw);
		} else {
			System.err.printf("Log Error: FileWriter is null, exiting.\n");
			System.exit(LOG_ERROR);
		}

	}
	
	/**
	 * If logging has started then this method will flush and close the stream, otherwise it will do nothing.
	 */
	public static void closeLog() {
		if(logger == null) {
			return;
		}
		
		try {
			logger.flush();
		} catch (IOException e) {
			System.err.printf("Log Error: Unable to flush logger associated with '%s'.\n",
					LOG_FILE.getAbsolutePath());
			e.printStackTrace();
		}

		try {
			logger.close();
		} catch (IOException e) {
			System.err.printf("Log Error: Unable to close logger associated with '%s'.\n",
					LOG_FILE.getAbsolutePath());
			e.printStackTrace();
		}
		
		// set logger to null to allow logging to start again
		logger = null;
	}
	
	private static void log(String msg) {
		log(CLASS_NAME, msg);
	}
	
	/**
	 * Append a time stamped message to the log.
	 * 
	 * @param classname - name of the class logging the message.
	 * @param msg - message to be appended to the log.
	 */
	public static void log(String classname, String msg) {
		if(logger == null) {
			return;
		}
		
		String tag = "\n[" + SDF.format(TIMESTAMP) + "]  " + classname + " :>\n";
/*		
		if(msg.trim().charAt(msg.length() - 1) != '\n') {
			msg += "\n";
		}
		
		msg = tag + "  " +  msg.replaceAll("\n", "\n  ");
		msg = msg.replaceAll("\n  $", "\n");
*/
		msg = tag + msg;
		try {
			logger.append(msg);
			logger.flush();
		} catch (IOException e) {
			System.err.printf("Log Error: Unable to append message to log file, '%s'.\n Message: '%s'.\n",
							LOG_FILE.getAbsolutePath(), msg);
			e.printStackTrace();
		}

	}
}
