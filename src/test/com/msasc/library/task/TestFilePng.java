package test.com.msasc.library.task;

import java.io.File;
import java.io.FileNotFoundException;

import com.qtplaf.library.util.SystemUtils;

public class TestFilePng {

	public static void main(String[] args) {
		try {
			File file = SystemUtils.getFileFromClassPathEntries("images/16/close.png");
			System.out.println(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
