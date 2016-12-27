package test.other;

import java.io.File;

import com.qtplaf.library.util.SystemUtils;

public class TestFiles {

	public static void main(String[] args) {
		try {
			File path = SystemUtils.getFileFromClassPathEntries("files");
			File file = new File(path,"network.txt");
			System.out.println(file);
			System.out.println(file.getAbsoluteFile());
			System.out.println(file.getCanonicalFile());
			System.out.println(file.getName());
			System.out.println(file.getParent());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
