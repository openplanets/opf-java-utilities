/**
 * 
 */
package org.opf_labs.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author  <a href="mailto:carl@openplanetsfoundation.org">Carl Wilson</a>.</p>
 *          <a href="https://github.com/carlwilson">carlwilson AT github</a>.</p>
 * @version 0.1
 * 
 * Created 5 Oct 2013:13:36:20
 */
public final class ProcessRunnerImplFactory implements ProcessRunnerFactory {
	private static final ProcessRunnerFactory INSTANCE = new ProcessRunnerImplFactory(); 
	private ProcessRunnerImplFactory() {
	}

	/**
	 * @return the ProcessRunners instance
	 */
	public static ProcessRunnerFactory getInstance() {
		return INSTANCE;
	}
	/**
	 * @see org.opf_labs.utils.ProcessRunnerFactory#createProcessRunner()
	 */
	@Override
	public ProcessRunner createProcessRunner() {
		return new ProcessRunnerImpl();
	}
	
	/**
	 * @see org.opf_labs.utils.ProcessRunnerFactory#createProcessRunner(java.lang.String)
	 */
	@Override
	public ProcessRunner createProcessRunner(final String command) {
		String[] commands = new String[]{command};
		return this.createProcessRunner(commands);
	}

	/**
	 * @see org.opf_labs.utils.ProcessRunnerFactory#createProcessRunner(java.lang.String[])
	 */
	@Override
	public ProcessRunner createProcessRunner(final String[] commands) {
		return this.createProcessRunner(Arrays.asList(commands));
	}

	/**
	 * @see org.opf_labs.utils.ProcessRunnerFactory#createProcessRunner(java.util.List)
	 */
	@Override
	public ProcessRunner createProcessRunner(final List<String> commands) {
		ProcessRunner processRunner = new ProcessRunnerImpl();
		processRunner.setCommand(commands);
		return processRunner;
	}
}
