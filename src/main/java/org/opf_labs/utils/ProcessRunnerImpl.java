/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package org.opf_labs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * <p>
 * Native command executor. Based on ProcessBuilder.
 * <ul>
 * <li>Incorporates timeout for spawned processes.
 * <li>Handle automatic collection of bytes from the output and error streams,
 * to ensure that they dont block.
 * <li>Handles automatic feeding of input to the process.
 * <li>Blocking while executing
 * <li>Implements Runnable, to be wrapped in a Thread.
 * </ul>
 * </p>
 * 
 * Use the Assessor methods to configure the Enviroment, input, collecting
 * behavoiur, timeout and startingDir. Use the getters to get the output and
 * error streams as strings, along with the return code and if the process timed
 * out.
 * 
 * <p>
 * This code is not yet entirely thread safe. Be sure to only call a given
 * processRunner from one thread, and do not reuse it.
 * </p>
 */
public class ProcessRunnerImpl implements Runnable, ProcessRunner {
	private InputStream processInput = null;
	private InputStream processOutput = null;
	private InputStream processError = null;

	/**
	 * The threads that polls the output from the commands. When a thread is
	 * finished, it removes itself from this list.
	 */
	private final List<Thread> threads = Collections
			.synchronizedList(new LinkedList<Thread>());

	private static final int MAXINITIALBUFFER = 1000000;
	private static final int THREADTIMEOUT = 1000; // Milliseconds
	private static final int POLLING_INTERVAL = 100;// milli

	private final ProcessBuilder pb;

	private long timeout = Long.MAX_VALUE;

	private boolean collect = true;
	private int maxOutput = 31000;
	private int maxError = 31000;
	private int return_code;
	private boolean timedOut;

	/**
	 * Create a new ProcessRunner. Cannot run, until you specify something with
	 * the assessor methods.
	 */
	ProcessRunnerImpl() {
		this.pb = new ProcessBuilder();
	}

	/**
	 * @see ProcessRunner#setEnviroment(java.util.Map)
	 */
	@Override
	public void setEnviroment(final Map<String, String> enviroment) {
		if (enviroment != null) {
			Map<String, String> env = this.pb.environment();
			env.putAll(enviroment);
		}
	}

	/**
	 * @see ProcessRunner#setInputStream(java.io.InputStream)
	 */
	@Override
	public synchronized void setInputStream(final InputStream processInput) {
		this.processInput = processInput;
	}

	/**
	 * @see ProcessRunner#setStartingDir(java.io.File)
	 */
	@Override
	public void setStartingDir(final File startingDir) {
		this.pb.directory(startingDir);
	}

	/**
	 * @see ProcessRunner#setCommand(java.util.List)
	 */
	@Override
	public void setCommand(final List<String> commands) {
		this.pb.command(commands);
	}

	/**
	 * @see ProcessRunner#setTimeout(long)
	 */
	@Override
	public synchronized void setTimeout(final long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @see ProcessRunner#setCollection(boolean)
	 */
	@Override
	public void setCollection(final boolean collect) {
		this.collect = collect;
	}

	/**
	 * How many bytes should we collect from the ErrorStream. Will block when
	 * limit is reached. Default 31000. If set to negative values, will collect
	 * until out of memory.
	 * 
	 * @param maxError
	 *            number of bytes to max collect.
	 */
	public void setErrorCollectionByteSize(final int maxError) {
		this.maxError = maxError;
	}

	/**
	 * How many bytes should we collect from the OutputStream. Will block when
	 * limit is reached. Default 31000; If set to negative values, will collect
	 * until out of memory.
	 * 
	 * @param maxOutput
	 *            number of bytes to max collect.
	 */

	public void setOutputCollectionByteSize(final int maxOutput) {
		this.maxOutput = maxOutput;
	}

	/**
	 * @see ProcessRunner#getProcessOutput()
	 */
	@Override
	public InputStream getProcessOutput() {
		return this.processOutput;
	}

	/**
	 * @see ProcessRunner#getProcessError()
	 */
	@Override
	public InputStream getProcessError() {
		return this.processError;
	}

	/**
	 * @see ProcessRunner#getReturnCode()
	 */
	@Override
	public int getReturnCode() {
		return this.return_code;
	}

	/**
	 * @see ProcessRunner#isTimedOut()
	 */
	@Override
	public boolean isTimedOut() {
		return this.timedOut;
	}

	/**
	 * @see ProcessRunner#getProcessOutputAsString()
	 */
	@Override
	public String getProcessOutputAsString() {
		return getStringContent(getProcessOutput());
	}

	/**
	 * @see ProcessRunner#getProcessErrorAsString()
	 */
	@Override
	public String getProcessErrorAsString() {
		return getStringContent(getProcessError());
	}

	/**
	 * Wait for the polling threads to finish.
	 */
	private synchronized void waitForThreads() {
		long endTime = System.currentTimeMillis() + THREADTIMEOUT;
		while (System.currentTimeMillis() < endTime && this.threads.size() > 0) {
			try {
				wait(POLLING_INTERVAL);
			} catch (InterruptedException e) {
				// Ignore, as we are just waiting
			}
		}
	}

	/**
	 * Utility Method for reading a stream into a string, for returning
	 * 
	 * @param stream
	 *            the string to read.
	 * @return A string with the contents of the stream.
	 */
	private static String getStringContent(final InputStream stream) {
		if (stream == null) {
			return null;
		}
		BufferedInputStream in = new BufferedInputStream(stream, 1000);
		StringWriter sw = new StringWriter(1000);
		int c;
		try {
			while ((c = in.read()) != -1) {
				sw.append((char) c);
			}
			return sw.toString();
		} catch (IOException e) {
			return "Could not transform content of stream to String";
		}

	}

	@Override
	public void execute() throws ProcessRunnerException {
		try {
			run();
		} catch (RuntimeException excep) {
			throw new ProcessRunnerException("Error running process: " + excep.getMessage());
		}
	}

	/**
	 * Run the method, feeding it input, and killing it if the timeout is
	 * exceeded. Blocking.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@SuppressWarnings("resource")
	public void run() {
		ByteArrayOutputStream pOut = null;
		ByteArrayOutputStream pError = null;
		try {
			Process p = this.pb.start();
			if (this.collect) {
				pOut = collectProcessOutput(p.getInputStream(), this.maxOutput);
				pError = collectProcessOutput(p.getErrorStream(), this.maxError);
				this.return_code = execute(p);
				waitForThreads();
				this.processOutput = new ByteArrayInputStream(
						pOut.toByteArray());
				this.processError = new ByteArrayInputStream(
						pError.toByteArray());

			} else {
				this.processOutput = p.getInputStream();
				this.processError = p.getErrorStream();
				this.return_code = execute(p);
			}
		} catch (IOException e) {
			throw new RuntimeException(
					"An io error occurred when running the command", e);
		} finally {
			IOUtils.closeQuietly(pOut);
			IOUtils.closeQuietly(pError);
		}
	}

	private synchronized int execute(final Process p) {
		long startTime = System.currentTimeMillis();
		feedProcess(p, this.processInput);
		int return_value;

		while (true) {
			// is the thread finished?
			try {
				// then return
				return_value = p.exitValue();
				break;
			} catch (IllegalThreadStateException e) {
				// not finished
			}
			// is the runtime exceeded?
			if (System.currentTimeMillis() - startTime > this.timeout) {
				// then return
				p.destroy();
				return_value = -1;
				this.timedOut = true;
			}
			// else sleep again
			try {
				wait(POLLING_INTERVAL);
			} catch (InterruptedException e) {
				// just go on.
			}

		}
		return return_value;

	}

	private ByteArrayOutputStream collectProcessOutput(
			final InputStream inputStream, final int maxCollect) {
		final ByteArrayOutputStream stream;
		if (maxCollect < 0) {
			stream = new ByteArrayOutputStream();
		} else {
			stream = new ByteArrayOutputStream(Math.min(MAXINITIALBUFFER,
					maxCollect));
		}

		Thread t = new Thread() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				try {
					InputStream reader = null;
					OutputStream writer = null;
					try {
						reader = new BufferedInputStream(inputStream);
						writer = new BufferedOutputStream(stream);
						int c;
						int counter = 0;
						while ((c = reader.read()) != -1) {
							counter++;
							if (maxCollect < 0 || counter < maxCollect) {
								writer.write(c);
							}
						}
					} finally {
						if (reader != null) {
							reader.close();
						}
						if (writer != null) {
							writer.close();
						}
					}
				} catch (IOException e) {
					// This seems ugly
					throw new RuntimeException("Couldn't read output from "
							+ "process.", e);
				}
				ProcessRunnerImpl.this.threads.remove(this);
			}
		};
		this.threads.add(t);
		t.start();
		return stream;
	}

	@SuppressWarnings("resource")
	private static void feedProcess(final Process process,
			final InputStream input) {
		if (input == null) {
			// No complaints here - null just means no input
			return;
		}

		final OutputStream pIn = process.getOutputStream();
		final InputStream given = input;
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					OutputStream writer = null;
					try {
						writer = new BufferedOutputStream(pIn);
						int c;
						while ((c = given.read()) != -1) {
							writer.write(c);
						}
					} finally {
						if (writer != null) {
							writer.close();
						}
						pIn.close();
					}
				} catch (IOException e) {
					// This seems ugly
					throw new RuntimeException("Couldn't write input to "
							+ "process.", e);
				}
			}
		};

		Thread.UncaughtExceptionHandler u = new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread thread, final Throwable e) {
				// Might not be the prettiest solution...
			}
		};
		t.setUncaughtExceptionHandler(u);
		t.start();
		try {
			pIn.close();
		} catch (IOException excep) {
			// Nothing to do
		}
	}
}