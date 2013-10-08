package org.opf_labs.utils;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Interface created for ProcessRunnerImpl, really just for mocking out to test.
 * 
 */
public interface ProcessRunner {

	/**
	 * Sets the enviroment that the process should run in. For the the
	 * equivalent to the command
	 * 
	 * <pre>
	 * export FLIM=flam
	 * echo $FLIM
	 * </pre>
	 * 
	 * put "FLIM","flam" in the enviroment.
	 * 
	 * @param enviroment
	 *            The Map containing the mapping in the enviroment.
	 */
	public abstract void setEnviroment(Map<String, String> enviroment);

	/**
	 * Set the inputstream, from which the process should read. To be used if
	 * you need to give commands to the process, after it has begun.
	 * 
	 * @param processInput
	 *            to read from.
	 */
	public abstract void setInputStream(InputStream processInput);

	/**
	 * The directory to be used as starting dir. If not set, uses the dir of the
	 * current process.
	 * 
	 * @param startingDir
	 *            the starting dir.
	 */
	public abstract void setStartingDir(File startingDir);

	/**
	 * Set the command for this ProcessRunner.
	 * 
	 * @param commands
	 *            the new command.
	 */
	public abstract void setCommand(List<String> commands);

	/**
	 * Set the timeout. Default to Long.MAX_VALUE in millisecs
	 * 
	 * @param timeout
	 *            the new timeout in millisecs
	 */
	public abstract void setTimeout(long timeout);

	/**
	 * Decide if the outputstreams should be collected. Default true, ie,
	 * collect the output.
	 * 
	 * @param collect
	 *            should we collect the output
	 */
	public abstract void setCollection(boolean collect);

	/**
	 * The OutputStream will either be the OutputStream directly from the
	 * execution of the native commands or a cache with the output of the
	 * execution of the native commands.
	 * 
	 * @return the output of the native commands.
	 */
	public abstract InputStream getProcessOutput();

	/**
	 * The OutputStream will either be the error-OutputStream directly from the
	 * execution of the native commands or a cache with the error-output of the
	 * execution of the native commands.
	 * 
	 * @return the error-output of the native commands.
	 */
	public abstract InputStream getProcessError();

	/**
	 * Get the return code of the process. If the process timed out and was
	 * killed, the return code will be -1. But this is not exclusive to this
	 * scenario, other programs can also use this return code.
	 * 
	 * @return the return code
	 */
	public abstract int getReturnCode();

	/**
	 * Tells whether the process has timedout. Only valid after the process has
	 * been run, of course.
	 * 
	 * @return has the process timed out.
	 */
	public abstract boolean isTimedOut();

	/**
	 * Return what was printed on the output channel of a _finished_ process, as
	 * a string, including newlines.
	 * 
	 * @return the output as a string
	 */
	public abstract String getProcessOutputAsString();

	/**
	 * Return what was printed on the error channel of a _finished_ process, as
	 * a string, including newlines.
	 * 
	 * @return the error as a string
	 */
	public abstract String getProcessErrorAsString();

	/**
	 * Execute the command
	 * 
	 * @throws ProcessRunnerException
	 *             when the process execution fails
	 */
	public abstract void execute() throws ProcessRunnerException;

	/**
	 * Wrapper for RuntimeExceptions thrown by ProcessRunner
	 * 
	 * @author <a href="mailto:carl@openplanetsfoundation.org">Carl
	 *         Wilson</a>.</p> <a
	 *         href="https://github.com/carlwilson">carlwilson AT
	 *         github</a>.</p>
	 * @version 0.1
	 * 
	 *          Created 5 Oct 2013:17:59:08
	 */
	public static final class ProcessRunnerException extends Exception {
		private static final long serialVersionUID = 2979744778175170085L;

		/**
		 * @see java.lang.Exception#Exception()
		 */
		public ProcessRunnerException() {
			super();
		}

		/**
		 * @see java.lang.Exception#Exception(String)
		 */
		public ProcessRunnerException(String message) {
			super(message);
		}

		/**
		 * @see java.lang.Exception#Exception(String, Throwable)
		 */
		public ProcessRunnerException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * @see java.lang.Exception#Exception(Throwable)
		 */
		public ProcessRunnerException(Throwable cause) {
			super(cause);
		}
	}
}