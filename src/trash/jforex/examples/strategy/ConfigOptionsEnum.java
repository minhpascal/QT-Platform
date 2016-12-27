package trash.jforex.examples.strategy;

import com.dukascopy.api.Configurable;
import com.dukascopy.api.IAccount;
import com.dukascopy.api.IBar;
import com.dukascopy.api.IContext;
import com.dukascopy.api.IMessage;
import com.dukascopy.api.IStrategy;
import com.dukascopy.api.ITick;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.JFException;
import com.dukascopy.api.Period; 

/**
 * The example strategy shows how to use custom enums in remote run.
 *
 */
public class ConfigOptionsEnum implements IStrategy {
    
    private static final String BULLISH = "BULLISH";
    private static final String BEARISH = "BEARISH";
    
    enum Mode{
        BULLISH(ConfigOptionsEnum.BULLISH),
        BEARISH(ConfigOptionsEnum.BEARISH);
        
        private final String name;
        
        private Mode(String name){
            this.name = name;
        }
        
        private static Mode fromString(String name){
            for(Mode mode : Mode.values()){
                if(mode.name.equals(name)){
                    return mode;
                }
            }
            return null;
        }
    }

    @Configurable(value = "mode1", options = { BULLISH, BEARISH})
    public String mode1 = BULLISH;

    @Override
    public void onStart(IContext context) throws JFException {
        Mode mode = Mode.fromString(mode1);
        context.getConsole().getOut().println("chosen mode: " + mode);
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {}
 
    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {}

    @Override
    public void onMessage(IMessage message) throws JFException {}

    @Override
    public void onAccount(IAccount account) throws JFException {}

    @Override
    public void onStop() throws JFException {}

}
