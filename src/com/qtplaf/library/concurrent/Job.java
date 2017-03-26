/*
 * Copyright (C) 2015 Miquel Sas
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.qtplaf.library.concurrent;

/**
 * Light weight execution task. For long tasks willing to be monitored use the more complex <tt>Task</tt>. The
 * <tt>Job</tt> is designed for small calculations that normally do not throw exceptions, although they are supported.
 * Jobs can not be paused nor cancelled, like tasks can.
 * <p>
 * <tt>Job</tt>'s should <tt>Thread.yield()</tt> at every loop.
 *
 * @author Miquel Sas
 */
public abstract class Job implements Runnable {

	/** Job status: pending to execute. */
	private final static int Pending = 0;
	/** Job status: executing. */
	private final static int Executing = 1;
	/** Job status. terminated. */
	private final static int Terminated = 2;

	/** Status. */
	private int status = Pending;
	/** Eventual thrown exception. */
	private Exception exception;

	/**
	 * Constructor.
	 */
	public Job() {
	}

	/**
	 * Run the job (normally in a thread).
	 */
	@Override
	public void run() {
		// Status executing.
		synchronized (this) {
			status = Executing;
		}
		// Do execute.
		try {

		} catch (Exception exception) {
			this.exception = exception;
		}
		// Status terminated.
		synchronized (this) {
			status = Terminated;
		}
	}

	/**
	 * Check if the job is pending to execute.
	 * 
	 * @return A boolean.
	 */
	public boolean isPending() {
		return status == Pending;
	}

	/**
	 * Check if the job is executing.
	 * 
	 * @return A boolean.
	 */
	public boolean isExecuting() {
		return status == Executing;
	}

	/**
	 * Check if the job is terminated.
	 * 
	 * @return A boolean.
	 */
	public boolean isTerminated() {
		return status == Terminated;
	}

	/**
	 * Returns the exception if any.
	 * 
	 * @return The exception or null.
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Execute the job.
	 */
	public abstract void execute();
}
