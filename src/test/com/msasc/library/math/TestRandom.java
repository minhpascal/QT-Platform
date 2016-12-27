package test.com.msasc.library.math;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;

public class TestRandom {

	public static void main(String[] args) {
		Random random = new Random();
		DoubleStream stream = random.doubles(10000, 0, 10);
		Iterator<Double> i = stream.iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}

}
