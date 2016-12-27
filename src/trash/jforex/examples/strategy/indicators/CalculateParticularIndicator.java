package trash.jforex.examples.strategy.indicators;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.*;
import com.dukascopy.api.IIndicators.AppliedPrice;

@RequiresFullAccess
public class CalculateParticularIndicator implements IStrategy {

    private IConsole console;
    private IIndicators indicators;
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

        IIndicator indicator = indicators.getIndicator(indName); 
        
        //1 - input related parameters - we make arrays of 1 element since we have 1 input
        AppliedPrice[] inputTypes = new AppliedPrice[] {appliedPrice};
        OfferSide[] offerSides = new OfferSide[] {side};
        
        //2 - optional inputs - we have three of them - Jaw Time Period, Teeth Time Period, Lips Time Period. Use default values.
        Object[] optParams = new Object[] {13, 8, 5};
        
        int shift = 0;
        Object[] outputs = indicators.calculateIndicator(instrument, period, offerSides, indName, inputTypes, optParams, shift);
        
        //3 - outputs - we have 3 outputs and all of them are of type DOUBLE
        for(int i = 0; i < 3; i++){
            String outputName = indicator.getOutputParameterInfo(i).getName();
            print("%s=%.5f", outputName, (Double)outputs[i]);
        }       
    }
    
    private void print(String format, Object...args){
        print(String.format(format, args));
    }

    private void print(Object message) {
        console.getOut().println(message);
    }
    public void onAccount(IAccount account) throws JFException {    }
    public void onMessage(IMessage message) throws JFException {    }
    public void onStop() throws JFException {    }
    public void onTick(Instrument instrument, ITick tick) throws JFException {    }
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {    }
}