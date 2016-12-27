package test.com.msasc.library.util;

import java.io.File;

public class TestFilePath {

	public static void main(String[] args) {
		File sourcePath = new File("C:\\Development\\Eclipse Workspaces\\Roca\\cma-head\\CMA_Central\\mads\\module_margins_central");
		File sourceFile = new File("C:\\Development\\Eclipse Workspaces\\Roca\\cma-head\\CMA_Central\\mads\\module_margins_central\\bin\\xvr\\com\\app\\modules\\margins_central\\entity\\Backup.class");
		File destinationPath = new File("C:\\Development\\Eclipse Workspaces\\Roca\\mproject_development\\margins_central\\mads\\module_margins_central");
		
		String destinationName = sourceFile.getAbsolutePath().substring(sourcePath.getAbsolutePath().length());
		System.out.println(destinationName);
		File destinationFile = new File(destinationPath,destinationName);
		System.out.println(destinationFile);
	}
}
