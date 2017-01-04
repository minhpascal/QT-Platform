package test.com.msasc.library.util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import com.qtplaf.library.app.Session;
import com.qtplaf.library.util.file.FileScanner;
import com.qtplaf.library.util.file.FileScannerListener;

public class TestFileScanner {
	
	private static int countFiles = 0;
	private static int countDirectories = 0;
	private static long totalSize = 0;
	
	public static class Listener implements FileScannerListener {
		@Override
		public void file(File sourceDirectory, File file) {
			System.out.println(file);
			if (file.isFile()) {
				countFiles++;
				totalSize += file.length();
			}
			if (file.isDirectory()) {
				countDirectories++;
			}
		}
	}

	public static void main(String[] args) {
		Session session = new Session(Locale.UK);
		try {
			File source = new File("C:\\Development\\Eclipse-Workspaces\\Roca");
			FileScanner scanner = new FileScanner(session);
			scanner.addSource(source);
			scanner.addListener(new Listener());
			scanner.execute();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		System.out.println("Directories: "+countDirectories);
		System.out.println("Files: "+countFiles);
		System.out.println("Total size: "+totalSize);
		System.exit(0);
	}

}
