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

package com.qtplaf.library.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qtplaf.library.util.list.IOList;

/**
 * Utility to insert records in a persistor in parallel.
 *
 * @author Miquel Sas
 */
public class ParallelInsertor implements Runnable {

	/** Logger instance. */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Insertor class.
	 */
	class Insertor implements Runnable {

		private List<Record> records;

		Insertor(List<Record> records) {
			this.records = records;
		}

		@Override
		public void run() {

			try {
				for (Record record : records) {
					persistor.insert(record);
					Thread.yield();
				}
			} catch (Exception exc) {
				logger.catching(exc);
			}

			synchronized (lock) {
				insertors.remove(this);
			}
		}

	}

	/**
	 * Underlying persistor.
	 */
	private Persistor persistor;
	/**
	 * The IOList manager.
	 */
	private IOList<Record> io = new IOList<>();
	/**
	 * The block size (number of records) of an insert task.
	 */
	private int blockSize = 100;
	/**
	 * The number of maximum concurrent tasks.
	 */
	private int maximumConcurrent = 4;
	/**
	 * Pool of insertors.
	 */
	private List<Insertor> insertors = new ArrayList<>();
	/**
	 * A boolean that indicates that the job should terminate when no more records are queued.
	 */
	private boolean terminate = false;
	/**
	 * Lock.
	 */
	private Object lock = new Object();
	/**
	 * Counter of insertors generated.
	 */
	private int count = 0;

	/**
	 * Constructor.
	 * 
	 * @param persistor The persistor.
	 */
	public ParallelInsertor(Persistor persistor) {
		super();
		this.persistor = persistor;
	}

	/**
	 * Add a record to be inserted.
	 * 
	 * @param record The record.
	 */
	public void addRecord(Record record) {
		while (io.getOutput().size() > blockSize * maximumConcurrent) {
			sleep(10);
		}
		io.addInput(record);
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException ignore) {
		}
	}

	/**
	 * Set the block size (number of records per insert job).
	 * 
	 * @param blockSize The block size.
	 */
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Sets the number maximum concurrent of parallel jobs.
	 * 
	 * @param maximumConcurrent The number maximum concurrent of parallel jobs.
	 */
	public void setMaximumConcurrent(int maximumConcurrent) {
		this.maximumConcurrent = maximumConcurrent;
	}

	/**
	 * Returns true if the job should terminate when no more records are queued.
	 * 
	 * @return A boolean.
	 */
	public boolean isTerminate() {
		synchronized (lock) {
			return terminate;
		}
	}

	/**
	 * Set true if the job should terminate when no more records are queued.
	 * 
	 * @param terminate A boolean.
	 */
	public void setTerminate(boolean terminate) {
		synchronized (lock) {
			this.terminate = terminate;
		}
	}

	/**
	 * Returns an insert block of records.
	 * 
	 * @return An insert block of records.
	 */
	private List<Record> getBlock() {
		List<Record> records = new ArrayList<>();

		// Transfer from input to output.
		if (io.getOutput().size() < blockSize) {
			io.transfer();
		}

		// Load up to block size.
		while (!io.getOutput().isEmpty() && records.size() < blockSize) {
			records.add(io.getOutput().remove(0));
		}

		return records;
	}

	/**
	 * Execute parallel inserts until set to terminate.
	 */
	@Override
	public void run() {
		while (true) {
			synchronized (lock) {

				// Check exit.
				if (isTerminate()) {
					break;
				}

				// Add an insertor if not achieved the maximum.
				if (insertors.size() < maximumConcurrent) {
					List<Record> block = getBlock();
					if (!block.isEmpty()) {
						Insertor insertor = new Insertor(block);
						insertors.add(insertor);
						new Thread(insertor, "Insertor " + count++).start();
					}
				}
			}

			// Yield.
			Thread.yield();
			sleep(10);
		}
	}

}
