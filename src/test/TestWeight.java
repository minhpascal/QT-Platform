package test;

import java.util.ArrayList;
import java.util.List;

public class TestWeight {

	public static void main(String[] args) {

	}

	private List<Double> weights(double weight, double incr, int times) {
		List<Double> weights = new ArrayList<>();
		for (int i = 0; i < times; i++) {
			if ( i == 0) {
				weights.add(weight);
				continue;
			}
		}
		return weights;
	}
}
