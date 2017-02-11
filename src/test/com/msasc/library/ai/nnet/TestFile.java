package test.com.msasc.library.ai.nnet;

import java.io.File;

import com.qtplaf.library.util.SystemUtils;

public class TestFile {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		try {
			File path = SystemUtils.getFileFromClassPathEntries("files");
			File file = new File(path,"network.txt");
//			NeuralNetwork network = Persistence.readNetwork(file);
			System.out.println(path.getAbsolutePath());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
