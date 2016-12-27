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

package com.qtplaf.library.util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.task.Task;
import com.qtplaf.library.task.TaskHandler;
import com.qtplaf.library.task.TaskRunner;

/**
 * Utility class to copy files from a list of source directories to a list of destination directories.
 * 
 * @author Miquel Sas
 */
public class FileCopy extends TaskRunner {

	/**
	 * Count listener.
	 */
	class CountListener implements FileScannerListener {
		/** A boolean that indicated if the count if for purge. */
		private boolean countForPurge = false;
		/** Number of files to process. */
		private long count = 0;

		/**
		 * Constructor indicating if the count is for countForPurge.
		 * 
		 * @param countForPurge A boolean.
		 */
		public CountListener(boolean purge) {
			super();
			this.countForPurge = purge;
		}

		/**
		 * Called upon each file sanned.
		 */
		@Override
		public void file(File sourceDirectory, File file) {

			// Check exclude.
			if (countForPurge) {
				if (isExcludePurge(file)) {
					return;
				}
			} else {
				if (isExcludeCopy(file)) {
					return;
				}
			}

			// Cumulate bytes to copy.
			if (!countForPurge && file.isFile()) {
				bytesToProcess += file.length();
			}

			count++;
		}

		/**
		 * Returns the number of files to process.
		 */
		public long getCount() {
			return count;
		}
	}

	/**
	 * Copy listener.
	 */
	class CopyListener implements FileScannerListener {
		/**
		 * Called upon each file sanned.
		 */
		@Override
		public void file(File sourceDirectory, File file) throws IOException {

			// Check exclude.
			if (isExcludeCopy(file)) {
				return;
			}

			// Notify step start.
			long step = getStep() + 1;
			long steps = getSteps();
			StringBuilder b = new StringBuilder();
			b.append(" (");
			b.append(FileUtils.getSizeLabel(bytesProcessed + file.length(), 1, getSession().getLocale()));
			b.append(" / ");
			b.append(FileUtils.getSizeLabel(bytesToProcess, 1, getSession().getLocale()));
			b.append(")");
			notifyStepStart(step, getStepMessage(step, steps, "Copy source ", b.toString()));

			// Copy the source file.
			if (file.isFile()) {
				// The destination root directory.
				File destinationDirectory = map.get(sourceDirectory);
				// The destination file.
				File destinationFile = FileUtils.getDestinationFile(sourceDirectory, file, destinationDirectory);
				
				String from = getSession().getString("tokenFrom") + ": " + file.getAbsolutePath();
				String to = getSession().getString("tokenTo") + ": " + destinationFile.getAbsolutePath();
				notifyLabel(labelFrom, from);
				notifyLabel(labelTo, to);

				// Try copy and register if not done by means of any error including access denied.
				try {

					// If destination file exists and can not write,would normally be by access denied. Try delete
					// first.
					if (destinationFile.exists()) {
						if (!destinationFile.canWrite()) {
							if (!destinationFile.delete()) {
								String message = getSession().getString("securityAccessToFileDenied");
								throw new FileException(destinationFile, message);
							}
						}
					}

					// Try copy without any timestamp check.
					FileUtils.copy(file, destinationFile);

				} catch (Exception exception) {
					addException(new FileException(destinationFile, exception.getMessage(), exception));
				}

				// Cumulate bytes processed.
				bytesProcessed += file.length();
			}

			// Notify step end.
			notifyStepEnd();
		}
	}

	/**
	 * Purge listener.
	 */
	class PurgeListener implements FileScannerListener {

		/** Deque to store directories not empty. */
		private Deque<File> deque = new ArrayDeque<>();

		/**
		 * Called upon each file sanned.
		 */
		@Override
		public void file(File destinationDirectory, File file) throws IOException {

			// Check exclude.
			if (isExcludePurge(file)) {
				return;
			}

			// Notify step start.
			long step = getStep() + 1;
			long steps = getSteps();
			notifyStepStart(step, getStepMessage(step, steps, "Purge destination ", null));

			// If the file is a directory, just add it to the deque.
			if (file.isDirectory()) {
				deque.addLast(file);
			}

			// If it is a file, try countForPurge.
			if (file.isFile()) {
				String purge = getSession().getString("tokenPurge") + ": " + file.getAbsolutePath();
				notifyLabel(labelFrom, purge);
				purge(destinationDirectory, file);
			}

			// Files in the deque are directories. While last directories are empty delete them.
			while (!deque.isEmpty()) {
				if (FileUtils.isEmpty(deque.getLast())) {
					File last = deque.removeLast();
					String purge = getSession().getString("tokenPurge") + ": " + last.getAbsolutePath();
					notifyLabel(labelFrom, purge);
					last.delete();
				} else {
					break;
				}
			}

			// Notify step end.
			notifyStepEnd();
		}
	}

	/**
	 * Task adapter to receive and forward events to the scanner task.
	 */
	class ScanListener extends TaskHandler {
		/**
		 * Invoked to notify that the task has effectively been cancelled (after a cancel request). Used to notify
		 * listeners.
		 * 
		 * @param task The task.
		 */
		@Override
		public void cancelled(Task task) {
			FileCopy.this.notifyCancelled();
		}

		/**
		 * Invoked to know if task cancellation has been requested. Used to forward the request to the scanner where the
		 * listener is installed.
		 * 
		 * @param task The task.
		 * @return A boolean that indicates if task cancellation has been requested.
		 */
		@Override
		public boolean cancelRequested(Task task) {
			return FileCopy.this.cancelRequested();
		}

		/**
		 * Invoked to notify that the task has effectively been paused (after a pause request). Used to forward the
		 * notification to listeners.
		 * 
		 * @param task The task.
		 */
		@Override
		public void paused(Task task) {
			FileCopy.this.notifyPaused();
		}

		/**
		 * Invoked to know if the task has been requested to pause. In non indeterminate tasks, it would normally be
		 * invoked at every step. Used to forward the request to the scanner where the listener is installed.
		 * 
		 * @param task The task.
		 * @return A boolean indicating if the task has been requested to pause.
		 */
		@Override
		public boolean pauseRequested(Task task) {
			return FileCopy.this.pauseRequested();
		}

		/**
		 * Invoked to notify that the task has effectively been resumed (after a resume request). Used to forward the
		 * notification to listeners.
		 * 
		 * @param task The task.
		 */
		@Override
		public void resumed(Task task) {
			FileCopy.this.notifyResumed();
		}

		/**
		 * Invoked to know that after pausing, if resume has beeen requested. Used to forward the request to the scanner
		 * where the listener is installed.
		 * 
		 * @param task The task.
		 * @return A boolean that indicates if resumen has been requested.
		 */
		@Override
		public boolean resumeRequested(Task task) {
			return FileCopy.this.resumeRequested();
		}
	}
	
	/**
	 * Additional label to show the from file.
	 */
	private static final String labelFrom = "From";
	/**
	 * Additional label to show the to file.
	 */
	private static final String labelTo = "To";

	/**
	 * The map of source and destination directories.
	 */
	private Map<File, File> map = new LinkedHashMap<>();
	/**
	 * List of source files (sub-directories or files) to exclude from copy.
	 */
	private List<File> excludeCopy = new ArrayList<>();
	/**
	 * List of destination files (sub-directories or files) to exclude from countForPurge.
	 */
	private List<File> excludePurge = new ArrayList<>();

	/**
	 * A boolean that indicates whether sub-directories from the source should be processed.
	 */
	private boolean processSubDirectories = true;
	/**
	 * A boolean that indicates whether destination should be purged, that is, non existing files in the source deleted.
	 */
	private boolean purgeDestination = false;

	/**
	 * Counter for bytes to process.
	 */
	private long bytesToProcess;
	/**
	 * Counter for bytes processed.
	 */
	private long bytesProcessed;

	/**
	 * Constructor.
	 * 
	 * @param session The working session.
	 */
	public FileCopy(Session session) {
		super(session);
		addAdditionalLabel(labelFrom);
		addAdditionalLabel(labelTo);
	}
	
	/**
	 * Purge the destination file if not exists in the source.
	 * 
	 * @param destinationDirectory
	 * @param destinationFile
	 * @throws IOException
	 */
	private void purge(File destinationDirectory, File destinationFile) throws IOException {
		File sourceDirectory = getSourceDirectory(destinationDirectory);
		File sourceFile = FileUtils.getSourceFile(destinationDirectory, destinationFile, sourceDirectory);
		if (!sourceFile.exists()) {
			destinationFile.delete();
		}
	}

	/**
	 * Returns the source directory given the destination one, in the list of source-destination directories.
	 * 
	 * @param destinationDirectory The destination directory.
	 * @return The source dirfectory.
	 */
	private File getSourceDirectory(File destinationDirectory) {
		Iterator<File> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			File source = keys.next();
			File destination = map.get(source);
			if (destination.equals(destinationDirectory)) {
				return source;
			}
		}
		return null;
	}

	/**
	 * Add source and destination directories to be copied.
	 * 
	 * @param sourceDirectory The source directory.
	 * @param destinationDirectory The destination directory.
	 */
	public void addDirectories(File sourceDirectory, File destinationDirectory) {
		map.put(sourceDirectory, destinationDirectory);
	}

	/**
	 * Exclude the argument source file or directory from the copy process.
	 * 
	 * @param source The source to exclude.
	 */
	public void addExcludeCopy(File source) {
		if (!excludeCopy.contains(source)) {
			excludeCopy.add(source);
		}
	}

	/**
	 * Check whether the source file must be excluded from copy.
	 * 
	 * @param source The source file.
	 * @return A boolean.
	 */
	private boolean isExcludeCopy(File source) {
		for (File file : excludeCopy) {
			if (FileUtils.isParent(file, source)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the destination file must be excluded from countForPurge.
	 * 
	 * @param destination The destination file.
	 * @return A boolean.
	 */
	private boolean isExcludePurge(File destination) {
		for (File file : excludePurge) {
			if (FileUtils.isParent(file, destination)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Exclude the argument destination file or directory from the countForPurge process.
	 * 
	 * @param destination The destination to exclude.
	 */
	public void addExcludePurge(File destination) {
		if (!excludePurge.contains(destination)) {
			excludePurge.add(destination);
		}
	}

	/**
	 * Returns a boolean indicating whether sub-directories from the source should be processed.
	 * 
	 * @return A boolean.
	 */
	public boolean isProcessSubDirectories() {
		return processSubDirectories;
	}

	/**
	 * Sets a boolean indicating whether sub-directories from the source should be processed.
	 * 
	 * @param processSubDirectories A boolean.
	 */
	public void setProcessSubDirectories(boolean subDirectories) {
		this.processSubDirectories = subDirectories;
	}

	/**
	 * Returns a boolean that indicates whether destination should be purged.
	 * 
	 * @return A boolean.
	 */
	public boolean isPurgeDestination() {
		return purgeDestination;
	}

	/**
	 * Sets a boolean that indicates whether destination should be purged.
	 * 
	 * @param purgeDestination A boolean.
	 */
	public void setPurgeDestination(boolean purgeDestination) {
		this.purgeDestination = purgeDestination;
	}

	/**
	 * Count the number of steps.
	 * 
	 * @return The number of steps.
	 */
	@Override
	public long countSteps() {

		// Notify counting.
		notifyCounting();

		// Number of steps.
		long count = 0;

		// If should countForPurge, count to analyze countForPurge.
		if (isPurgeDestination()) {

			// The scanner to count the destination.
			FileScanner scanner = new FileScanner(getSession());
			Iterator<File> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				File source = keys.next();
				scanner.addSourceDirectory(map.get(source));
			}
			scanner.setScanSubDirectories(isProcessSubDirectories());

			// The counter listener.
			CountListener counterListener = new CountListener(true);
			scanner.addListener(counterListener);

			// The task listener.
			scanner.addListener(new ScanListener());

			// Do scan.
			new Thread(scanner, "File countForPurge counter").start();

			// Wait until terminated.
			while (!scanner.isTerminated()) {
				Thread.yield();
			}

			// Read steps.
			count += counterListener.getCount();
		}

		// The scanner to count the source.
		FileScanner scanner = new FileScanner(getSession());
		Iterator<File> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			File source = keys.next();
			scanner.addSourceDirectory(source);
		}
		scanner.setScanSubDirectories(isProcessSubDirectories());

		// The counter listener.
		CountListener counterListener = new CountListener(false);
		scanner.addListener(counterListener);

		// The task listener.
		scanner.addListener(new ScanListener());

		// Do scan.
		new Thread(scanner, "File counter").start();

		// Wait until terminated.
		while (!scanner.isTerminated()) {
			Thread.yield();
		}

		// Read steps.
		count += counterListener.getCount();

		// Notify.
		notifyStepCount(count);
		return getSteps();
	}

	/**
	 * Executes the copy task.
	 * 
	 * @throws Exception If an unrecoverable error occurs during execution.
	 */
	@Override
	public void execute() throws Exception {

		// Reset bytes to copy and copied.
		bytesToProcess = 0;
		bytesProcessed = 0;

		// Count steps.
		countSteps();

		// If required, the scanner to countForPurge destination.
		if (isPurgeDestination()) {
			
			// Clear labels.
			clearAdditionalLabels();

			// The scanner to countForPurge source.
			FileScanner scanner = new FileScanner(getSession());
			Iterator<File> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				File source = keys.next();
				scanner.addSourceDirectory(map.get(source));
			}
			scanner.setScanSubDirectories(isProcessSubDirectories());

			// The countForPurge listener.
			PurgeListener purgeListener = new PurgeListener();
			scanner.addListener(purgeListener);

			// The task listener.
			scanner.addListener(new ScanListener());

			// Do scan.
			new Thread(scanner, "File purger").start();

			// Wait until terminated.
			while (!scanner.isTerminated()) {
				Thread.yield();
			}
		}
		
		// Clear labels.
		clearAdditionalLabels();

		// The scanner to copy source.
		FileScanner scanner = new FileScanner(getSession());
		Iterator<File> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			File source = keys.next();
			scanner.addSourceDirectory(source);
		}
		scanner.setScanSubDirectories(isProcessSubDirectories());

		// The copier listener.
		CopyListener copyListener = new CopyListener();
		scanner.addListener(copyListener);

		// The task listener.
		scanner.addListener(new ScanListener());

		// Scan in the same thread.
		scanner.execute();
		
		// Clear labels.
		clearAdditionalLabels();
		
		// If there are files not copied, notify it with an error.
		if (!getExceptions().isEmpty()) {
			StringBuilder b = new StringBuilder();
			for (Exception exception : getExceptions()) {
				b.append("\n" + exception.getMessage());
			}
			throw new Exception(b.toString());
		}
	}

	/**
	 * Returns a boolean indicating whether the task will support cancel requests.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCancelSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task supports counting steps through a call to <code>countSteps()</code>.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isCountStepsSupported() {
		return true;
	}

	/**
	 * Returns a boolean indicating if the task is indeterminate, that is, the task can not count its number of steps.
	 * 
	 * @return A boolean indicating if the task is indeterminate.
	 */
	@Override
	public boolean isIndeterminate() {
		return false;
	}

	/**
	 * Returns a boolean indicating whether the task will support the pause/resume requests.
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean isPauseSupported() {
		return true;
	}

}
