package trash.jforex.examples.strategy.indicators;

import java.io.File;
import java.util.Arrays;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

@RequiresFullAccess
public class CalculateArbitraryIndicator implements IStrategy {

	private IConsole console;
	private IIndicators indicators;
	@Configurable(value = "Custom indicator .jfx file", description = "Only for custom indicators. For platform indicators leave it empty.")
	public File indFile;
	@Configurable("Indicator name")
	public String indName = "ALLIGATOR";
	@Configurable("Instrument")
	public Instrument instrument = Instrument.EURUSD;
	@Configurable("period")
	public Period period = Period.ONE_MIN;
	@Configurable("AppliedPrice")
	public AppliedPrice appliedPrice = AppliedPrice.CLOSE;
	@Configurable("OfferSide")
	public OfferSide side = OfferSide.BID;

	public void onStart(IContext context) throws JFException {
		this.console = context.getConsole();
		this.indicators = context.getIndicators();

		// load from file if there is such
		if (indFile != null && indFile.exists()) {
			indName = indicators.registerCustomIndicator(indFile);
		}

		IIndicator indicator = indicators.getIndicator(indName);

		// retrieve indicator metadata
		IndicatorInfo info = indicator.getIndicatorInfo();
		int inputCount = info.getNumberOfInputs();
		int optInputCount = info.getNumberOfOptionalInputs();
		int outputCount = info.getNumberOfOutputs();
		print("inputCount=%s, optInputCount=%s, outputCount=%s", inputCount, optInputCount, outputCount);
		print(indicator.getInputParameterInfo(0).getType());

		// 1 - input related parameters

		// assume close price for all inputs
		AppliedPrice[] inputTypes = new AppliedPrice[inputCount];
		Arrays.fill(inputTypes, appliedPrice);

		// assume bid side for all inputs
		OfferSide[] offerSides = new OfferSide[inputCount];
		Arrays.fill(offerSides, side);

		// 2 - optional input related parameters
		Object[] optParams = new Object[optInputCount];
		for (int i = 0; i < optInputCount; i++) {
			optParams[i] = indicator.getOptInputParameterInfo(i).getDescription().getOptInputDefaultValue();
			print("Set default opt input: %s=%s", indicator.getOptInputParameterInfo(i).getName(), optParams[i]);
		}

		int shift = 0;
		Object[] outputs = indicators.calculateIndicator(instrument, period, offerSides, indName, inputTypes, optParams, shift);

		// 3 - process outputs

		print("indicator outputs: ");
		for (int i = 0; i < outputCount; i++) {
			OutputParameterInfo outputInfo = indicator.getOutputParameterInfo(i);
			String outputName = outputInfo.getName();
			if (outputInfo.getType() == OutputParameterInfo.Type.DOUBLE) {
				print("%s=%.7f", outputName, (Double) outputs[i]);
			} else if (outputInfo.getType() == OutputParameterInfo.Type.INT) {
				print("%s=%s", outputName, outputs[i]);
			} else {
				print("%s type is Object - %s, which needs customized processing.", outputName, outputs[i].getClass());
			}
		}

	}

	private void print(String format, Object... args) {
		print(String.format(format, args));
	}

	private void print(Object message) {
		console.getOut().println(message);
	}

	public void onAccount(IAccount account) throws JFException {	}
	public void onMessage(IMessage message) throws JFException {	}
	public void onStop() throws JFException {	}
	public void onTick(Instrument instrument, ITick tick) throws JFException {	}
	public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {	}
}