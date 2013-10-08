package org.opf_labs.utils;

import java.util.List;

/**
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 5 Oct 2013:19:22:59
 */
public interface ProcessRunnerFactory {

	/**
	 * @return a completely uninitialised ProcessRunner instance
	 */
	public abstract ProcessRunner createProcessRunner();

	/**
	 * @param command a single string command
	 * @return a ProcessRunner instance initialised with the command
	 */
	public abstract ProcessRunner createProcessRunner(String command);

	/**
	 * @param commands commands and arguments as an array
	 * @return a ProcessRunner instance initialised with the commands
	 */
	public abstract ProcessRunner createProcessRunner(String[] commands);

	/**
	 * @param commands commands and arguments as a List
	 * @return a ProcessRunner instance initialised with the commands
	 */
	public abstract ProcessRunner createProcessRunner(List<String> commands);

}