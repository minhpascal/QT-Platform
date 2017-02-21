package test;

import com.qtplaf.library.ai.nnet.function.output.Gaussian;
import com.qtplaf.library.ai.nnet.function.output.Sigmoid;
import com.qtplaf.library.util.NumberUtils;

public class TestFunction {

	public static void main(String[] args) {
		
		double[] in = new double[]{
			-1.0, -0.9, -0.8, -0.7, -0.6, -0.5, -0.4, -0.3, -0.2, -0.1, 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
		
		for (int i = 0; i < in.length; i++) in[i] = in[i] * 10;
		
		Gaussian gaussian = new Gaussian();
		double[] outg = new double[in.length];
		for (int i = 0; i < in.length; i++) outg[i] = gaussian.getOutput(in[i]);

		Sigmoid sigmoid = new Sigmoid();
		double[] outs = new double[in.length];
		for (int i = 0; i < in.length; i++) outs[i] = sigmoid.getOutput(in[i]);
		
		print(in, outg, outs);

	}
	private static void print(double[] in, double[] outg, double[] outs) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < in.length; i++) {
			if (i > 0) {
				b.append("\n");
			}
			if (in[i] >= 0) {
				b.append(" ");
			}
			b.append(NumberUtils.getBigDecimal(in[i], 1).toPlainString());
			b.append("     ");
			b.append(NumberUtils.getBigDecimal(outg[i], 5).toPlainString());
			b.append("     ");
			b.append(NumberUtils.getBigDecimal(outs[i], 5).toPlainString());
			b.append("     ");
			b.append(NumberUtils.getBigDecimal((outs[i]*2.0)-1.0, 5).toPlainString());
		}
		System.out.println(b);
	}
}
