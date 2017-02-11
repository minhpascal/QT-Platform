package test.com.msasc.library.ai.nnet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

import com.qtplaf.library.util.SystemUtils;

public class CreateInitialWeights {

	public static void main(String[] args) throws Exception {
		int[] sizes = new int[] { 784, 100, 10 };
		int count = 0;
		for (int i = 1; i < sizes.length; i++) {
			int a = sizes[i-1];
			int b = sizes[i];
			count += (a * b);
		}
		File file = SystemUtils.getFileFromClassPathEntries("weights.txt");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		BufferedWriter buffer = new BufferedWriter(writer);
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			buffer.write(Double.toString(random.nextGaussian()));
			buffer.newLine();
		}
		buffer.close();
	}

}
