package test.com.msasc.library.trading.data.indicators;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class TestGaussianFitter {

	public static void main(String[] args) {
		WeightedObservedPoints obs = new WeightedObservedPoints();
		obs.add(0, 1);
		obs.add(1, 2);
		obs.add(2, 4);
		obs.add(3, 8);
		obs.add(4, 16);
		obs.add(5, 32);
		obs.add(6, 64);
		obs.add(7, 128);
		obs.add(8, 256);
		obs.add(9, 512);

//		obs.add(0, 1);
//		obs.add(1, -3);
//		obs.add(2, 1);
//		obs.add(3, -3);
//		obs.add(4, 1);
//		obs.add(5, -3);
//		obs.add(6, 1);
//		obs.add(7, -3);
//		obs.add(8, 1);
//		obs.add(9, -3);

		GaussianCurveFitter fitter = GaussianCurveFitter.create();
		double[] params = fitter.fit(obs.toList());

		Gaussian.Parametric function = new Gaussian.Parametric();
		for (int x = 0; x < 10; x++) {
			double y = function.value(x, params);
			System.out.println(y);
		}
	}

}
