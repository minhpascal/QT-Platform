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

import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.list.ListUtils;

/**
 * A pool of parallel executing jobs.
 *
 * @author Miquel Sas
 */
public class JobPool extends Job {

	/** List of jobs to be executed. */
	private List<Job> jobs = new ArrayList<>();
	/** Number of maximum concurrent jobs. */
	private int maximumConcurrent = 8;
	/** Stop flag. */
	private boolean stopExecute = false;

	/**
	 * Constructor.
	 */
	public JobPool() {
	}

	/**
	 * Add a job to the list of jobs to be executed.
	 * 
	 * @param job The job to execute.
	 */
	public void add(Job job) {
		jobs.add(job);
	}

	/**
	 * Set the number of maximum concurrent jobs.
	 * 
	 * @param maximumConcurrent The number of maximum concurrent jobs.
	 */
	public void setMaximumConcurrent(int maximumConcurrent) {
		this.maximumConcurrent = maximumConcurrent;
	}

	/**
	 * Force stop.
	 */
	public synchronized void stopExecute() {
		stopExecute = true;
	}

	/**
	 * Execute the job.
	 */
	public void execute() {
		List<Job> jobsToExecute = new ArrayList<>(jobs);
		List<Job> executingJobs = new ArrayList<>();

		while (true) {

			// Remove terminated from executing list.
			List<Job> toRemove = new ArrayList<>();
			for (Job job : executingJobs) {
				if (job.isTerminated()) {
					toRemove.add(job);
				}
			}
			executingJobs.removeAll(toRemove);

			// Check stop.
			if (!stopExecute) {
				if (executingJobs.isEmpty()) {
					break;
				}
			}
			
			// Add pending tasks to execute and launch them.
			if (!stopExecute) {
				while (!jobsToExecute.isEmpty() && executingJobs.size() < maximumConcurrent) {
					Job job = ListUtils.removeLast(jobsToExecute);
					executingJobs.add(job);
					new Thread(job, Integer.toString(jobsToExecute.size())).start();
				}
			}
			
			// If all jobs have terminated, exit.
			if (jobsToExecute.isEmpty() && executingJobs.isEmpty()) {
				break;
			}

			// Yield
			Thread.yield();
		}
	}
}
