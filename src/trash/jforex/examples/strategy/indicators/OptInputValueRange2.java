package trash.jforex.examples.strategy.indicators;

import java.util.Arrays;

import com.dukascopy.api.*;
import com.dukascopy.api.indicators.*;

@RequiresFullAccess
public class OptInputValueRange2 implements IStrategy {

    private IConsole console;
    private IIndicators indicators;

    @Configurable("Indicator name")
    public String indName = "BBANDS";

    public void onStart(IContext context) throws JFException {
        this.console = context.getConsole();
        this.indicators = context.getIndicators();      
        
        IIndicator indicator = indicators.getIndicator(indName);        
        IndicatorInfo info = indicator.getIndicatorInfo();
        int optInputCount = info.getNumberOfOptionalInputs();
        
        for(int i = 0; i < optInputCount; i++){
            OptInputParameterInfo optInfo = indicator.getOptInputParameterInfo(i);
            OptInputDescription descr = optInfo.getDescription();
            String rangeInfoStr = "";
            if(descr instanceof IntegerRangeDescription){
                IntegerRangeDescription d = ((IntegerRangeDescription)descr);
                rangeInfoStr = String.format("min=%s, max=%s", d.getMin(), d.getMax());
            } else if (descr instanceof DoubleRangeDescription){
                DoubleRangeDescription d = ((DoubleRangeDescription)descr);
                rangeInfoStr = String.format("min=%.5f, max=%.5f", d.getMin(), d.getMax());
            } else if (descr instanceof BooleanOptInputDescription){
                rangeInfoStr = ""; //boolean does not have no range
            } else if (descr instanceof IntegerListDescription){
                IntegerListDescription d = ((IntegerListDescription)descr);
                rangeInfoStr = String.format("names=%s, values=%s", Arrays.asList(d.getValueNames()), Arrays.toString(d.getValues()));
            } else if (descr instanceof DoubleListDescription){
                DoubleListDescription d = ((DoubleListDescription)descr);
                rangeInfoStr = String.format("names=%s, values=%s", Arrays.asList(d.getValueNames()), Arrays.toString(d.getValues()));
            }
            print("Opt input: %s, range: %s, default= %s",optInfo.getName(), rangeInfoStr, descr.getOptInputDefaultValue());
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